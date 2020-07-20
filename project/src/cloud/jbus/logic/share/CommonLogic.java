package cloud.jbus.logic.share;

import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;


import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.db.bean.DatDecodeEntity;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.bean.UserRegEntity;
import cloud.jbus.db.dao.DatDecodeDao;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.db.dao.UserRegDao;
import cloud.jbus.logic.user.SendConfirmCodeLogic;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZDbProcessor;

public class CommonLogic {

	public static boolean checkConfirmCode(String account, String confirmCode, ZSimpleJsonObject res, EntityManager em) {

		
		// 验证码check
		UserRegDao regDao = new UserRegDao(em);
		UserRegEntity userReg = regDao.findByAccount(account);
		
		if (userReg == null) {

			res.add("status", -12)
				.add("msg", "验证失败：验证码不存在。");
			
			return false;
		}
		
		if (new Date().getTime() > userReg.getExpireTime().getTime() ) {

			res.add("status", -12)
				.add("msg", "验证失败：验证码过期。");
			
			return false;
		}
		
		if (userReg.getStatus().equals(StatusConst.CODE_INVALID)) {

			res.add("status", -12)
				.add("msg", "验证失败：验证码无效。");
			
			return false;
		}
		
		if(!confirmCode.toLowerCase().equals(userReg.getConfirmCode().toLowerCase())) {
			
			increaseRetryTimes(account);
			
			res.add("status", -12)
				.add("msg", "验证失败：验证码错误。");
			
			return false;
		}
		
		return true;
	}
	

	private static void increaseRetryTimes(String account) {
		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {

				UserRegDao regDao = new UserRegDao(em);
				UserRegEntity userReg = regDao.findByAccount(account);
				
				userReg.setRetryTimes(userReg.getRetryTimes()+1);
				
				if (userReg.getRetryTimes() > SendConfirmCodeLogic._maxRetryTimes) {
					userReg.setStatus(StatusConst.CODE_INVALID);
				}
				
				regDao.save(userReg);
				
			}
			
		}.run();
	}
	

	public static String getDeviceSnById(Integer deviceId, EntityManager em) {
		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findById(deviceId);
		
		if (device == null) {
			return null;
		}
		
		return device.getDeviceSn();
		
	}
	
	public static Integer getDeviceIdBySn(String deviceSn, EntityManager em) {
		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findByDeviceSn(deviceSn);
		
		if (device == null) {
			return null;
		}
		
		return device.getId();
		
	}

	public static String getDeviceNameById(Integer deviceId, EntityManager em) {
		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findById(deviceId);
		
		if (device == null) {
			return null;
		}
		
		return device.getDeviceName();
		
	}
	

	public static String getSensorNameByNo(Integer deviceId, Integer sensorNo, EntityManager em) {
		SensorDao dao = new SensorDao(em);
		SensorEntity sensor = dao.findBySensorNo(deviceId, sensorNo);
		
		if (sensor == null) {
			return null;
		}
		
		return sensor.getSensorName();
		
	}

	public static Integer getSensorIdByNo(Integer deviceId, Integer sensorNo, EntityManager em) {
		SensorDao dao = new SensorDao(em);
		SensorEntity sensor = dao.findBySensorNo(deviceId, sensorNo);
		
		if (sensor == null) {
			return null;
		}
		
		return sensor.getId();
		
	}
	
	public  static Integer getDeviceIdBySn(String deviceSn) {
		Integer[] deviceId = {null};
		
		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {
				deviceId[0] = getDeviceIdBySn(deviceSn, em);
				
			}
		}.run();;
		
		return deviceId[0];
	}

	public  static Integer getUserIdBySecretId(String secretId, EntityManager em) {
		UserDao dao = new UserDao(em);
		UserEntity user = dao.findBySecretId(secretId);
		
		if (user == null) {
			return null;
		}
		
		return user.getId();
	}

	public  static Integer getUserIdBySecretId(String secretId) {
		Integer[] userId = {null};
		
		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {
				userId[0] = getUserIdBySecretId(secretId, em);
				
			}
		}.run();;
		
		return userId[0];
	}
	

	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, String>> getFieldStyle(Integer deviceId, String sensorNo, ZSimpleJsonObject res, EntityManager em)  {

		DatDecodeDao decodeDao = new DatDecodeDao(em);
		DatDecodeEntity decode = decodeDao.findByDeviceId(deviceId);
		
		//校验：decode存在
		if (decode == null || decode.getResultSchema() == null) {
			res.add("status", -21)
				.add("msg", "解码器错误。");
			return null;
		}
		
		String resultSchema = decode.getResultSchema();
		Map<String, Object> scheMap = JsonHelper.json2map(resultSchema);
		
		// 校验：resultSchema中sno存在
		if (!scheMap.containsKey(sensorNo)) {
			res.add("status", -22)
				.add("msg", "解码器中缺少sno(" + sensorNo + ")");
			return null;
		}
		

		Map<String, Object> sch = (Map<String, Object>) scheMap.get(sensorNo);
		if ("ref".equals(sch.get("type"))) {
			String refSno = (String) sch.get("refsno");
			
			sch = (Map<String, Object>) scheMap.get(refSno);
			if(sch == null) {

				res.add("status", -22)
					.add("msg", "解码器中缺少refsno(" + refSno + ")");
				
				return null;
			}
		}
		
		Map<String, Map<String, String>> fieldStyle = (Map<String, Map<String, String>>) sch.get("field");
		String type = (String) sch.get("type");
		
		if (!"metric".equals(type) || fieldStyle == null){
			res.add("status", -23)
				.add("msg", "解码器模式错误");
			
			return null;
		}
		
		return fieldStyle;
	}
}
