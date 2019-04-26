//package cloud.jbus.proxy;
//
//import java.io.UnsupportedEncodingException;
//
//import org.apache.log4j.Logger;
//
//import cloud.jbus.common.constant.JbusConst;
//import cloud.jbus.common.helper.HexHelper;
//import cloud.jbus.common.helper.JsonHelper;
////import cloud.jbus.service.EventService;
//
//public class ControlProxy {
//
//	static Logger log = Logger.getLogger(ControlProxy.class);
//	
//	public static void receiveDat(String topic, byte[] data) {
//		
//
//		log.info(
//				String.format("receive: topic=%s, data=[%s]", 
//						topic, 
//						HexHelper.bytesToHexString(data)));
//		
//		// 只处理状态信息
//		if (!topic.startsWith(JbusConst.TOPIC_PREFIX_STS)) {
//			return;
//		}
//		
//		try {
//
//			EventService.saveEvent(JsonHelper.json2map(new String(data, "UTF-8")));
//			
//		} catch (UnsupportedEncodingException e) {
//			log.error("", e);
//			return;
//		}
//
//	}
//
//	public static void reSubscribe() {
//		subscribeStatus();
//	}
//	
//	public static void subscribeStatus() {
//		EventService.subscribeEventOfAll();
//	}
//	
//	
//}
