package cloud.jbus.logic.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
                                                                                                         
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.device.param.SensorListLogicParam;
import cloud.jbus.logic.share.ParamValidator;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="sensor.list")
public class SensorListLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SensorListLogicParam myParam = (SensorListLogicParam)logicParam;
		Integer page = Integer.valueOf(myParam.getPage());
		Integer pageSize = Integer.valueOf(myParam.getPageSize());
		Integer deviceId = Integer.valueOf(myParam.getDeviceId());
		
		SensorDao dao = new SensorDao(em);
		Long total = dao.findTotal(deviceId);
		List<SensorEntity> sensorList = dao.findByDeviceId(deviceId, page, pageSize);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		sensorList.forEach((E)->{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sensorId", E.getId());
			map.put("sensorNo", E.getSensorNo());
			map.put("sensorName", E.getSensorName());
			map.put("memo", E.getMemo());
			result.add(map);
		});
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "传感器查询成功！")
			.add("total", total)
			.add("result", result);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SensorListLogicParam myParam = (SensorListLogicParam)logicParam;
		
		if (!ParamValidator.checkPageParam(myParam, res)) {
			return false;
		}
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"}
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
