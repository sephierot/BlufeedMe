/*
 * BlufeedmeBTApp.java
 */
package blufeedmebt.vista;


import com.intel.bluetooth.BlueCoveImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import blufeedmebt.DispositivoBusquedaBT;
import blufeedmebt.DispositivoEnvioBT;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;

/**
 * The main class of the application.
 */
public class BlufeedmeBTApp extends SingleFrameApplication {
    public static final Logger LOGGER = Logger.getLogger("blufeedmebt");

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new BlufeedmeBTView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of BlufeedmeBTApp
     */
    public static BlufeedmeBTApp getApplication() {
        return Application.getInstance(BlufeedmeBTApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) throws IOException {
        //Lanzamos la interfaz grafica
        launch(BlufeedmeBTApp.class, args);

        //Establecemos las propiedades de la libreria bluecove
        if(args.length > 0) setPropiedadesBluecove(args[0]);
        else setPropiedadesBluecove("bluecove.prop");
        
        BlueCoveImpl.useThreadLocalBluetoothStack();
        Vector<String> dispositivos;

        try {
            dispositivos = BlueCoveImpl.getLocalDevicesID();
            System.out.println("En esta maquina hay instalado/s " + dispositivos.size() + " dispositivo/s Bluetooth");
            System.out.println(dispositivos.get(0));

            DispositivoBusquedaBT dispositivoBusqueda = new DispositivoBusquedaBT(dispositivos.get(0));
            
            //Iniciamos la hebra de busqueda
            dispositivoBusqueda.run();
/*
            for (int i = 1; i < dispositivos.size(); i++) {
                System.out.println("Iniciando el dispositivo de envío: " + i);
                DispositivoEnvioBT sender = new DispositivoEnvioBT(dispositivos.get(i));
                sender.start();
            }*/
        } catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }

    private static void setPropiedadesBluecove(String url_fichero) throws IOException {
        FileInputStream fis = new FileInputStream(new File(url_fichero));
        Properties cfg = new Properties();
        cfg.load(fis);
        Enumeration<Object> keys = cfg.keys();
        while (keys.hasMoreElements()) {
            String k = (String) keys.nextElement();
            BlueCoveImpl.setConfigProperty(k, cfg.getProperty(k));
        }
    }
}
