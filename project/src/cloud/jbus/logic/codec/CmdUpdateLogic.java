package cloud.jbus.logic.codec;

import javax.persistence.EntityManager;

import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.CmdEncodeDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.codec.param.CmdUpdateLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="codec.cmd.update")
public class CmdUpdateLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		CmdUpdateLogicParam myParam = (CmdUpdateLogicParam)logicParam;

		Integer sensorId = Integer.valueOf(myParam.getSensorId());
		Integer cmdId = Integer.valueOf(myParam.getCmdId());
		Integer cmdNo = Integer.valueOf(myParam.getCmdNo());
		String cmdName = myParam.getCmdName();
		String scriptText = myParam.getScriptText();
		
		
		CmdEncodeDao dao = new CmdEncodeDao(em);
		CmdEncodeEntity bean = dao.findByCmdId(sensorId, cmdId);

		if(bean == null) {

			res.add("status", -21)
				.add("msg", "命令解析器不存在");
			return  false;
		}
		
		bean.setCmdNo(cmdNo);
		bean.setCmdName(cmdName);
		bean.setScriptText(scriptText);
		dao.save(bean);
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "codec.cmd.update ok！")
			.add("cmdId", bean.getId());
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		CmdUpdateLogicParam myParam = (CmdUpdateLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"sensorId", "1", "0", "1"},
			{"cmdId", "1", "0", "1"},
			{"cmdNo", "1", "0", "1"},
			{"cmdName", "1", "255", "0"},
			{"scriptText", "1", "1000", "0"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}

		CmdEncodeDao dao = new CmdEncodeDao(em);
		
		// cmdNo 重复check 
		CmdEncodeEntity cmd  = dao.findByCmdNo(
				toInt(myParam.getSensorId()), 
				toInt(myParam.getCmdNo()));
		if (cmd != null
				&& !cmd.getId().equals(toInt(myParam.getCmdId()))) {	

			res.add("status", -12)
				.add("msg", "cmdNo 已存在。");
			
			return false;
			
		}
		
		return true;
	}
	

	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		
		// 设置deviceId
		CmdUpdateLogicParam myParam = (CmdUpdateLogicParam)logicParam;
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
