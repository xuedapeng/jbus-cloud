package cloud.jbus.logic.device;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.ScheduleEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.CmdEncodeDao;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.ScheduleDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.DeviceCloneLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="device.clone")
public class DeviceCloneLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceCloneLogicParam myParam = (DeviceCloneLogicParam)logicParam;
		
		Integer deviceId = toInt(myParam.getDeviceId());
		String deviceSn = CommonLogic.getDeviceSnById(deviceId, em);
		Integer fromDeviceId = toInt(myParam.getFromDeviceId());
		
		SensorDao sensorDao = new SensorDao(em);
		CmdEncodeDao cmdDao = new CmdEncodeDao(em);
		ScheduleDao schDao = new ScheduleDao(em);
		
		// check 
		if(!myParam.getFromDeviceSn().equals(CommonLogic.getDeviceSnById(fromDeviceId,em))) {

			res.add("status", -13)
				.add("msg", "fromDeviceId/Sn not match.");
			
			return false;
		}
		
		if(!sensorDao.findByDeviceId(deviceId, 1, 1).isEmpty()) {

			res.add("status", -11)
				.add("msg", "sensor not empty.");
			
			return false;
		}
		
		if(!schDao.findByDeviceId(deviceId, 1, 1) .isEmpty()) {

			res.add("status", -12)
				.add("msg", "schedule not empty.");
			
			return false;
		}
		
		// 传感器复制、 传感器命令复制
		List<SensorEntity> sensorList = sensorDao.findByDeviceId(fromDeviceId, 1, StatusConst.MAX_SENSOR_AMOUNT);
		for(SensorEntity fromSensor: sensorList) {
			SensorEntity sensor = new SensorEntity();
			sensor.setDeviceId(deviceId);
			sensor.setSensorNo(fromSensor.getSensorNo());
			sensor.setSensorName(fromSensor.getSensorName());
			sensor.setMemo(fromSensor.getMemo());
			sensor.setStatus(fromSensor.getStatus());
			sensorDao.save(sensor);
			
			// 命令复制
			List<CmdEncodeEntity> cmdList = cmdDao.findBySensorId(fromSensor.getId());
			for(CmdEncodeEntity fromCmd: cmdList) {
				CmdEncodeEntity cmd = new CmdEncodeEntity();
				cmd.setSensorId(sensor.getId());
				cmd.setCmdNo(fromCmd.getCmdNo());
				cmd.setCmdName(fromCmd.getCmdName());
				cmd.setScriptText(fromCmd.getScriptText());
				cmd.setParamSchema(fromCmd.getParamSchema());
				cmd.setIncludeCrc(fromCmd.getIncludeCrc());
				cmdDao.save(cmd);
			}
		}
		
		// 计划任务复制
		List<ScheduleEntity> schList = schDao.findByDeviceId(fromDeviceId, 1, StatusConst.MAX_SCHEDULE_AMOUNT);
		for(ScheduleEntity fromSch: schList) {
			ScheduleEntity sch = new ScheduleEntity();
			sch.setDeviceId(deviceId);
			sch.setDeviceSn(deviceSn);
			sch.setCmdHex(fromSch.getCmdHex());
			sch.setDatPtn(fromSch.getDatPtn());
			sch.setInterval(fromSch.getInterval());
			sch.setStatus(fromSch.getStatus());
			sch.setDelay(fromSch.getDelay());
			
			schDao.save(sch);
		}
		
		
		res.add("status", 1)
			.add("msg", "device.clone ok.");
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceCloneLogicParam myParam = (DeviceCloneLogicParam)logicParam;

		String[][] matrix = new String[][]{
			{"fromDeviceId", "1", "0", "1"},
			{"fromDeviceSn", "1", "8", "0"},
			{"deviceId", "1", "0", "1"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		
		if(myParam.getDeviceId().equals(myParam.getFromDeviceId())) {

			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "deviceId 与 fromDeviceId 不能相同。");
			
			return false;
		}
		
		
		return true;
	}


}
