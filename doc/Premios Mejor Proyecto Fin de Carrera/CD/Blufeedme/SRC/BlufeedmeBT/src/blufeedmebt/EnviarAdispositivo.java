/*
 *
 * Copyright 2010 David Armenteros Escabias, Ángel Daniel Sanjuán Espejo.
 *
 * This file is part of BluFeedMe.
 *
 * BluFeedMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package blufeedmebt;

import blufeedme.modelo.Dispositivo;
import blufeedme.modelo.Noticia;
import blufeedme.basedatos.impl.BdBlufeedmeMySql;
import blufeedmebt.eventos.EventoDispositivo;
import blufeedmebt.eventos.ListenerDispositivo;
import com.intel.bluetooth.RemoteDeviceHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.swing.event.EventListenerList;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class EnviarAdispositivo extends Thread {

    private ClientSession clientSession;
    private HeaderSet cabeceraOperacion;
    private HeaderSet cabeceraDesconectar;
    private Operation putOperation;
    private OutputStream streamSalida;
    private RemoteDevice dispositivo;
    private String URL_Servicio;
    private String deviceId;
    private String PIN;
    private String MAC;
    private Semaphore maxConexiones;
    private File fichero_html;
    private File fichero_bd_prop;
    private static final int MAX_NOTICIAS = 5;          //Maximo numero de noticias
    private static final int MAX_TAM_NOTICIAS = 10000;  //tamaño maximo del cuerpo de las noticias en caracteres que se transmitiran
    private static int N_NOTICIAS_ENVIADAS;
    private BdBlufeedmeMySql baseDatos = new BdBlufeedmeMySql();     //Base de datos (una conexion por hebra o dispositivo de emision)
    //Para notificar de los eventos que se generen
    protected EventListenerList listenerList = new EventListenerList();
    public static final Logger LOGGER = Logger.getLogger("blufeedmebt.EnviarAdispositivo");

    /**
     * Constructor de la hebra
     * @param dispositivo Dispositivo de destino que recibirá el fichero
     * @param URL_Servicio URL del servicio del dispositivo de destinnno que recibirá el fichero
     * @param PIN Pin del dispositivo de destino que recibirá el fichero
     * @param deviceId ID del dispositivo fisico Bluetooth instalado en la máquina local
     * @param fichero_bd_prop Fichero con las propiedades de conexión con la base de datos
     * @param fichero_html Fichero plantilla con el código html que será insertado en el fichero a enviar
     * @param maxConexiones Número máximo de conexiones simultáneas permitidas por el dispositivo bluetooth
     */
    public EnviarAdispositivo(RemoteDevice dispositivo, String URL_Servicio, String PIN, String deviceId, File fichero_bd_prop, File fichero_html, Semaphore maxConexiones) {
        this.dispositivo = dispositivo;
        this.URL_Servicio = URL_Servicio;
        this.PIN = PIN;
        this.maxConexiones = maxConexiones;
        this.deviceId = deviceId;
        this.MAC = dispositivo.getBluetoothAddress();
        N_NOTICIAS_ENVIADAS = 0;
        this.fichero_bd_prop = fichero_bd_prop;
        this.fichero_html = fichero_html;

        //Inicializamos la conexion con la Base de datos
        if (!this.baseDatos.connect(this.fichero_bd_prop)) {
            LOGGER.log(Level.SEVERE, "ERROR AL CONECTAR A LA BASE DE DATOS, hebra id={0}", this.getId());
            System.exit(-1);
        }
    }

    /**
     * Realiza el pairing y la conexión entre el dispositivo bluetooth y el dispositivo de destino
     * @return Booleano indicando si ha habido éxito en la conexión o no
     */
    private boolean ConectarConDispositivo() {
        if (!RemoteDeviceHelper.implIsAuthenticated(this.dispositivo)) {
            try {
                if (!RemoteDeviceHelper.authenticate(this.dispositivo, this.PIN)) {
                    LOGGER.log(Level.WARNING, "Error al realizar el Pairing con el dispositivo MAC:{0}", this.MAC);
                    return false;
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Error al realizar el Pairing con el dispositivo MAC:{0}", this.MAC);
                return false;
            }
        }

        LOGGER.log(Level.INFO, "El pairing con el dispositivo MAC:{0} se ha realizado con exito", this.MAC);

        try {
            clientSession = (ClientSession) Connector.open(this.URL_Servicio, Connector.READ_WRITE, true);
            //Creamos la cabecera para la operacion
            this.cabeceraOperacion = clientSession.createHeaderSet();

            try {
                if (this.clientSession.connect(cabeceraOperacion).getResponseCode() == ResponseCodes.OBEX_HTTP_OK) {
                    LOGGER.log(Level.INFO, "Conectado con el dispositivo MAC:{0}", this.MAC);
                    return true;
                } else {
                    LOGGER.log(Level.WARNING, "No se ha podido conectar con el dispositivo MAC:{0}", this.MAC);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Error al conectar con el dispositivo MAC:" + this.MAC, ex);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error al abrir la conexion con el servicio URL:" + this.URL_Servicio, ex);
        }

        return false;
    }

    /**
     * Realiza el envío de un fichero con el nombre y datos indicados como parámetros
     * @param noticias Cadena que contiene el fichero completo que será enviado al dispositivo de destino
     * @param titulo Título del fichero a enviar
     * @return Booleano indicando si se ha realizado el envío del fichero correctamente
     */
    private boolean EnviarDatos(StringBuilder noticias, String titulo) {
        byte bytes[] = null;

        try {
            bytes = noticias.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.WARNING, "Error al realizar el Encoding UTF-8 para el dispositivo MAC:{0}", this.MAC);
            return false;
        }

        this.cabeceraOperacion.setHeader(HeaderSet.NAME, titulo);
        //this.cabeceraOperacion.setHeader(HeaderSet.DESCRIPTION, "");
        this.cabeceraOperacion.setHeader(HeaderSet.TYPE, "text/html");
        this.cabeceraOperacion.setHeader(HeaderSet.LENGTH, new Long(bytes.length));

        try {
            //Ejecutamos el put
            this.putOperation = clientSession.put(cabeceraOperacion);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error al realizar el PUT en el dispositivo MAC:{0}", this.MAC);
            return false;
        }

        try {
            this.streamSalida = this.putOperation.openOutputStream();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error al abrir el stream en el dispositivo MAC:{0}", this.MAC);
            return false;
        }
        try {
            this.streamSalida.write(bytes);
            this.streamSalida.flush();
            this.streamSalida.close();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error al realizar el write en el dispositivo MAC:{0}", this.MAC);
            return false;
        }

        try {
            if (this.putOperation.getResponseCode() == ResponseCodes.OBEX_HTTP_OK) {
                return true;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error al obtener el codigo de respuesta en el dispositivo MAC:{0}", this.MAC);
        }

        return false;
    }

    /**
     * Realiza la desconexión entre el dispositivo bluetooth local y el dispositivo de destino
     */
    private void DesconectarConDispostivo() {
        if (this.streamSalida != null) {
            try {
                this.streamSalida.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "No se ha podido cerrar el stream de salida con el dispositivo MAC:{0}", this.MAC);
            }
        }

        if (this.putOperation != null) {
            try {
                this.putOperation.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "No se ha podido cerrar la operacion con el dispositivo MAC:{0}", this.MAC);
            }
        }

        if (this.clientSession != null) {
            try {
                this.cabeceraDesconectar = this.clientSession.disconnect(null);
                LOGGER.log(Level.INFO, "Desconexion con dispositivo MAC:{0} {1}", new Object[]{this.MAC, this.cabeceraDesconectar.getResponseCode()});
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "No se ha podido desconectar la sesion con el dispositivo MAC:{0}", this.MAC);
            }
            try {
                this.clientSession.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "No se ha podido cerrar la sesion con el dispositivo MAC:{0}", this.MAC);
            }
        }

        this.cabeceraOperacion = null;
        this.cabeceraDesconectar = null;
    }

    /**
     * Compone un mensaje formateado en HTML, formado por la lista de noticias indicada
     * @param noticias Lista de noticias que serán insertadas en el mensaje creado
     * @return Cadena con todo el mensaje generado
     */
    private StringBuilder ComponerMensajeHTML(ArrayList<Noticia> noticias) {
        FileInputStream fr_html = null;

        int tam_suma = 0;

        try {
            fr_html = new FileInputStream(this.fichero_html);
            byte[] bufferHtml = new byte[(int) this.fichero_html.length()];

            //Leemos el fichero html
            fr_html.read(bufferHtml);

            StringBuilder html = new StringBuilder(new String(bufferHtml, "UTF-8"));
            SimpleDateFormat f = new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm");

            /*Ejemplo de fichero generado
            <div class="noticia">
            <div class="titulo">
            <h1>Titulo de la noticia</h1>
            <div class="fecha">12/23/2010</div>
            </div>
            <div class="cuerpo">
            <h2>Subtitulo de la noticia</h2>
            <div class="categoria">Categoría:Dep. de Arquitectura de los computadores</div>
            <div class="autor">Escrito por:asf</div>
            <p>Se ha abierto un plazo preferente para solicitar la adaptación a los nuevos grados hasta el 29 de Julio de 2010.  E-mail de contacto: jbernier@ugr.es</p>
            </div>
            </div>*/

            //Contamos el maximo de noticias que vamos a transmitir segun su tamaño
            for (int i = 0; (i < noticias.size() && (tam_suma < MAX_TAM_NOTICIAS) && (N_NOTICIAS_ENVIADAS < MAX_NOTICIAS)); i++) {
                Noticia n = noticias.get(i);

                html.append("<div class=\"noticia\"><div class=\"titulo\"><h1>");
                html.append(n.getTitulo());
                html.append("</h1><div class=\"fecha\">");
                html.append(f.format(n.getFecha().getTime()));
                html.append("</div></div><div class=\"cuerpo\"><h2>");
                html.append(n.getSubtitulo());
                html.append("</h2><div class=\"categoria\">");
                html.append(this.baseDatos.getCategoria(n.getIdCategoria()).getNombre());
                html.append("</div><div class=\"autor\">");
                html.append(n.getAutor());
                html.append("</div><p>");
                html.append(n.getTexto());
                html.append("</p></div></div>");

                tam_suma += n.getTexto().length();

                //Aumentamos el numero de noticias que enviaremos
                N_NOTICIAS_ENVIADAS++;
            }
            //Cerramos el html
            html.append("</body></html>");

            return html;
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                fr_html.close();
            } catch (IOException ex) {
                Logger.getLogger(EnviarAdispositivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método principal de la hebra EnviarAdispositivo
     */
    @Override
    public void run() {
        //Obtenemos las primeras MAX_NOTICIAS a enviar al dispositivo
        ArrayList<Noticia> noticias = this.baseDatos.getNoticiasNoEnviadas(this.MAC, MAX_NOTICIAS);

        if (noticias != null && noticias.size() > 0) {
            //Creamos un dispositivo con algunos de los datos que nos interesan (faltaria el id y sus categorías)
            Dispositivo disp = this.baseDatos.getDispositivo(MAC);
            String nombreDisp = "";

            try {
                nombreDisp = this.dispositivo.getFriendlyName(true);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Fallo al obtener el FriendlyName del dispositivo", ex);
            }

            //Notificamos a todos los listener
            for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                l.noticiasAenviar(new EventoDispositivo(this, disp, nombreDisp, EventoDispositivo.NOTICIAS_A_ENVIAR, noticias));
            }

            //Conectamos con el dispositivo
            if (ConectarConDispositivo()) {
                //Utilizamos StringBuilder porque es un objeto y no se realiza una copia al pasarlo por parametro
                //ademas es mas eficiente que strigBuffer porque no esta sincronizado
                StringBuilder html = ComponerMensajeHTML(noticias);

                if (html != null) {
                    SimpleDateFormat f = new SimpleDateFormat("dd_MM_yy.HH_mm");
                    //Indicamos como nombre del fichero: UGR_+ultima fecha de publicacion de la ultima noticia entregada
                    if (EnviarDatos(html, f.format(noticias.get(N_NOTICIAS_ENVIADAS - 1).getFechaPubli().getTime()) + ".html")) {
                        //Tenemos que confirmar aquellas noticias que se hayan enviado con exito (unicamente las que haya elegido ComponerMensajeHTML)
                        //NOTA: se podria haber comprobado que todas se han confirmado correctamente
                        for (int i = 0; i < N_NOTICIAS_ENVIADAS; i++) {
                            this.baseDatos.ConfirmarEnvioNoticia(this.MAC, noticias.get(i).getId());
                        }

                        //Notificamos a todos los listener
                        for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                            l.noticiasEnviadas(new EventoDispositivo(this, disp, nombreDisp, EventoDispositivo.NOTICIAS_ENVIADAS, noticias, N_NOTICIAS_ENVIADAS));
                        }

                        LOGGER.log(Level.INFO, "{0} noticias enviadas correctamente y confirmadas para el dispositivo MAC:{1}", new Object[]{N_NOTICIAS_ENVIADAS, this.MAC});
                    }
                } else {
                    LOGGER.log(Level.WARNING, "No se ha podido obtener el código HTML de las noticias");
                }
            }

            //Liberamos todos los recursos de la conexion que se hayan inicializado
            DesconectarConDispostivo();
        } else {
            LOGGER.log(Level.INFO, "No hay noticias disponibles para el dispositivo MAC:{0}", this.MAC);
        }

        //Liberamos la conexion con la base de datos
        this.baseDatos.disconnect();

        //Liberamos una hebra de conexion para envio de datos
        this.maxConexiones.release();
    }

    /**
     * Registra un nuevo listener en la hebra para que sea notificado por ésta de todos los eventos ocurridos durante la ejecución
     * @param listener Listener que será notificado de los eventos
     */
    public void addListenerDispositivos(ListenerDispositivo listener) {
        listenerList.add(ListenerDispositivo.class, listener);
    }

    /**
     * Elimina el registro del listener realizado previamente por addListernerDispositivos() en la hebra
     * @param listener Listener que será eliminado de la lista de listener registrados
     */
    public void removeListenerDispositivos(ListenerDispositivo listener) {
        listenerList.remove(ListenerDispositivo.class, listener);
    }
}
