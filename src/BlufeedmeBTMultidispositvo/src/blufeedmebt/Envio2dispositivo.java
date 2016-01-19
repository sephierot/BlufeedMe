package blufeedmebt;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




import com.intel.bluetooth.BlueCoveImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

/**
 *
 * @author sephierot-linux
 */
public class Envio2dispositivo extends Thread{
    private ClientSession clientSession;
    private HeaderSet hsConnectReply;
    private HeaderSet hsOperation;
    private Operation putOperation;
    private String deviceId;
    private Semaphore sem;
    private String url;
    private Object devId;

    public Envio2dispositivo(String url, String deviceId, Semaphore sem, Object devId) {
        this.deviceId = deviceId;
        this.sem = sem;
        this.url = url;
        this.devId = devId;
    }

    private void initBtStack() {
        BlueCoveImpl.setThreadBluetoothStackID(devId);
        /*
        BlueCoveImpl.setConfigProperty("bluecove.deviceID", deviceId);
        try {
        LocalDevice.getLocalDevice();
        } catch (BluetoothStateException e2) {
        // we get exception when stack was already initialized. Let just
        attach to it
        e2.printStackTrace();
        System.err.println("-----continue anyway------------");
        try {
        Object devId = BlueCoveImpl.getThreadBluetoothStackID();
        BlueCoveImpl.setThreadBluetoothStackID(devId);
        } catch (Exception e) {
        e.printStackTrace();
        System.err.println("-----continue anyway--!!-----");
        //System.exit(-10);
        }
        }*/
    }

    @Override
    public void run() {
        String threadName = this.getName();
        System.err.println("running Send2DeviceThread " + threadName + " on device " + deviceId);
        try {
            initBtStack();
            System.out.println("Sending from " + threadName + " to " + url);
            int res = send();
            System.out.println("Sending from " + threadName + " to " + url
                    + " ... done: " + res);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-25);
        } finally {
            try {
                BlueCoveImpl.releaseThreadBluetoothStack();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sem.release();
        }
    }

    public int send() {
        String mimeType = "x-application/java";
        String fileToSend = "/tmp/z.jar";
        File f2s = new File(fileToSend);
        int res;
        if (0 != (res = openConnection(url, f2s.getName(), mimeType))) {
            System.err.println("Cant open connection " + res);
            return res;
        }
        try {// Send file to the server
            OutputStream os = putOperation.openOutputStream();
            sendFile(f2s, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -975;
        } finally {
            closeConection();
        }
        return 0;
    }

    private int openConnection(String deviceUrl, String fileToSend,
            String mimeType) {
        try {
            clientSession = (ClientSession) Connector.open(deviceUrl);
            hsConnectReply = clientSession.connect(null);
            if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                System.err.println("Failed to connect");
                return -1;
            }
            hsOperation = clientSession.createHeaderSet();
            hsOperation.setHeader(HeaderSet.NAME, fileToSend);
            hsOperation.setHeader(HeaderSet.TYPE, mimeType);
            //Create PUT Operation
            putOperation = clientSession.put(hsOperation);
        } catch (Exception e) {
            e.printStackTrace();
            closeConection();
            // BluetoothStackBluez.java , line ~550
            // Failed to connect [115] Operation now in progress
            // Failed to connect [111] Connecction refused
            if (e.getMessage().contains("115")) {
                return -115;
            }
            if (e.getMessage().contains("111")) {
                return -111;
            }
            return -2;
        }
        return 0;
    }

    private void closeConection() {
        try {
            putOperation.close();
        } catch (Exception e) {
        }
        try {
            clientSession.disconnect(null);
        } catch (Exception e) {
        }
        try {
            clientSession.close();
        } catch (Exception e) {
        }
        putOperation = null;
        clientSession = null;
        hsOperation = null;
        putOperation = null;
    }

    private void sendFile(File fileToSend, OutputStream os) throws IOException {
        int data;
        FileInputStream fin = new FileInputStream(fileToSend);
        while (-1 != (data = fin.read())) {
            os.write(data);
        }
        fin.close();
    }
}
