package cloud.jbus.logic.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.EventEntity;
import cloud.jbus.db.bean.VDeviceOnlineEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.EventDao;
import cloud.jbus.db.dao.VDeviceOnlineDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.monitor.param.GetEventLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="monitor.event.query")
public class GetEventLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		GetEventLogicParam myParam = (GetEventLogicParam)logicParam;

		String deviceKey = myParam.getDeviceKey();
		String onlyLast = myParam.getOnlyLast();
		Integer userId = getLoginUserId(myParam.getSecretId());
		Integer page = 1;
		Integer pageSize = 100;
		
		if (StringUtils.isEmpty(StringUtils.trim(deviceKey))) {
			deviceKey = null;
		}
		
		// 默认只查最新的
		if(StringUtils.isEmpty(StringUtils.trim(onlyLast))) {
			onlyLast = "yes";
		}	
		
		// 查找设备
		DeviceDao deviceDao = new DeviceDao(em);
		List<DeviceEntity> deviceList = deviceDao.searchDevice(userId, deviceKey, page, pageSize);
		if (deviceList.isEmpty()) {

			res.add("status", -10);
			res.add("msg", "没有找到设备");
			return false;
		}
		
		List<String> deviceSnList = deviceList.stream().map(DeviceEntity::getDeviceSn).collect(Collectors.toList());
		Map<String, String> sn2nameMap = deviceList.stream().collect(Collectors.toMap(DeviceEntity::getDeviceSn, DeviceEntity::getDeviceName));
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// 查找事件
		if ("yes".equals(onlyLast)) {
			EventDao eventDao = new EventDao(em);
			List<EventEntity> eventList = eventDao.searchEventLast(userId, deviceSnList, page, pageSize);
			eventList.forEach((event)->{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("deviceId", event.getDeviceId());
				map.put("deviceSn", event.getDeviceSn());
				map.put("event", event.getEvent());
				map.put("memo", event.getMemo());
				map.put("time", DateHelper.toYmdhms(event.getTime()));
				map.put("deviceName", sn2nameMap.get(event.getDeviceSn()));
				resultList.add(map);
			});
		} else {
			EventDao eventDao = new EventDao(em);
			List<EventEntity> eventList = eventDao.searchEvent(userId, deviceSnList, page, pageSize);
			eventList.forEach((event)->{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("deviceId", event.getDeviceId());
				map.put("deviceSn", event.getDeviceSn());
				map.put("event", event.getEvent());
				map.put("memo", event.getMemo());
				map.put("time", DateHelper.toYmdhms(event.getTime()));
				map.put("deviceName", sn2nameMap.get(event.getDeviceSn()));
				resultList.add(map);
			});
		}
		
		res.add("status", 1);
		res.add("msg", "设备状态查询成功");
		res.add("result", resultList);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		return true;
	}

}
