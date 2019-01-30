package cloud.jbus.logic.device;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.SensorAddLogicParam;
import cloud.jbus.logic.device.param.SensorDeleteLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="sensor.delete")
public class SensorDeleteLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SensorDeleteLogicParam myParam = (SensorDeleteLogicParam)logicParam;

		SensorDao dao = new SensorDao(em);
		SensorEntity sensor = dao.findBySensorId(
				Integer.valueOf(myParam.getDeviceId()), 
				Integer.valueOf(myParam.getSensorId()));
		
		if (sensor == null) {
			res.add("status", -21)
				.add("msg", "传感器不存在");
			return  false;
		}
		
		dao.delete(sensor);
		
		res.add("status", 1)
			.add("msg", "sensor.delete ok.");
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SensorDeleteLogicParam myParam = (SensorDeleteLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"sensorId", "1", "0", "1"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		
		
		return true;
	}


}
