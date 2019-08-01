package cloud.jbus.mqtt;


import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.exception.JbusException;
import fw.jbiz.common.conf.ZSystemConfig;


public class MqttPoolManager {

	static String BROKER = ZSystemConfig.getProperty("mqtt.broker");
	static String USERNAME = ZSystemConfig.getProperty("mqtt.auth.account");
	static String PASSWORD = ZSystemConfig.getProperty("mqtt.auth.password");
	 
	 
	static MqttPool _mqttPool = new MqttPool();
	static MqttPool _mqttPool_local = null;
	
	public static void initialize() {
		if (StringUtils.isEmpty(BROKER)) {
			throw new JbusException("need mqtt broker server.");
		}
		
		_mqttPool.initialize(BROKER, USERNAME, PASSWORD);
		
	}
	
	public static MqttPool getMqttPool() {
		return _mqttPool;
	}


	
}
