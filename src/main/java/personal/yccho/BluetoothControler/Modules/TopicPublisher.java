package personal.yccho.BluetoothControler.Modules;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import personal.yccho.BluetoothControler.Tools;
import personal.yccho.BluetoothControler.Data.Data_Paras;

public class TopicPublisher implements Runnable {
	public Data_Paras paras;
	public String clientId;
	public String content;
	
	public TopicPublisher(Data_Paras paras, String content) {
		this.paras = paras;
		this.clientId = "Bluetooth_Devices_Status_Publish_"+Tools.GenerateRandomString();
		this.content = content;
	}
	
	@Override
	public void run() {
		doPublish();		
	}	
	
	private MqttConnectOptions setUpConnectionOptions() {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		connOpts.setUserName(paras.mqttUS);
		connOpts.setPassword(paras.mqttPW.toCharArray());
		return connOpts;
	}
	
	private void doPublish() {		
		Tools.showLog(0, "Bluetooth Devices Status Publisher initializing...");
		// Create an Mqtt client
    	MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient;
		try {
			// Connect the client
			mqttClient = new MqttClient(paras.mqttHost, clientId, persistence);			
			MqttConnectOptions connOpts = setUpConnectionOptions(); 
			mqttClient.connect(connOpts);				
			Tools.showLog(0, "clientId-> "+clientId+" of MQTT Connect success.");			
			
			// Create a Mqtt message 
			MqttMessage message = new MqttMessage(content.getBytes());			
            message.setQos(0);                
            message.setRetained(true);
            if(!mqttClient.isConnected()) mqttClient.reconnect();	           
            
            // Publish the message	            
            mqttClient.publish(paras.statusPublicTopic, message);
            Tools.showLog(0, "publish TcisStatus! clientId-> "+clientId+", TOPIC: "+paras.statusPublicTopic);		
					
			mqttClient.disconnect();
		} catch (MqttException e) {			
			Tools.showLog(1, "reason " + e.getReasonCode());
			Tools.showLog(1, "msg " + e.getMessage());
			Tools.showLog(1, "loc " + e.getLocalizedMessage());
			Tools.showLog(1, "cause " + e.getCause());
			Tools.showLog(1, "excep " + e);
			e.printStackTrace();
		}
	}	
}




