package cloud.jbus.logic.codec;

import javax.persistence.EntityManager;

import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.CmdEncodeDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.codec.param.CmdAddLogicParam;
import cloud.jbus.logic.codec.param.CmdDeleteLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="codec.cmd.delete")
public class CmdDeleteLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		CmdDeleteLogicParam myParam = (CmdDeleteLogicParam)logicParam;

		Integer sensorId = Integer.valueOf(myParam.getSensorId());
		Integer cmdId = Integer.valueOf(myParam.getCmdId());
		
		CmdEncodeDao dao = new CmdEncodeDao(em);
		CmdEncodeEntity bean = dao.findByCmdId(sensorId, cmdId);
		if(bean == null) {

			res.add("status", -21)
				.add("msg", "命令解析器不存在");
			return  false;
		}
		
		dao.delete(bean);
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "codec.cmd.delete ok!");
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		String[][] matrix = new String[][]{
			{"sensorId", "1", "0", "1"},
			{"cmdId", "1", "0", "1"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		
		return true;
	}
	

	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		
		// 设置deviceId
		CmdDeleteLogicParam myParam = (CmdDeleteLogicParam)logicParam;
		Integer sensorId = Integer.valueOf(myParam.getSensorId());
		
		SensorDao dao = new SensorDao(em);
		SensorEntity sensor = dao.findById(sensorId);
		
		if(sensor==null) {
			res.add("msg", "sensorId 不存在")
			.add("status", IResponseObject.RSP_CD_ERR_PARAM);
			
			return false;
		}
		
		myParam.setDeviceId(String.valueOf(sensor.getDeviceId()));
		
		return super.auth(logicParam, res, em);
	}

}
