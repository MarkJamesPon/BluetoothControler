package personal.yccho.BluetoothControler;

import org.json.simple.JSONObject;

import personal.yccho.BluetoothControler.Data.Data_Paras;
import personal.yccho.BluetoothControler.Modules.BluetoothDevicesControler;
import personal.yccho.BluetoothControler.Modules.TopicPublisher;

public class Controler {
	public static Data_Paras paras;
	public static int returnStatusfreq;	
	
	private BluetoothDevicesControler bluetoothControler;
	
	public Controler() {
		readConfig();
		bluetoothControler = new BluetoothDevicesControler();
	}
	
	private void readConfig() {
		paras = new Data_Paras();
		JSONObject configJObj = Tools.readJSONFile("config.json");
		paras.controlerId = (String) configJObj.get("controlerId");
		paras.mqttHost = (String) configJObj.get("mqttHost");
		paras.mqttUS = (String) configJObj.get("mqttUS");
		paras.mqttPW = (String) configJObj.get("mqttPW");
		paras.statusPublicTopic = (String) configJObj.get("statusPublicTopic");
		returnStatusfreq = (int)(long) configJObj.get("returnStatusfreq");
	}

	public void BluetoothSearch() {
		bluetoothControler.searchDevices();
    	bluetoothControler.searchServices();
	}
	
	public void returnBluetoothStatus() {
		try {
			while(true){
				BluetoothSearch();
				String statusContent = bluetoothControler.createStatusPacket().toJSONString();
				TopicPublisher publisher = new TopicPublisher(paras, statusContent);
				new Thread(publisher).run();
				Thread.sleep(returnStatusfreq);				
			}
		} catch (InterruptedException e) {
			Tools.showLog(0, "InterruptedException");
			e.printStackTrace();
		}
	}
}
