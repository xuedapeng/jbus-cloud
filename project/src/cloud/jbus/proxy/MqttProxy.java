//package cloud.jbus.proxy;
//
//import org.apache.log4j.Logger;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//
//import cloud.jbus.common.helper.GuidHelper;
//import cloud.jbus.common.helper.HexHelper;
//import fw.jbiz.common.conf.ZSystemConfig;
//
//
//public class MqttProxy {
//
//	private static Logger log = Logger.getLogger(MqttProxy.class);
//
//	private static MqttClient _mqttClient;
//	private static String _mqttBroker = ZSystemConfig.getProperty("mqtt.broker");
//	private static String _mqttUsername = ZSystemConfig.getProperty("mqtt.auth.account");
//	private static String _mqttPassword = ZSystemConfig.getProperty("mqtt.auth.password");
//	private static MqttConnectOptions _connOpts = new MqttConnectOptions(); 
//	
//	private static boolean _reconnectRunningFlg = false;
//	
//	
//	static {
////		init();
//	}
//	
//	public static void init() {
//
//		if (_mqttClient != null) {
//			return;
//		}
//		
//		log.info("init mqtt.");
//		_connOpts.setCleanSession(true);  
//		_connOpts.setUserName(_mqttUsername);  
//		_connOpts.setPassword(_mqttPassword.toCharArray());  
//		_connOpts.setConnectionTimeout(10);  
//		_connOpts.setKeepAliveInterval(20); 
//		_connOpts.setAutomaticReconnect(false);
//
//		try {
//			_mqttClient = createMqttClient();
//			log.info("mqtt connected.");
//		} catch (MqttException e) {
//			log.error("", e);
//		}
//		
//		
//		reconnectService();
//	}
//	
//	private static void reconnectService() {
//		if (_reconnectRunningFlg) {
//			return;
//		} else {
//			_reconnectRunningFlg = true;
//		}
//		
//		new Thread() {
//
//			@Override
//			public void run() {
//
//				while(true) {
//
////					log.info("mqtt reconnectService running.");
//					
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {
//						log.error("", e);
//					}
//
////					log.info("isConnected:" + _mqttClient.isConnected());
//					if (!_mqttClient.isConnected()) {
//						try {
//							_mqttClient.connect(_connOpts);
//							ControlProxy.reSubscribe();
//
//							log.info("mqtt reconnected.");
//						} catch (MqttException e) {
//							log.error("", e);
//						}
//					}
//				}
//			}
//		}.start();
//		
//	}
//	
//	public static void publish(String topic, byte[] data) {
//		try {
//
//			log.info(
//					String.format("before:publish:topic=%s, data=[%s]", 
//							topic, 
//							HexHelper.bytesToHexString(data)));
//			
//			MqttMessage mm = new MqttMessage(data);
//			mm.setQos(0);
//			_mqttClient.publish(topic, mm);
//			
//			log.info(
//					String.format("after:publish:topic=%s, data=[%s]", 
//							topic, 
//							HexHelper.bytesToHexString(data)));
//			
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//	}
//
//	public static void subscribe(String topic) {
//		try {
//			log.info("before:subscribe:topic=" + topic);
//			_mqttClient.subscribe(topic);
//			log.info("after:subscribe:topic=" + topic);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//		
//	}
//
//	public static void unsubscribe(String topic) {
//		try {
//			log.info("before:unsubscribe:topic=" + topic);
//			_mqttClient.unsubscribe(topic);
//			log.info("after:unsubscribe:topic=" + topic);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//		
//	}
//
//	private static MqttClient createMqttClient() throws MqttException {
//		
//        MemoryPersistence persistence = new MemoryPersistence();  
//        
//        MqttClient mqttClient = new MqttClient(_mqttBroker, GuidHelper.genUUID(), persistence);  
//        mqttClient.setCallback(new MqttCallback(){
//
//			@Override
//			public void connectionLost(Throwable cause) {
//				log.info("", cause);
//			}
//
//			@Override
//			public void deliveryComplete(IMqttDeliveryToken token) {
//				
//				
//			}
//
//			@Override
//			public void messageArrived(String topic, MqttMessage message) throws Exception {
//				
//				ControlProxy.receiveDat(topic, message.getPayload());
//				
//			}
//        	
//        });  
//        
//        
//        mqttClient.connect(_connOpts);  
//        while(!mqttClient.isConnected()) {
//        	log.info("waiting for connect...");
//        }
//        // 超时设定
//        mqttClient.setTimeToWait(1000);
//
//        return mqttClient;  
//	}
//	
//}
