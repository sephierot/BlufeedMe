/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebaobex;

import com.intel.bluetooth.RemoteDeviceHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;

/**
 *
 * @author david
 */
public class Main {

    final static UUID[] SERVICIOS = {
        //ListenerDispositivosBT.OBEX
        //ListenerDispositivosBT.OBEX_FILE_TRANSFER
        //ListenerDispositivosBT.L2CAP
        //ListenerDispositivosBT.RFCOMM
        //ListenerDispositivosBT.OBEX
        ListenerDispositivosBT.OBEX_PUSH
    };
    final static int[] ATRIBUTOS = {
        ListenerDispositivosBT.SERVICE_NAME
    //ListenerDispositivosBT.SERVICE_ID
    //ListenerDispositivosBT.SERVICE_RECORD_HANDLE
    //ListenerDispositivosBT.SERVICE_CLASS_ID_LIST
    //ListenerDispositivosBT.SERVICE_RECORD_STATE
    //ListenerDispositivosBT.PROTOCOL_DESCRIPTOR_LIST
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        //Obtenemos nuestro dispositivo Bluetooth local
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        //Establecemos como visible nuestro dispositivo
        localDevice.setDiscoverable(DiscoveryAgent.GIAC);

        boolean esRequeridoUsuario = true;
        boolean esAccesoTotal = true;


        File fichero = new File("/home/sephierot-linux/prueba.html");
        FileInputStream streamFichero = new FileInputStream(fichero);
        byte[] datosFichero = new byte[(int) fichero.length()];
        streamFichero.read(datosFichero);
        String nombreFichero = fichero.getName();


        System.out.println("Nombre dispositivo local: " + localDevice.getFriendlyName() + "  MAC:" + localDevice.getBluetoothAddress());

        //Obtenemos el buscador de nuestro dispositivo
        DiscoveryAgent buscador = localDevice.getDiscoveryAgent();
        ListenerDispositivosBT listenerBT = new ListenerDispositivosBT();

        listenerBT.clearDispositivos();
        listenerBT.clearServicios();


        synchronized (listenerBT.lock_devices) {
            boolean comenzado = buscador.startInquiry(DiscoveryAgent.GIAC, listenerBT);
            if (comenzado) {
                System.out.println("Buscando Dispositivos... ");
                listenerBT.lock_devices.wait();
            }
        }

        if (listenerBT.getVecDispositivos().size() <= 0) {
            System.out.println("No se han encontrado dispositivos.");
        } else {
            for (RemoteDevice dispositivoRemoto : ListenerDispositivosBT.vecDispositivos) {
                System.out.println("Comenzada busqueda de servicios en: " + dispositivoRemoto.getBluetoothAddress());

                synchronized (listenerBT.lock_services) {
                    buscador.searchServices(null, SERVICIOS, dispositivoRemoto, listenerBT);
                    listenerBT.lock_services.wait();
                }
            }
        }


        //Ruta del dispositivo y del servicio
        String serverURL = listenerBT.vecServicios.get(0).getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
        RemoteDevice device = listenerBT.vecDispositivos.get(0);

        System.out.println("Sincronizando con " + serverURL);

        try {
            //setCursorWait();
            boolean rc = RemoteDeviceHelper.authenticate(device, "1234");
            System.out.println("authenticate returns: " + rc);
        } catch (IOException e) {
            System.out.println("can't authenticate:" + e.getMessage());
        } catch (Throwable e) {
            System.out.println("authenticate error:" + e.getMessage());
        } finally {
            //setCursorDefault();
        }
        System.out.println(device.getFriendlyName(false) + " Autenticado:" + device.isAuthenticated());
        System.out.println(device.getFriendlyName(false) + " De confianza:" + device.isTrustedDevice());


        boolean timeouts = true;
        ClientSession clientSession=null;
        try {
            System.out.println("Conectando con el dispositivo " + serverURL);
            
            clientSession = (ClientSession) Connector.open(serverURL, Connector.READ_WRITE, timeouts);

            //clientSession.setAuthenticator(new AuthenticatorOBEX("clienteSephierot"));

            System.out.println("Conectado con " + serverURL);
            /*HeaderSet hsConnect = clientSession.createHeaderSet();

            //Creamos la cabecera de autentificacion
            hsConnect.createAuthenticationChallenge("OBEX-Con-Auth-Test", esRequeridoUsuario, esAccesoTotal);

            System.out.println("ANTES");
            HeaderSet hsConnectReply = clientSession.connect(hsConnect);


            if (hsConnectReply.getResponseCode() == ResponseCodes.OBEX_HTTP_OK) {
                System.out.println("Autenticacion con exito");
            } else {
                System.out.println("Fallo en la autenticacion, codigo:"+ hsConnectReply.getResponseCode());
            }

            System.out.println("DESPUES");*/

            //Creamos la cabecera para la operacion
            HeaderSet hsOperation = clientSession.createHeaderSet();

            clientSession.connect(hsOperation);
            
            hsOperation.setHeader(HeaderSet.NAME, nombreFichero);
            hsOperation.setHeader(HeaderSet.DESCRIPTION, "");
            hsOperation.setHeader(HeaderSet.TYPE, "text/html");
            hsOperation.setHeader(HeaderSet.WHO, "UGR".getBytes());
            hsOperation.setHeader(HeaderSet.NAME, nombreFichero);
            hsOperation.setHeader(HeaderSet.LENGTH, new Long(datosFichero.length));

            Operation po = clientSession.put(hsOperation);

            OutputStream os = po.openOutputStream();
            os.write(datosFichero);
            os.close();

            System.out.println("put responseCode " + po.getResponseCode());

            HeaderSet receivedHeaders = po.getReceivedHeaders();
            String description = (String) receivedHeaders.getHeader(HeaderSet.DESCRIPTION);
            if (description != null) {
                System.out.println("Description " + description);
            }

            po.close();

            HeaderSet hsd = clientSession.disconnect(null);
            System.out.println("disconnect responseCode " + hsd.getResponseCode());

            System.out.println("Conexion finalizada");
        } catch (IOException e) {
            System.out.println("Communication error: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (clientSession != null) {
                clientSession.close();
            }
        }







        /*       ClientSession clientSession = (ClientSession) Connector.open(serverURL, Connector.READ_WRITE);
        //HeaderSet hsAutenticacion = clientSession.createHeaderSet();
        
        //clientSession.setAuthenticator(new AuthenticatorOBEX("SEPHIEROT2"));
        
        hsAutenticacion.createAuthenticationChallenge("OBEX-Con-Auth-Test", esRequeridoUsuario, esAccesoTotal);


        HeaderSet hsOperation = clientSession.createHeaderSet();

        hsOperation.setHeader(HeaderSet.NAME, nombreFichero);
        hsOperation.setHeader(HeaderSet.DESCRIPTION, "Acepta la noticia");
        hsOperation.setHeader(HeaderSet.TARGET, "uooooo".getBytes());
        hsOperation.setHeader(HeaderSet.TYPE, "text/plain");
        hsOperation.setHeader(HeaderSet.WHO, "UGR".getBytes());
        hsOperation.setHeader(HeaderSet.COUNT, new Long(1));
        hsOperation.setHeader(HeaderSet.LENGTH, new Long(datosFichero.length));

        if (clientSession.connect(hsOperation).getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
        System.out.println("Fallo al conectar.  " + hsOperation.getResponseCode());
        clientSession.close();
        return;
        }

        //Create PUT Operation
        Operation putOperation = clientSession.put(hsOperation);
        OutputStream os = putOperation.openOutputStream();
        os.write(datosFichero);
        os.flush();
        os.close();

        putOperation.close();
        clientSession.disconnect(null);
        clientSession.close();*/
    }
}
