package cloud.jbus.proxy;


import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import cloud.jbus.common.helper.HexHelper;
import cloud.jbus.mqtt.MqttPoolManager;



public class MqttProxy {

	static Logger log = Logger.getLogger(MqttProxy.class);

	public static void publish(String topic, byte[] data) {
		try {

			log.info(
					String.format("before:publish:topic=%s, data=[%s]", 
							topic, 
							HexHelper.bytesToHexString(data)));
			
			MqttMessage mm = new MqttMessage(data);
			mm.setQos(0);
			MqttPoolManager.getMqttPool().getInstance().publish(topic, mm);
			
			log.info(
					String.format("after:publish:topic=%s, data=[%s]", 
							topic, 
							HexHelper.bytesToHexString(data)));
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static void doAfterReconnect() {
//		subscribe();
	}
	
}
