package blufeedmebt;


import com.intel.bluetooth.BlueCoveImpl;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

/**
 *
 * @author David
 */
public class DispositivoEnvioBT extends Thread {
    private static BlockingQueue<String> cola = new LinkedBlockingQueue<String>();
    private Semaphore semaforoEnviosMultiples;
    private String idDispositivo;
    public static final Logger LOGGER = Logger.getLogger("blufeedmebt.DispositivoEnvioBT");

    public DispositivoEnvioBT(String idDispositivo){
        this.idDispositivo = idDispositivo;
        this.LOGGER.setLevel(Level.ALL);
    }

    public static boolean addOperacionEnvio(String url) {
        try {
            if (!cola.contains(url)) {
                cola.put(url);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Inicializa la pila del Bluetooth devolviendo el identificador de dicha pila para su posterior uso
     * @param deviceId
     * @return El identificador de dicha pila
     */
    private Object iniciarPilaBT(String idDispositivo) {
        Object devId = null;
        BlueCoveImpl.setConfigProperty("bluecove.deviceID", idDispositivo);

        try {
            LocalDevice.getLocalDevice();
        } catch (BluetoothStateException e2) {
            //Si la pila ya ha sido inicializada lanzará una excepción aunque no pase nada
            this.LOGGER.log(Level.OFF, e2.getMessage());
        } finally {
            try {
                //Inicializa la pila si no lo ha hecho y devuelve el identificador que se utilizará en otros hilos para el acceso a la misma pila.
                devId = BlueCoveImpl.getThreadBluetoothStackID();
            } catch (BluetoothStateException e) {
                this.LOGGER.log(Level.SEVERE, e.getMessage());
                return null;
            }
        }

        int maxDispositivos = Integer.valueOf(LocalDevice.getProperty("bluetooth.connected.devices.max"));
        this.LOGGER.log(Level.INFO, "bluetooth.connected.devices.max= {0}", maxDispositivos);
        
        semaforoEnviosMultiples = new Semaphore(maxDispositivos);
        
        return devId;
    }

    @Override
    public void run() {
        this.LOGGER.log(Level.INFO, "Corriendo hebra DispositivoBThebra para dispositivo: {0}", idDispositivo);

        //Inicializamos la pila
        Object devId = iniciarPilaBT(idDispositivo);
        
        while (true) {
            try {
                //Determina si otro envío puede iniciarse, sino esperera a que finalice alguno
                semaforoEnviosMultiples.acquireUninterruptibly();
                String url = cola.take();

                //Lanzamos una nueva hebra para el envío
                Thread s2d = new Envio2dispositivo(url, idDispositivo, semaforoEnviosMultiples, devId);
                s2d.start();
            } catch (Exception e) {
                e.printStackTrace();
                semaforoEnviosMultiples.release();
                System.exit(-4);
            }
        }
    }
}
