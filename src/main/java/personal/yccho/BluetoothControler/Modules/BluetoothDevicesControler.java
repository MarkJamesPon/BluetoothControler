package personal.yccho.BluetoothControler.Modules;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import personal.yccho.BluetoothControler.Controler;
import personal.yccho.BluetoothControler.Tools;
import personal.yccho.BluetoothControler.Data.Data_BluetoothDevice;

public class BluetoothDevicesControler {
	public Map<String, Data_BluetoothDevice> devices;		//key: address
	private final UUID serviceUUID;
	
	public BluetoothDevicesControler() {				
		devices = new HashMap<String, Data_BluetoothDevice>();	
		serviceUUID = new UUID(0x1105);
	}
	
	public void searchDevices() {
		devices.clear();
		final Object inquiryCompletedEvent = new Object();
		DiscoveryListener listener = new DiscoveryListener() {
			@Override
			public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {				
				try {
					//System.out.println("Device " + arg0.getBluetoothAddress() + " found: "+arg0.getFriendlyName(false));
					Data_BluetoothDevice deviceObj = new Data_BluetoothDevice(arg0);
					devices.put(deviceObj.address, deviceObj);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void inquiryCompleted(int arg0) {
				//System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
			}
			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {}
			
			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {}            
        };
        
        synchronized(inquiryCompletedEvent) {
            boolean started;
			try {
				started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
				if (started) {
	                //System.out.println("wait for device inquiry to complete...");
	                inquiryCompletedEvent.wait();
	                Tools.showLog(0, devices.size() +  " device(s) found");
	            }	
			} catch (BluetoothStateException | InterruptedException e) {
				e.printStackTrace();
			}
            
        }
	}
	
	public void searchServices() {
		for(Entry<String, Data_BluetoothDevice> deviceObj : devices.entrySet()) deviceObj.getValue().services.clear();
		final Object serviceSearchCompletedEvent = new Object();	
        
        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };
        try {
        	for(Entry<String, Data_BluetoothDevice> device : devices.entrySet()) {        		
        		synchronized(serviceSearchCompletedEvent) {
        			Tools.showLog(0, "search services on " + device.getKey() + " " + device.getValue().name);	                
        			ServiceListener listener = new ServiceListener(device.getKey(), devices, serviceSearchCompletedEvent);
					LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, device.getValue().remoteDevices, listener);					
	                serviceSearchCompletedEvent.wait();
        		}
        	}       	
        } catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject createStatusPacket() {
		JSONObject status_jsob = new JSONObject();
		status_jsob.put("controlerId", Controler.paras.controlerId);
		JSONArray bluetoothDevices_arr = new JSONArray();
		for(Entry<String, Data_BluetoothDevice> device : devices.entrySet()) {
			bluetoothDevices_arr.add(device.getValue().createStatusPacket());
		}
		status_jsob.put("bluetoothDevices", bluetoothDevices_arr);
		return status_jsob;
	}
	
}







