package cloud.jbus.logic.codec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.CmdEncodeDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.codec.param.CmdListLogicParam;
import cloud.jbus.logic.share.ParamValidator;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="codec.cmd.list")
public class CmdListLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		CmdListLogicParam myParam = (CmdListLogicParam)logicParam;

		Integer page = Integer.valueOf(myParam.getPage());
		Integer pageSize = Integer.valueOf(myParam.getPageSize());
		Integer sensorId = Integer.valueOf(myParam.getSensorId());
		
		CmdEncodeDao dao = new CmdEncodeDao(em);
		Long total = dao.findTotal(sensorId);
		List<CmdEncodeEntity> cmdList = dao.findBySensorId(sensorId, page, pageSize);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		cmdList.forEach((E)->{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("cmdId", E.getId());
			map.put("cmdNo", E.getCmdNo());
			map.put("cmdName", E.getCmdName());
			map.put("scriptText", E.getScriptText());
			result.add(map);
		});
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "codec.cmd.list ok！")
			.add("result", result)
			.add("total", total);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		if (!ParamValidator.checkPageParam(
				(CmdListLogicParam)logicParam, res)) {
			return false;
		}
		
		String[][] matrix = new String[][]{
			{"sensorId", "1", "0", "1"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		
		return true;
	}
	

	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		
		// 设置deviceId
		CmdListLogicParam myParam = (CmdListLogicParam)logicParam;
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
