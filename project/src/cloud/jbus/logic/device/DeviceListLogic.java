package cloud.jbus.logic.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import cloud.jbus.common.helper.HexHelper;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.DeviceListLogicParam;

@Action(method="device.list")
public class DeviceListLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceListLogicParam myParam = (DeviceListLogicParam)logicParam;

		Integer page = Integer.valueOf(myParam.getPage());
		Integer pageSize = Integer.valueOf(myParam.getPageSize());
		Integer userId = getLoginUserId(myParam.getSecretId());
		
		DeviceDao dao = new DeviceDao(em);
		Long total = dao.findTotal(userId);
		
		List<DeviceEntity> deviceList = dao.findByPage(userId, page, pageSize);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		SensorDao sensorDao = new SensorDao(em);
		
		deviceList.forEach(E->{
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("deviceId", E.getId());
			map.put("deviceSn", E.getDeviceSn());
			map.put("deviceName", E.getDeviceName());
			map.put("longitude", E.getLongitude());
			map.put("latitude", E.getLatitude());
			map.put("crcMode", E.getCrcMode());
			map.put("memo", E.getMemo());
			map.put("secretKey", HexHelper.bytesToHexStringNoBlank(E.getSecretKey().getBytes()));
			
			map.put("sensorAmount", sensorDao.findTotal(E.getId()));
			
			resultList.add(map);
		});
		
		res.add("status", 1)
			.add("msg", "device.list ok.")
			.add("total", total)
			.add("result", resultList);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceListLogicParam myParam = (DeviceListLogicParam)logicParam;
		
		if (myParam.getPage() == null) {
			myParam.setPage("1");
		}
		if (myParam.getPageSize() == null) {
			myParam.setPageSize("20");
		}
		
		if (!NumericHelper.isInteger(myParam.getPage())
				|| !NumericHelper.isInteger(myParam.getPageSize())) {

			res.add("status", -3)
				.add("msg", "invalid page info.");
			
			return false;
		}
		
		return true;
	}
	
}
