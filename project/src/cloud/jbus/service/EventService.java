package cloud.jbus.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.mysql.cj.log.Log;

import cloud.jbus.common.constant.JbusConst;
import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.EventEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.realtime.GetOnlineInfoOfDevicesLogic;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.proxy.ControlProxy;
import cloud.jbus.proxy.MqttProxy;
import fw.jbiz.db.ZDao;
import fw.jbiz.logic.ZDbProcessor;

// 设备上下线信息相关服务
public class EventService {

	static Logger log = Logger.getLogger(EventService.class);
	
	public static void updateStatusOnStart() {

		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {
				updateStatusOnStart(em);
			}
			
		}.run();

	}
	
	private static void updateStatusOnStart(EntityManager em) {

		log.info("updateStatusOnStart start.");
		
		int pageSize = 1000;
		int page = 1;
		
		DeviceDao dao = new DeviceDao(em);
		while(true) {
			List<DeviceEntity> list = dao.findByPage(null, page, pageSize);
			updateStatusOnStart(list);
			if (list.size() < pageSize) {
				break;
			} else {
				page++;
			}
		}


		log.info("updateStatusOnStart end.");
	}
	
	@SuppressWarnings("unchecked")
	private static void updateStatusOnStart(List<DeviceEntity> list) {

		List<String> deviceSnList = list.stream().map(DeviceEntity::getDeviceSn).collect(Collectors.toList());
		Map<String, Object> map = GetOnlineInfoOfDevicesLogic.getOnlineStatus(deviceSnList);
		
		if (Integer.valueOf((String) map.get("status")) < 0) {
			return;
		}
		
		Map<String, String> result = (Map<String, String>)map.get("result");
		list.forEach((device)->{
			EventEntity eventEntity = new EventEntity();
			eventEntity.setDeviceId(device.getId());
			eventEntity.setDeviceSn(device.getDeviceSn());
			eventEntity.setEvent(result.get(device.getDeviceSn()));	
			eventEntity.setTime(new Date());
			eventEntity.setMemo("on webapp start");
			
			ZDao.saveAsy(eventEntity);
		});
	}
	
	public static void subscribeEvent(String deviceSn) {

		MqttProxy.subscribe(JbusConst.TOPIC_PREFIX_STS + deviceSn);
	}
	
	public static void unsubscribeEvent(String deviceSn) {

		MqttProxy.unsubscribe(JbusConst.TOPIC_PREFIX_STS + deviceSn);
	}

	public static void subscribeEvent(List<String> deviceSnList) {
		deviceSnList.forEach((sn)->{
			subscribeEvent(sn);
		});
	}
	
	public static void subscribeEventOfAll() {

		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {
				subscribeEventOfAll(em);
			}
			
		}.run();
	}
	
	private static void subscribeEventOfAll(EntityManager em) {

		log.info("subscribeEventOfAll start.");
		
		int pageSize = 1000;
		int page = 1;
		
		DeviceDao dao = new DeviceDao(em);
		while(true) {
			List<DeviceEntity> list = dao.findByPage(null, page, pageSize);
			subscribeEvent(list.stream().map(DeviceEntity::getDeviceSn).collect(Collectors.toList()));
			if (list.size() < pageSize) {
				break;
			} else {
				page++;
			}
		}
		
		log.info("subscribeEventOfAll end.");
	}
	
	public static void saveEvent(Map<String, Object> data) {
		
		String deviceSn = (String)data.get("deviceSn");
		String time = (String)data.get("time");
		String event = (String)data.get("event");
		String memo = JsonHelper.map2json(data);
		
		EventEntity eventEntity = new EventEntity();
		eventEntity.setDeviceId(CommonLogic.getDeviceIdBySn(deviceSn));
		eventEntity.setDeviceSn(deviceSn);
		eventEntity.setEvent(event);
		eventEntity.setTime(DateHelper.fromYmdhms(time));
			
		eventEntity.setMemo(memo);
		
		ZDao.saveAsy(eventEntity);
	}

}


