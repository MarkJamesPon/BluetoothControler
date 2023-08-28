package personal.yccho.BluetoothControler.Modules;

import java.util.Map;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import personal.yccho.BluetoothControler.Tools;
import personal.yccho.BluetoothControler.Data.Data_BluetoothDevice;
import personal.yccho.BluetoothControler.Data.Data_Service;

public class ServiceListener implements DiscoveryListener {
	public String address;
	public Map<String, Data_BluetoothDevice> devices;
	public final Object serviceSearchCompletedEvent;
	
	public ServiceListener(String address, Map<String, Data_BluetoothDevice> devices, Object serviceSearchCompletedEvent) {
		this.address = address;
		this.devices = devices;
		this.serviceSearchCompletedEvent = serviceSearchCompletedEvent;
	}

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (int i = 0; i < servRecord.length; i++) {
            String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) {
                continue;
            }
            DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
            String sName = "-";
            if (serviceName != null) {
            	sName = (String) serviceName.getValue();
                Tools.showLog(0, "service " + serviceName.getValue() + " found " + url);
            } else {
            	Tools.showLog(0, "service found " + url);
            }
            if(devices.get(address)!=null) {
            	devices.get(address).services.add(new Data_Service(sName, url));
            }
        }
	}

	@Override
	public void serviceSearchCompleted(int transID, int respCode) {		
		if(devices.get(address)!=null) {
			Tools.showLog(0, "service search completed! device: "+devices.get(address).name+
    							", service size: "+devices.get(address).services.size());
    	}                
        synchronized(serviceSearchCompletedEvent){
            serviceSearchCompletedEvent.notifyAll();
        }
	}

	@Override
	public void inquiryCompleted(int discType) {}

}
