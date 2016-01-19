package blufeedmebt;

import com.intel.bluetooth.BlueCoveImpl;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
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

/**
 *
 * @author sephierot-linux
 */
public class DispositivoBusquedaBT extends Thread implements DiscoveryListener {

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
    //Indica que unicamente se buscara dicho servicio
    UUID[] serviciosAbuscar = new UUID[]{OBEX_PUSH};
    //IMPORTANTE el tamaño maximo no puede modificarse una vez reservado.INICIALIZAR A getProperty("bluetooth.sd.attr.retrievable.max")
    //No permitimos que mas de una hebra añada al mismo tiempo un dispositivo encontrado
    private static ArrayBlockingQueue<RemoteDevice> vecDispositivos = new ArrayBlockingQueue<RemoteDevice>(100);
    private static ArrayBlockingQueue<String> vecServicios = new ArrayBlockingQueue<String>(100);
    //Cerrojos para la hebra en ejecucion para la busqueda de dispositivos y servicios
    //private Object lock_dispositivos = new Object();
    //private Object lock_servicios = new Object();
    Semaphore maxServiceSearchesSemaphore = null;
    Semaphore scanningSemaphore = new Semaphore(1);
    //Buscador de nuestra hebra
    private DiscoveryAgent buscador;
    private String deviceId;
    //Numero maximo de busquedas en paralelo de servicios
    private int maxBusqServicios;
    //Numero maximo de dispositivos conectados
    private int maxConexionesDispositivos;
    public static Logger LOGGER = Logger.getLogger("blufeedmebt.DispositivoBusquedaBT");

    public DispositivoBusquedaBT(String deviceId) throws BluetoothStateException {
        maxServiceSearchesSemaphore = new Semaphore(Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.trans.max")));
        this.buscador = LocalDevice.getLocalDevice().getDiscoveryAgent();
        this.deviceId = deviceId;
        this.LOGGER.setLevel(Level.ALL);
    }

    @Override
    public void run() {
        try {//Establecemos el id del dispositivo que manejara la hebra
            BlueCoveImpl.setConfigProperty("bluecove.deviceID", deviceId);
            LocalDevice.getLocalDevice();
            this.maxBusqServicios = Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.trans.max"));
            this.maxConexionesDispositivos = Integer.parseInt(LocalDevice.getProperty("bluetooth.connected.devices.max"));

            //LOGGER.log(Level.INFO, "Para el dispositivo{0}   bluetooth.sd.trans.max={1}    bluetooth.connected.devices.max={2}", new Object[]{deviceId, this.maxConexionesDispositivos, this.maxBusqServicios});
            System.out.println("Para el dispositivo " + deviceId + " bluetooth.sd.trans.max=" + this.maxConexionesDispositivos + "    bluetooth.connected.devices.max=" + this.maxBusqServicios);

            //LOGGER.log(Level.SEVERE, ex.getMessage());

        } catch (Exception e) {
            //Obtendremos una excepcion cuando la pila ya esté inicializada pero si capturamos la excepcion no importa
            LOGGER.log(Level.WARNING, e.getMessage());

            try {
                Object devId = BlueCoveImpl.getThreadBluetoothStackID();
                BlueCoveImpl.setThreadBluetoothStackID(devId);
            } catch (Exception e2) {
                LOGGER.log(Level.WARNING, "Error: La pila ya está inicializada. Se continúa de todas formas");
            }
        }

        try {
            while (true) {
                scanningSemaphore.acquire();
                buscador.startInquiry(DiscoveryAgent.GIAC, this);
                LOGGER.log(Level.INFO, "Comienzo del Inquiry de la pila {0}", deviceId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al iniciar el Inquiry de la pila {0}", deviceId);
            System.exit(-1);
        }
    }

    @Override
    /**
     * Metodo que se llama cada vez que se detecta un dispositivo
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        /*PARA COMPROBAR SI ES UN MOVIL
        int PHONE_CLASS_DEVICE= 512;
        int majorDeviceClass = cod.getMajorDeviceClass();
        if (PHONE_CLASS_DEVICE == majorDeviceClass){}*/

        try {
            LOGGER.log(Level.FINEST, "Dispositivo detectado por {0}:{1}  Mac:{2}", new Object[]{this.deviceId, btDevice.getFriendlyName(true), btDevice.getBluetoothAddress()});
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }

        //Si existe en la BD lo insertamos y sino no porque no está registrado el dispositivo
        if (true) {
            //Al tratarse de una cola con sincronizacion no hace falta sincronizar entre los procesos
            //Se verifica que no este añadido por otra hebra
            if (!vecDispositivos.contains(btDevice)) {
                vecDispositivos.add(btDevice);
            }
        }
    }

    @Override
    public void inquiryCompleted(int discType) {
        switch (discType) {
            case DiscoveryListener.INQUIRY_COMPLETED:
                LOGGER.log(Level.INFO, "Busqueda de dispositivos completada con normalidad");

                try {
                    System.out.println("Inquiry completa para la hebra:"+this.getId());
                    //Cogemos el dispositivo de la cabeza de los que se disponen y se le realiza una busqueda de servicios
                    RemoteDevice dispositivoElegido = vecDispositivos.poll();

                     while (dispositivoElegido != null){
                        maxServiceSearchesSemaphore.acquire();
                        int transId = buscador.searchServices(null /*atributos*/, serviciosAbuscar, dispositivoElegido, this);
                        LOGGER.log(Level.INFO, "Transaccion de busqueda de servicios iniciada con -> {0} con id: {1}", new Object[]{dispositivoElegido.getBluetoothAddress(), transId});
                    }
                    scanningSemaphore.release();
                }catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                    System.exit(-1);
                }
                break;

            case DiscoveryListener.INQUIRY_TERMINATED:
                LOGGER.log(Level.WARNING, "Busqueda de dispositivos terminada");
                break;

            case DiscoveryListener.INQUIRY_ERROR:
                LOGGER.log(Level.WARNING, "Error en la busqueda de dispositivos");
                break;

            default:
                LOGGER.log(Level.WARNING, "Error en la busqueda de dispositivos, codigo de respuesta de dispositivo desconocido");
                break;
        }
    }

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        String servicio = "";
        LOGGER.log(Level.INFO, "Servicio descubierto en la hebra con transID:{0}", transID);

        //Para obtener el nombre del servicio si se especifico en el inquiry
        //DataElement serviceName = servRecord[i].getAttributeValue(SERVICE_NAME);

        System.out.println("Servicio descubierto para la hebra:"+this.getId()+"  , tamaño del vector:"+servRecord.length);
        for (int i = 0; i < servRecord.length; i++) {
            servicio = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

            if (servicio == null) {
                continue;
            }

            //Si no esta ya añadido el servicio lo agregamos
            if (!vecServicios.contains(servicio)) {
                LOGGER.log(Level.FINEST, "Servicio ObexPush encontrado en {0}", servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
                vecServicios.add(servicio);
            } else {
                LOGGER.log(Level.INFO, "Servicio ObexPush encontrado en {0} pero ya insertado", servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
            }

            return;
        }
        LOGGER.log(Level.WARNING, "Ignorando el servicio{0}:no soporta el servicio requerido", servicio);
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        maxServiceSearchesSemaphore.release();

        switch (respCode) {
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                LOGGER.log(Level.WARNING, "Busqueda de servicios completada con normalidad. transId={0}", transID);
                break;

            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                LOGGER.log(Level.WARNING, "Busqueda de servicios terminada. transId= {0}", transID);
                break;

            case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                LOGGER.log(Level.WARNING, "Dispositivo no alcanzable para el servicio. transId= {0}", transID);
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
    }
}
