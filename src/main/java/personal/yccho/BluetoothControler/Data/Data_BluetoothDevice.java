package personal.yccho.BluetoothControler.Data;

import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.RemoteDevice;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Data_BluetoothDevice {
	public String name;
	public String address;	
	public RemoteDevice remoteDevices;
	public ArrayList<Data_Service> services;
	
	public Data_BluetoothDevice(RemoteDevice remoteDevices) throws IOException {
		this.name = remoteDevices.getFriendlyName(false);
		this.address = remoteDevices.getBluetoothAddress();
		this.remoteDevices = remoteDevices;
		services = new ArrayList<Data_Service>();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject createStatusPacket() {
		JSONObject statusPacket_jsob = new JSONObject();
		statusPacket_jsob.put("name", this.name);
		statusPacket_jsob.put("address", this.address);
		JSONArray services_arr = new JSONArray();
		for(Data_Service sevice : services) {
			JSONObject service_jsob = new JSONObject();
			service_jsob.put("name", sevice.name);
			service_jsob.put("url", sevice.url);
			services_arr.add(service_jsob);
		}
		statusPacket_jsob.put("services", services_arr);
		return statusPacket_jsob;
	}
}




