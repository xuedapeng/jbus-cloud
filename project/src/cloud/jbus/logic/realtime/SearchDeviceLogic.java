package cloud.jbus.logic.realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.DatDecodeEntity;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.CmdEncodeDao;
import cloud.jbus.db.dao.DatDecodeDao;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.realtime.param.SearchDeviceLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="realtime.device.search")
public class SearchDeviceLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SearchDeviceLogicParam myParam = (SearchDeviceLogicParam)logicParam;
		Integer userId = this.getLoginUserId(myParam.getSecretId());
		String filter = myParam.getFilter();
		Integer page = Integer.valueOf(myParam.getPage());
		Integer pageSize = Integer.valueOf(myParam.getPageSize());
		
		
		DeviceDao deviceDao = new DeviceDao(em);
		List<DeviceEntity> deviceList = deviceDao.searchDevice(userId, filter, page, pageSize);
		
		SensorDao sensorDao = new SensorDao(em);
		DatDecodeDao datDecodeDao = new DatDecodeDao(em);
		CmdEncodeDao cmdEncodeDao = new CmdEncodeDao(em);
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for(DeviceEntity device: deviceList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("deviceId", device.getId());
			map.put("deviceSn", device.getDeviceSn());
			map.put("deviceName", device.getDeviceName());
			map.put("crcMode", device.getCrcMode());
			
			
			Integer deviceId = device.getId();
			DatDecodeEntity datDecode = datDecodeDao.findByDeviceId(deviceId);
			if (datDecode != null) {
				Map<String, Object> decMap = new HashMap<String, Object>();
				decMap.put("decodeId", datDecode.getId());
				decMap.put("scriptText", datDecode.getScriptText());
				decMap.put("resultSchema", datDecode.getResultSchema());
				decMap.put("includeCrc", datDecode.getIncludeCrc());
				map.put("datDecode", decMap);
			}
			
			List<SensorEntity> sensorList = sensorDao.findByDeviceId(
					deviceId, StatusConst.DEFAULT_PAGE_IDX, StatusConst.MAX_SENSOR_AMOUNT);
			List<Map<String, Object>> resultSensorList = new ArrayList<Map<String, Object>>();
			for(SensorEntity sensor: sensorList) {
				Map<String, Object> mapSensor = new HashMap<String, Object>(); 
				mapSensor.put("sensorId", sensor.getId());
				mapSensor.put("sensorNo", sensor.getSensorNo());
				mapSensor.put("sensorName", sensor.getSensorName());
				
				
				List<CmdEncodeEntity> cmdEncodeList = cmdEncodeDao.findBySensorId(sensor.getId());
				List<Map<String, Object>> resultEncList = new ArrayList<Map<String, Object>>();
				for(CmdEncodeEntity enc: cmdEncodeList) {
					Map<String, Object> mapEnc = new HashMap<String, Object>(); 
					mapEnc.put("cmdNo", enc.getCmdNo());
					mapEnc.put("cmdName", enc.getCmdName());
					mapEnc.put("scriptText", enc.getScriptText());
					mapEnc.put("paramSchema", enc.getParamSchema());
					mapEnc.put("includeCrc", enc.getIncludeCrc());
					resultEncList.add(mapEnc);
				}
				mapSensor.put("cmdEncodeList", resultEncList);
				resultSensorList.add(mapSensor);
			}
			
			map.put("sensorList", resultSensorList);
			resultList.add(map);
			
		}
		
		// [{"deviceId":"", "deviceSn":"", "deviceName":"", "crcMode":"", 
		//					"sensorList":[{"sensorId":"", "sensorNo":"", "sensorName":""
		//							"cmdEncodeList":[{"cmdNo":"", "cmdName":"", "scriptText":"", "paramSchema":"", "includeCrc":""}]
		//						}],
		//					"datDecode":{"decodeId":"", "scriptText":"", "resultSchema":"", "includeCrc":""}
		// 					
		
		//	}]
		res.add("status", 1)
			.add("msg","realtime.device.search ok")
			.add("result", resultList);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SearchDeviceLogicParam myParam = (SearchDeviceLogicParam)logicParam;
		
		if (myParam.getPage() == null) {
			myParam.setPage("1");
		}
		
		if (myParam.getPageSize() == null) {
			myParam.setPageSize("10");
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
