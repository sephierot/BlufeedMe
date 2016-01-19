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

import blufeedme.modelo.Categoria;
import blufeedme.modelo.Dispositivo;
import blufeedme.basedatos.impl.BdBlufeedmeMySql;
import blufeedmebt.eventos.EventoDispositivo;
import blufeedmebt.eventos.ListenerDispositivo;
import com.intel.bluetooth.BlueCoveImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.swing.event.EventListenerList;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class DispositivoBT extends Thread implements DiscoveryListener {

    //Define el pin por defecto de conexion para dispositivos que no hayan sido registrados
    public static final String PIN_DEFAULT = "0000";
    //Define la categoria de noticias que se mostraran en pantalla y por tanto se enviaran a todos los dispositivos
    public static final Long CATEGORIA_PANTALLA = new Long(1);
    public static final UUID L2CAP = new UUID(0x0100);
    public static final UUID RFCOMM = new UUID(0x0003);
    public static final UUID OBEX = new UUID(0x0008);
    public static final UUID OBEX_PUSH = new UUID(0x1105);
    public static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);
    public static final int SERVICE_RECORD_HANDLE = 0x000;
    public static final int SERVICE_CLASS_ID_LIST = 0x0001;
    public static final int SERVICE_RECORD_STATE = 0x0002;
    public static final int SERVICE_ID = 0x0003;
    public static final int PROTOCOL_DESCRIPTOR_LIST = 0x0004;
    public static final int SERVICE_NAME = 0x0100;
    UUID[] serviciosAbuscar;                //Indica que unicamente se buscara dicho servicio
    //No necesitamos controlar la concurrencia sobre este map porque se autosincroniza con las hebras
    //El put no puede tener ningun valor null, por eso se utiliza la cadena vacia ""
    private ConcurrentHashMap<RemoteDevice, String> mapDispositivosURL;
    private HashMap<String, String> vectdispositivos;   //MAC,PIN
    private DiscoveryAgent buscador;        //Buscador de nuestra hebra
    private String deviceId;
    private int maxBusqServicios;           //Numero maximo de busquedas en paralelo de servicios
    private int maxConexionesDispositivos;  //Numero maximo de dispositivos conectados
    private Semaphore semaforoMaxBusqServ;
    private Semaphore semaforoMaxConexiones;
    private Semaphore semaforoEscaneo;
    private AtomicInteger dispositivosRestantesBusq = new AtomicInteger(0); //Indica el numero de dispositivos que quedan por buscarle los servicios
    private File fichero_html;
    private File fichero_bd_prop;
    private BdBlufeedmeMySql baseDatos = new BdBlufeedmeMySql();     //Base de datos (una conexion por hebra o dispositivo de emision)
    private boolean terminar = false;
    //Para notificar de los eventos que se generen
    protected EventListenerList listenerList = new EventListenerList();
    public static final Logger LOGGER = Logger.getLogger("blufeedmebt.DispositivoBT");

    /**
     * Constructor de la clase
     * @param deviceId Identificador del dispositivo bluetooth local que será utilizado
     * @param servicios Servicios bluetoth que serán utilizados
     * @param fichero_bd_prop Fichero de propiedades para la conexión con la base de datos
     * @param fichero_html Fichero de propiedades que contiene la plantilla a utilizar para la generación de ficheros html a enviar
     */
    public DispositivoBT(String deviceId, UUID[] servicios, File fichero_bd_prop, File fichero_html) {
        this.deviceId = deviceId;
        this.serviciosAbuscar = servicios;
        this.mapDispositivosURL = new ConcurrentHashMap<RemoteDevice, String>();
        this.vectdispositivos = new HashMap<String, String>();
        this.maxBusqServicios = Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.trans.max"));
        this.maxConexionesDispositivos = Integer.parseInt(LocalDevice.getProperty("bluetooth.connected.devices.max"));
        this.fichero_bd_prop = fichero_bd_prop;
        this.fichero_html = fichero_html;

        //Establecemos el nombre de la hebra
        this.setName("Escaneo_" + deviceId);

        this.semaforoMaxBusqServ = new Semaphore(this.maxBusqServicios);//Bloqueo para el max de busquedas de servicios simultaneos
        this.semaforoMaxConexiones = new Semaphore(this.maxConexionesDispositivos);//Bloqueo para el max de conexiones simultaneas
        this.semaforoEscaneo = new Semaphore(1);                        //Bloqueo para una sola hebra


        //Establecemos el nivel de detalle del log
        LOGGER.setLevel(Level.ALL);

        try {
            this.buscador = LocalDevice.getLocalDevice().getDiscoveryAgent();
        } catch (BluetoothStateException ex) {
            LOGGER.log(Level.SEVERE, "Error al obtener dispositivo local. Informacion de la excepcion:", ex);
            System.exit(-1);
        }

        //Inicializamos la conexion con la Base de datos
        if (!this.baseDatos.connect(this.fichero_bd_prop)) {
            LOGGER.log(Level.SEVERE, "ERROR AL CONECTAR A LA BASE DE DATOS, hebra id={0}", this.getId());
            System.exit(-1);
        }
    }

    /**
     * Establece las propiedades de la pila bluetooth, indicadas en un fichero
     * @param url_fichero fichero donde se almacenan las propiedades
     * @see BlueCoveImpl.setConfigProperty Propiedades disponibles para la pila en Bluecove
     */
    public void setPropiedadesBluecove(File url_fichero) throws IOException {
        FileInputStream fis = new FileInputStream(url_fichero);
        Properties cfg = new Properties();

        /*
         * PROPERTY_OBEX_TIMEOUT                The amount of time in milliseconds for which the implementation will attempt to successfully transmit a packet before it throws InterruptedIOException. Defaults to 2 minutes.
         * PROPERTY_LOCAL_DEVICE_ADDRESS        If Stack support multiple bluetooth adapters select one by its bluetooth address. (Linux BlueZ and Emulator) Initialization property.
         * PROPERTY_INQUIRY_DURATION_DEFAULT
         * PROPERTY_INQUIRY_DURATION            Device Inquiry time in seconds defaults to 11 seconds.
         * PROPERTY_CONNECT_UNREACHABLE_RETRY   On MS stack retry connection automatically when received WSAENETUNREACH during connect.
         * PROPERTY_CONNECT_TIMEOUT             The amount of time in milliseconds for which the implementation will attempt to establish connection RFCOMM or L2CAP before it throws BluetoothConnectionException.
         */

        cfg.load(fis);
        Enumeration<Object> keys = cfg.keys();
        while (keys.hasMoreElements()) {
            String k = (String) keys.nextElement();
            BlueCoveImpl.setConfigProperty(k, cfg.getProperty(k));
        }
    }

    /**
     * Método que es llamado por cada detección de un dispositivo nuevo durante el Inquiry de dispositivos
     * @param btDevice Dispositivo que ha sido encontrado
     * @param cod Tipo de dispositivo descubierto (teléfono móvil, pc etc)
     */
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        /*PARA COMPROBAR SI ES UN MOVIL
        int PHONE_CLASS_DEVICE= 512;
        int majorDeviceClass = cod.getMajorDeviceClass();
        if (PHONE_CLASS_DEVICE == majorDeviceClass){}*/

        try {
            Dispositivo disp_encontrado = this.baseDatos.getDispositivo(btDevice.getBluetoothAddress());
            String nombre = btDevice.getFriendlyName(true);
            LOGGER.log(Level.INFO, "Dispositivo detectado por {0}.\tNombre:{1}  Mac:{2}", new Object[]{this.deviceId, nombre, btDevice.getBluetoothAddress()});

            //Si estaba en la BD
            if (disp_encontrado != null) {
                LOGGER.log(Level.INFO, "Dispositivo añadido:{0}  Mac:{1}   URL:{2}", new Object[]{btDevice.getFriendlyName(true), btDevice.getBluetoothAddress(), disp_encontrado.getURL_servicio()});
                //Añadimos el dispositivo y su URL del servicio si ya la tenemos en la BD
                //El put no puede tener ningun valor null, por eso se utiliza la cadena vacia ""
                if (disp_encontrado.getURL_servicio() == null) {
                    this.mapDispositivosURL.put(btDevice, "");
                } else {
                    this.mapDispositivosURL.put(btDevice, disp_encontrado.getURL_servicio());
                }

                //Notificamos a todos los listener
                for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                    l.dispositivoEncontrado(new EventoDispositivo(this, disp_encontrado, nombre, EventoDispositivo.DISPOSITIVO_REGISTRADO_ENCONTRADO));
                }

                //Añadimos el dispositivo aunque no tenga actualizado el URL del servicio porque nos interesa su pin
                this.vectdispositivos.put(disp_encontrado.getMac(), disp_encontrado.getPin());

            } else {//Si no esta registrado en la BD lo añadimos y le asociamos la categoria de pantalla
                ArrayList cats_pantalla = new ArrayList<Categoria>();
                cats_pantalla.add(this.baseDatos.getCategoria(DispositivoBT.CATEGORIA_PANTALLA));
                Dispositivo disp_no_insertado = new Dispositivo(Dispositivo.ID_NULL, btDevice.getBluetoothAddress(), DispositivoBT.PIN_DEFAULT, cats_pantalla, "");

                //Añadimos el dispositivo encontrado y que no está en la BD
                LOGGER.log(Level.INFO, "Dispositivo no registrado en el sistema, se le enviarán únicamente las noticias importantes.");

                if (this.baseDatos.insert(disp_no_insertado)) {
                    LOGGER.log(Level.INFO, "El dispositivo no registrado MAC:{0} se ha añadido a la BD", disp_no_insertado.getMac());

                    //Notificamos a todos los listener
                    for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                        //Creamos un dispositivo con la unica informacion del nombre y la mac porque no está registrado
                        l.dispositivoEncontrado(new EventoDispositivo(this, disp_no_insertado, nombre, EventoDispositivo.DISPOSITIVO_ENCONTRADO));
                    }

                    //Añadimos el dispositivo aunque no tenga actualizado el URL del servicio porque nos interesa su pin
                    this.vectdispositivos.put(disp_no_insertado.getMac(), disp_no_insertado.getPin());
                } else {
                    LOGGER.log(Level.WARNING, "No se ha podido insertar en la BD el dispositivo no registrado MAC:{0}", disp_no_insertado.getMac());
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Fallo al obtener el FriendlyName del dispositivo, Informacion de la excepcion:", ex);
        }
    }

    /**
     * Método que es llamado cuando finaliza el Inquiry de dispositivos
     * @param discType Tipo de finalización del Inquiry: INQUIRY_COMPLETED, INQUIRY_TERMINATED, INQUIRY_ERROR
     */
    @Override
    public void inquiryCompleted(int discType) {
        boolean sinTrabajo = true;

        switch (discType) {
            case DiscoveryListener.INQUIRY_COMPLETED:
                Set<RemoteDevice> dispositivos = this.mapDispositivosURL.keySet();

                //Contamos el numero de servicios nuevos que tendremos que buscar
                for (Iterator<RemoteDevice> c = dispositivos.iterator(); c.hasNext();) {
                    if (this.mapDispositivosURL.get(c.next()).isEmpty()) {
                        this.dispositivosRestantesBusq.incrementAndGet();
                    }
                }

                //Recorremos todos los dispositivos que se disponen y se le realiza una busqueda de servicios
                //(unicamente si no han sido ya encontrados). Para la busqueda se crea una hebra (hasta el maximo permitido) por lo que continuaría con
                //la busqueda del siguiente dispositivo concurrentemente
                for (Iterator<RemoteDevice> i = dispositivos.iterator(); i.hasNext();) {
                    RemoteDevice dispositivoElegido = i.next();

                    //Buscamos sus servicios unicamente si no lo hemos hecho nunca (URL igual a cadena vacia)
                    if (this.mapDispositivosURL.get(dispositivoElegido).isEmpty()) {
                        //Obtenemos permiso para ejecutar hebra para busqueda de servicios, sino quedará en espera hasta que haya un hueco libre
                        semaforoMaxBusqServ.acquireUninterruptibly();

                        try {
                            int transId;
                            transId = buscador.searchServices(null, serviciosAbuscar, dispositivoElegido, this);
                            LOGGER.log(Level.INFO, "............ Transaccion de busqueda de servicios iniciada con -> {0} con id: {1} ............", new Object[]{dispositivoElegido.getBluetoothAddress(), transId});

                            //Indicamos que no se realice ningun release de la hebra que busca los dispositivos para que se bloquee
                            //cuando intente realizar la siguiente busqueda y no sea desbloqueada hasta que termine la ultima hebra de servicios
                            //si hubiera excepcion no pasaría nada porque no se llegaría a asignar el valor
                            sinTrabajo = false;
                        } catch (BluetoothStateException ex) {
                            LOGGER.log(Level.WARNING, "Error al realizar la busqueda de servicios. Informacion de la excepcion:", ex);
                        }
                    }
                }
                LOGGER.log(Level.INFO, "Busqueda de dispositivos completada con normalidad");
                break;

            case DiscoveryListener.INQUIRY_TERMINATED:
                LOGGER.log(Level.WARNING, "Busqueda de dispositivos terminada por la aplicacion y no ha sido completada");
                break;

            case DiscoveryListener.INQUIRY_ERROR:
                LOGGER.log(Level.WARNING, "Error en la busqueda de dispositivos");
                break;

            default:
                LOGGER.log(Level.WARNING, "Error en la busqueda de dispositivos, codigo de respuesta de dispositivo desconocido");
                break;
        }

        /*IMPORTANTE:la hebra que busca dispositivos no puede volver a buscarlos mientras no se hayan
        finalizado el resto de hebras abiertas por ella para la busqueda de servicios
        por tanto el permiso para continuar (release) se lo tiene que dar la ultima hebra de busqueda de servicios
        al finalizar.
        Si se da el caso de que no hay ningún dispositivo no habrá servicios que buscar y por tanto
        se quedará bloqueada en la proxima busqueda si no se libera, otro caso sería que si haya dispositivos pero como ya sabemos sus servicios
        tampoco se buscan y por tanto tambien se crearía bloqueo*/
        if (sinTrabajo) {
            semaforoEscaneo.release();
        }

        LOGGER.log(Level.INFO, "............ Finalizacion del escaneo de dispositivos ............");
    }

    /**
     * Método que es llamado por cada detección de un servicio nuevo durante la búsqueda de servicios en un dispositivo
     * @param transID Identificador de la transacción
     * @param servRecord Array de servicios que han sido descubiertos
     */
    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        String url_servicio = "";
        //LOGGER.log(Level.INFO, "............ Servicio descubierto para la hebra:{0}  con transID:{1} , tamaño del vector de servicios:{2} ............", new Object[]{this.getId(), transID, servRecord.length});

        //Para obtener el nombre del servicio si se especifico en el inquiry
        //DataElement serviceName = servRecord[i].getAttributeValue(SERVICE_NAME);

        for (int i = 0; i < servRecord.length; i++) {
            url_servicio = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

            //Seguimos buscando en el vector
            if (url_servicio == null) {
                continue;
            }

            LOGGER.log(Level.INFO, "............ Servicio ObexPush encontrado en {0} ............", url_servicio);

            //Buscamos en el map para ver si el servicio del dispositivo estaba registrado en la BD anteriormente
            if (this.mapDispositivosURL.get(servRecord[i].getHostDevice()).isEmpty()) {
                LOGGER.log(Level.INFO, "Servicio ObexPush añadido a la BD porque no estaba previamente");

                //Actualizamos la BD con la URL del servicio para el dispositivo
                this.baseDatos.updateURL(servRecord[i].getHostDevice().getBluetoothAddress(), url_servicio);

                //Actualizamos el map actual
                this.mapDispositivosURL.replace(servRecord[i].getHostDevice(), url_servicio);

                Dispositivo disp_encontrado_servicio = this.baseDatos.getDispositivo(servRecord[i].getHostDevice().getBluetoothAddress());

                //Notificamos a todos los listener
                for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                    String nombreDisp = "";
                    try {
                        nombreDisp = servRecord[i].getHostDevice().getFriendlyName(true);
                    } catch (IOException ex) {
                        LOGGER.log(Level.WARNING, "Fallo al obtener el FriendlyName del dispositivo. Informacion de la excepcion:", ex);
                    }
                    l.servicioEncontrado(new EventoDispositivo(this, disp_encontrado_servicio, nombreDisp, EventoDispositivo.SERVICIO_ENCONTRADO));
                }
            } else {
                LOGGER.log(Level.INFO, "Servicio ObexPush estaba previamente en la BD");
            }

            return;
        }
        LOGGER.log(Level.WARNING, "Ignorando el servicio{0}:no soporta el servicio requerido", url_servicio);
    }

    /**
     *
     * Método que es llamado cuando finaliza el Inquiry de dispositivos
     * @param transID Identificador de la transacción finalizada
     * @param respCode Tipo de respuesta tras la finalización de la búsqueda: SERVICE_SEARCH_COMPLETED, SERVICE_SEARCH_TERMINATED, SERVICE_SEARCH_DEVICE_NOT_REACHABLE, SERVICE_SEARCH_NO_RECORDS, SERVICE_SEARCH_ERROR
     */
    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        //Aumentamos en uno el semaforo, permitiendo que otra hebra pueda buscar servicios
        semaforoMaxBusqServ.release();

        switch (respCode) {
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                LOGGER.log(Level.INFO, "Busqueda de servicios completada con normalidad. transId={0}", transID);
                break;

            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                LOGGER.log(Level.WARNING, "Busqueda de servicios terminada por la aplicacion y no ha sido completada. transId= {0}", transID);
                break;

            case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                LOGGER.log(Level.WARNING, "Dispositivo no alcanzable para el servicio indicado. transId= {0}", transID);
                break;

            case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                LOGGER.log(Level.WARNING, "No se encontraron registros de servicio. transId= {0}", transID);
                break;

            case DiscoveryListener.SERVICE_SEARCH_ERROR:
                LOGGER.log(Level.WARNING, "Error en la busqueda de servicios. transId= {0}", transID);
                break;

            default:
                LOGGER.log(Level.WARNING, "Codigo de respuesta de servicio desconocido. transId= {0}", transID);
                break;
        }

        //Decrementamos el numero de dispositivos a los que tenemos que buscarle los servicios.
        //Tanto si hay error como no, si se trata de la ultima hebra que busca servicios que ha invocado este metodo
        //tenemos que despertar a la hebra propia de esta clase que estaba buscando dispositivos
        if (this.dispositivosRestantesBusq.decrementAndGet() == 0) {
            //Liberamos el semaforo para permitir la proxima busqueda
            semaforoEscaneo.release();
        }
    }

    /**
     * Método que lanza las operaciones necesarias para comenzar la búsqueda de dispositivos y posteriormente los servicios de cada uno de ellos
     */
    private void BusquedaDispositivosServicios() {
        //Limpiamos los vectores de dispositivos encontrados
        this.vectdispositivos.clear();
        this.mapDispositivosURL.clear();

        try {
            //Obtenemos el permiso para realizar la busqueda mientras no haya otra busqueda en funcionamiento
            this.semaforoEscaneo.acquireUninterruptibly();
            LOGGER.log(Level.INFO, "............ Comienzo del escaneo de dispositivos ............");
            buscador.startInquiry(DiscoveryAgent.GIAC, this);

            //Notificamos a todos los listener
            for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                l.nuevaBusqueda(new EventoDispositivo(this, null, "", EventoDispositivo.NUEVA_BUSQUEDA));
            }
        } catch (BluetoothStateException ex) {
            LOGGER.log(Level.SEVERE, "Error al iniciar el Inquiry de la pila " + deviceId + ". Informacion de la excepcion:", ex);
            if (this.semaforoEscaneo.tryAcquire()) {
                this.semaforoEscaneo.release();
            }
        }
    }

    /**
     * Finaliza la ejecución de la hebra
     */
    public void Terminar() {
        this.terminar = true;
    }

    /**
     * Método principal de la hebra DispositivoBT
     */
    @Override
    public void run() {
        while (!this.terminar) {
            this.BusquedaDispositivosServicios();

            //Obtenemos otro permiso mas para bloquear hasta que termine el inquiry
            this.semaforoEscaneo.acquireUninterruptibly();

            ArrayList<EnviarAdispositivo> hebras = new ArrayList<EnviarAdispositivo>();

            //Enviamos las noticias para todos los dispositivos que hemos encontrado a nuestro alcance durante el Inquiry
            for (Iterator<Entry<RemoteDevice, String>> i = this.mapDispositivosURL.entrySet().iterator(); i.hasNext();) {
                Entry<RemoteDevice, String> dispositivoEnvio = i.next();

                //Comenzamos el envio unicamente si hemos obtenido o teniamos su URL
                if (!dispositivoEnvio.getValue().isEmpty()) {
                    EnviarAdispositivo envio = new EnviarAdispositivo(dispositivoEnvio.getKey(), dispositivoEnvio.getValue(), this.vectdispositivos.get(dispositivoEnvio.getKey().getBluetoothAddress()), this.deviceId, this.fichero_bd_prop, this.fichero_html, this.semaforoMaxConexiones);

                    //Asinamos todos los listener que tengamos asociados actualmente tambien a las hebras de envio para que puedan notificar tambien
                    for (ListenerDispositivo l : this.listenerList.getListeners(ListenerDispositivo.class)) {
                        envio.addListenerDispositivos(l);
                    }

                    //Establecemos el nombre de la hebra
                    envio.setName("Envio_" + dispositivoEnvio.getKey().getBluetoothAddress());

                    //Añadimos la hebra al vector de hebras para poder esperarla posteriormente
                    hebras.add(envio);

                    //Obtenemos permiso para ejecutar la hebra si no se supera el limite maximo de hebras de envio posibles
                    semaforoMaxConexiones.acquireUninterruptibly();
                    envio.start();

                    LOGGER.log(Level.INFO, "Iniciada la hebra de envio {0}", envio.getName());
                }
            }

            //Establecemos mayor prioridad al resto de hebras que a esta propia para que terminen antes
            yield();

            //Esperamos hasta que todas las hebras hayan terminado
            for (int i = 0; i < hebras.size(); i++) {
                try {
                    LOGGER.log(Level.INFO, "Esperando que acabe la hebra {0}", hebras.get(i).getName());
                    hebras.get(i).join();
                    LOGGER.log(Level.INFO, "Continua la ejecución tras la espera de la hebra {0}", hebras.get(i).getName());
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, "Error al realizar el join para esperar la hebra {0}", hebras.get(i).getName());
                }
            }

            //Ya se puede liberar el semaforo para que se realice una nueva busqueda
            this.semaforoEscaneo.release();
        }

        //Liberamos la conexion con la base de datos
        this.baseDatos.disconnect();
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
