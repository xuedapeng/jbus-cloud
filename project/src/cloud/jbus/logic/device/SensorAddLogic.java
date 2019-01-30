package cloud.jbus.logic.device;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.SensorAddLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="sensor.add")
public class SensorAddLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SensorAddLogicParam myParam = (SensorAddLogicParam)logicParam;
		
		SensorEntity sensor = new SensorEntity();
		sensor.setDeviceId(Integer.valueOf(myParam.getDeviceId()));
		sensor.setSensorNo(Integer.valueOf(myParam.getSensorNo()));
		sensor.setSensorName(myParam.getSensorName());
		sensor.setMemo(myParam.getMemo());
		
		SensorDao dao = new SensorDao(em);
		dao.save(sensor);
		
		res.add("status", 1)
			.add("msg", "sensor.add ok.")
			.add("sensorId", sensor.getId());
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SensorAddLogicParam myParam = (SensorAddLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"sensorNo", "1", "0", "1"},
			{"sensorName", "1", "50", "0"},
			{"memo", "0", "100", "0"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		
		SensorDao dao = new SensorDao(em);
		// sensorNo 重复check 
		if (dao.findBySensorNo(
					Integer.valueOf(myParam.getDeviceId()), 
					Integer.valueOf(myParam.getSensorNo())) 
				!= null) {

			res.add("status", -12)
				.add("msg", "sensorNo 已存在。");
			
			return false;
			
		}
		
		
		// 总数check
		Long total = dao.findTotal(Integer.valueOf(myParam.getDeviceId()));
		if (total >= StatusConst.MAX_SENSOR_AMOUNT) {

			res.add("status", -11)
				.add("msg", "已达传感器数上限("+ StatusConst.MAX_SENSOR_AMOUNT + ")。");
			
			return false;
		}
		
		return true;
	}


}
