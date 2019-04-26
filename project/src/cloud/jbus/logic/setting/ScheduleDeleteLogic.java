package cloud.jbus.logic.setting;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.ScheduleEntity;
import cloud.jbus.db.dao.ScheduleDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.setting.param.ScheduleDeleteLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="setting.schedule.delete")
public class ScheduleDeleteLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ScheduleDeleteLogicParam myParam = (ScheduleDeleteLogicParam)logicParam;

		ScheduleDao dao = new ScheduleDao(em);
		ScheduleEntity schedule = dao.findByScheduleId(
				toInt(myParam.getDeviceId()), 
				toInt(myParam.getScheduleId()));
		
		if (schedule == null) {
			res.add("status", -21)
				.add("msg", "计划任务不存在");
			return  false;
		}
		
		dao.delete(schedule);
		
		res.add("status", 1)
			.add("msg", "setting.schedule.delete ok.");
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		ScheduleDeleteLogicParam myParam = (ScheduleDeleteLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"scheduleId", "1", "0", "1"}
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
