package cloud.jbus.logic.setting;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.ScheduleEntity;
import cloud.jbus.db.bean.SensorEntity;
import cloud.jbus.db.dao.ScheduleDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.SensorUpdateLogicParam;
import cloud.jbus.logic.setting.param.ScheduleUpdateLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="setting.schedule.update")
public class ScheduleUpdateLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ScheduleUpdateLogicParam myParam = (ScheduleUpdateLogicParam)logicParam;

		ScheduleDao dao = new ScheduleDao(em);
		ScheduleEntity schedule = dao.findByScheduleId(
				toInt(myParam.getDeviceId()), 
				toInt(myParam.getScheduleId()));
		
		if (schedule == null) {
			res.add("status", -21)
				.add("msg", "计划任务不存在");
			return  false;
		}
		
		schedule.setCmdHex(myParam.getCmdHex());
		schedule.setDatPtn(myParam.getDatPtn());
		schedule.setInterval(toInt(myParam.getInterval()));
		schedule.setStatus(toInt(myParam.getStatus()));
		
		dao.save(schedule);
		
		res.add("status", 1)
			.add("msg", "setting.schedule.update ok.");
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		ScheduleUpdateLogicParam myParam = (ScheduleUpdateLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"scheduleId", "1", "0", "1"},
			{"cmdHex", "1", "255", "0"},
			{"datPtn", "0", "1000", "0"},
			{"interval", "1", "0", "1"},
			{"status", "1", "0", "1"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		

		// 状态：1 or 9
		Integer status = toInt(myParam.getStatus());
		if (!status.equals(StatusConst.STATUS_NORMAL) && !status.equals(StatusConst.STATUS_STOP)) {

			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：status值无效");
			
			return false;
		}
		
		// datPtn
		String datPtn = myParam.getDatPtn();
		if (!ScheduleAddLogic.validate4datPtn(datPtn, res)) {
			return false;
		}
		
		return true;
	}


}
