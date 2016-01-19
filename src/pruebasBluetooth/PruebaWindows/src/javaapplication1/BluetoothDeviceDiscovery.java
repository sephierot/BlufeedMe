
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javaapplication1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
/**
 * Class that discovers all bluetooth devices in the neighbourhood
 * and displays their name and bluetooth address.
 */
public class BluetoothDeviceDiscovery implements DiscoveryListener{
	//object used for waiting
	private static Object lock=new Object();
	//vector containing the devices discovered
	private static ArrayList vecDevices=new ArrayList<RemoteDevice>();
        private static ArrayList vecServices = new ArrayList<Integer>();

        private static DiscoveryAgent agent;

        public static final UUID SERVICIO_L2CAP = new UUID(0x0100);
        public static UUID[] SERVICIOS = new UUID[]{ SERVICIO_L2CAP };
        public static int[] ATRIBUTOS = new int[]{0x0001,0x0003,0x0008,0x000C,0x0100,0x000F,
0x1101,0x1000,0x1001,0x1002,0x1115,0x1116,0x1117};




	//main method of the application
	public static void main(String[] args) throws IOException {

		//display local device address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
                localDevice.setDiscoverable(DiscoveryAgent.GIAC);

		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());


		//find devices
		agent = localDevice.getDiscoveryAgent();
		System.out.println("Starting device inquiry...");
		agent.startInquiry(DiscoveryAgent.GIAC, new BluetoothDeviceDiscovery());

		try {
			synchronized(lock){
				lock.wait();
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Device Inquiry Completed. ");
		//print all devices in vecDevices
		int deviceCount=vecDevices.size();
		if(deviceCount <= 0){
			System.out.println("No Devices Found .");
		}
		else{
			//print bluetooth device addresses and names in the format [ No. address (name) ]
			System.out.println("Bluetooth Devices: ");
			for (int i = 0; i <deviceCount; i++) {
				RemoteDevice remoteDevice=(RemoteDevice)vecDevices.get(i);
				System.out.println((i+1)+". "+remoteDevice.getBluetoothAddress()+" ("+remoteDevice.getFriendlyName(false)+")");
			}
		}
                RemoteDevice rd = (RemoteDevice)(vecDevices.get(0));
                String address = rd.getBluetoothAddress();
                String friendlyName = null;
                try {
                    friendlyName = rd.getFriendlyName(true);
                } catch(IOException e) { }

                String device = null;
                if(friendlyName == null) {
                    device = address;
                } else {
                    device = friendlyName + " ("+address+")";
                }
                try {
                    System.out.println("Comenzada busqueda"+ " de serivicios en: "+device+"; ");
                    agent.searchServices(ATRIBUTOS, SERVICIOS, rd, new BluetoothDeviceDiscovery());

                    try {
			synchronized(lock){
				lock.wait();
			}
                    }
                    catch (InterruptedException e) {
			e.printStackTrace();
                    }
                    System.out.println("searchServices completada ");
                } catch(BluetoothStateException e) {
                    e.printStackTrace();
                    System.err.println("No se pudo "+"comenzar la busqueda");
                }
        }//end main


	//methods of DiscoveryListener
	/**
	 * This call back method will be called for each discovered bluetooth devices.
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		System.out.println("Device discovered: "+btDevice.getBluetoothAddress());
		//add the device to the vector
		if(!vecDevices.contains(btDevice)){
			vecDevices.add(btDevice);
		}
	}
	//no need to implement this method since services are not being discovered
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            System.out.println("Se ha encontrado un servicio remoto");
            ServiceRecord service = null;
            for(int i=0; i<servRecord.length; i++){
                service = servRecord[i];
                vecServices.add(service);
            }
	}
	//no need to implement this method since services are not being discovered
	public void serviceSearchCompleted(int transID, int respCode) {
            synchronized(lock){
		lock.notify();
            }
       
            switch(respCode) {
                case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                    System.out.println("Busqueda completada "+ "con normalidad");
                    break;
                case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                    System.out.println("Busqueda cancelada");
                    break;
                case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                    System.out.println("Dispositivo no alcanzable");
                    break;
                case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                    System.out.println("No se encontraron registros"+" de servicio");
                    break;
                case DiscoveryListener.SERVICE_SEARCH_ERROR:
                    System.out.println("Error en la busqueda");
                    break;
            }
	}
	/**
	 * This callback method will be called when the device discovery is
	 * completed.
	 */
	public void inquiryCompleted(int discType) {
		synchronized(lock){
			lock.notify();
		}
		switch (discType) {
			case DiscoveryListener.INQUIRY_COMPLETED :
				System.out.println("INQUIRY_COMPLETED");
				break;
			case DiscoveryListener.INQUIRY_TERMINATED :
				System.out.println("INQUIRY_TERMINATED");
				break;
			case DiscoveryListener.INQUIRY_ERROR :
				System.out.println("INQUIRY_ERROR");
				break;
			default :
				System.out.println("Unknown Response Code");
				break;
		}
	}//end method
}//end class
