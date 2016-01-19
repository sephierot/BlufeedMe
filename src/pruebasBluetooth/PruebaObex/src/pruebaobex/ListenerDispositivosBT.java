package pruebaobex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 * Class that discovers all bluetooth devices in the neighbourhood
 * and displays their name and bluetooth address.
 */
public class ListenerDispositivosBT implements DiscoveryListener {
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


    //Objetos para funcionar de cerrojo
    public static final Object lock_devices = new Object();
    public static final Object lock_services = new Object();

    //Vector con los dispositivos que hay a nuestro alcance
    public static ArrayList<RemoteDevice> vecDispositivos = new ArrayList<RemoteDevice>();
    public static ArrayList<ServiceRecord> vecServicios = new ArrayList<ServiceRecord>();

    /**
     * Metodo que se llama cada vez que se detecta un dispositivo
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        try {
            System.out.println("Dispositivo detectado: " + btDevice.getFriendlyName(true) + "  Mac:" + btDevice.getBluetoothAddress());
        } catch (IOException ex) {
            Logger.getLogger(ListenerDispositivosBT.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Añadimos el dispositivo si no está ya
        if (!vecDispositivos.contains(btDevice)) {
            vecDispositivos.add(btDevice);
        }
    }

    /**
     * This callback method will be called when the device discovery is
     * completed.
     */
    public void inquiryCompleted(int discType) {
        synchronized (lock_devices) {
            lock_devices.notifyAll();
        }

        switch (discType) {
            case DiscoveryListener.INQUIRY_COMPLETED:
                System.out.println("Busqueda de dispositivos completada con normalidad");
                break;

            case DiscoveryListener.INQUIRY_TERMINATED:
                System.out.println("Busqueda de dispositivos terminada");
                break;

            case DiscoveryListener.INQUIRY_ERROR:
                System.out.println("Error en la busqueda de dispositivos");
                break;

            default:
                System.out.println("Codigo de respuesta de dispositivo desconocido");
                break;
        }
    }

    /**
     * Metodo que se llama cada vez que se detecta un servicio de un dispositvo
     */
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        for (int i = 0; i < servRecord.length; i++) {
            //String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

            //Añadimos el servicio encontrado
            vecServicios.add(servRecord[i]);
            DataElement serviceName = servRecord[i].getAttributeValue(SERVICE_NAME);

            if (serviceName != null) {
                System.out.println("Servicio con nombre:" + serviceName.getValue() + " encontrado en " + servRecord[i].getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, true));
            } else {
                System.out.println("Servicio encontrado en " + servRecord[i].getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, true));
            }
        }
    }


    /**
     * Metodo que se llama cuando finaliza la busqueda de servicios
     * @param transID
     * @param respCode
     */
    public void serviceSearchCompleted(int transID, int respCode) {
        synchronized (lock_services) {
            lock_services.notifyAll();
        }

        switch (respCode) {
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                System.out.println("Busqueda de servicios completada con normalidad");
                break;

            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                System.out.println("Busqueda de servicios terminada");
                break;

            case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                System.out.println("Dispositivo no alcanzable para el servicio");
                break;

            case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                System.out.println("No se encontraron registros de servicio");
                break;

            case DiscoveryListener.SERVICE_SEARCH_ERROR:
                System.out.println("Error en la busqueda de servicios");
                break;

            default:
                System.out.println("Codigo de respuesta de servicio desconocido");
                break;
        }
    }

    public static void clearDispositivos(){
        vecDispositivos.clear();
    }

    public static void clearServicios(){
        vecServicios.clear();
    }

    public static ArrayList<RemoteDevice> getVecDispositivos() {
        return vecDispositivos;
    }

    public static ArrayList<ServiceRecord> getVecServicios() {
        return vecServicios;
    }
}
