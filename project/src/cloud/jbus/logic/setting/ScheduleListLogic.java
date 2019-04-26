package cloud.jbus.logic.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
                                                                                                         
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.ScheduleEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.ScheduleDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.device.param.SensorListLogicParam;
import cloud.jbus.logic.setting.param.ScheduleListLogicParam;
import cloud.jbus.logic.share.ParamValidator;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="setting.schedule.list")
public class ScheduleListLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ScheduleListLogicParam myParam = (ScheduleListLogicParam)logicParam;
		Integer page = Integer.valueOf(myParam.getPage());
		Integer pageSize = Integer.valueOf(myParam.getPageSize());
		Integer deviceId = Integer.valueOf(myParam.getDeviceId());
		
		ScheduleDao dao = new ScheduleDao(em);
		Long total = dao.findTotal(deviceId);
		List<ScheduleEntity> scheduleList = dao.findByDeviceId(deviceId, page, pageSize);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		scheduleList.forEach((E)->{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("scheduleId", E.getId());
			map.put("deviceSn", E.getDeviceSn());
			map.put("cmdHex", E.getCmdHex());
			map.put("datPtn", E.getDatPtn());
			map.put("interval", E.getInterval());
			map.put("status", E.getStatus());
			result.add(map);
		});
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "计划任务查询成功！")
			.add("total", total)
			.add("result", result);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ScheduleListLogicParam myParam = (ScheduleListLogicParam)logicParam;
		
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
