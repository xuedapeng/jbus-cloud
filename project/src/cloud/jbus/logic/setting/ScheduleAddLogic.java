package cloud.jbus.logic.setting;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.ScheduleEntity;
import cloud.jbus.db.dao.ScheduleDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.setting.param.ScheduleAddLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="setting.schedule.add")
public class ScheduleAddLogic extends BaseZLogic {

	static Logger log = Logger.getLogger(ScheduleAddLogic.class);
	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ScheduleAddLogicParam myParam = (ScheduleAddLogicParam)logicParam;
		
		String deviceSn = CommonLogic.getDeviceSnById(toInt(myParam.getDeviceId()), em);

		if (deviceSn == null) {
			res.add("status", -11)
				.add("msg", "设备不存在");
			
			return false;
		}
		
		ScheduleEntity schedule = new ScheduleEntity();
		schedule.setDeviceId(toInt(myParam.getDeviceId()));
		schedule.setDeviceSn(deviceSn);
		schedule.setCmdHex(myParam.getCmdHex());
		schedule.setDatPtn(myParam.getDatPtn());
		schedule.setInterval(toInt(myParam.getInterval()));
		schedule.setStatus(toInt(myParam.getStatus()));
		
		ScheduleDao dao = new ScheduleDao(em);
		dao.save(schedule);
		
		res.add("status", 1)
			.add("msg", "setting.schedule.add ok.")
			.add("scheduleId", schedule.getId());
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		ScheduleAddLogicParam myParam = (ScheduleAddLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
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
		
		// interval
		if (toInt(myParam.getInterval()).intValue() <= 0) {

			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：interval值无效，必须是正整数");
			
			return false;
		}
		
		
		// datPtn
		String datPtn = myParam.getDatPtn();
		if (!validate4datPtn(datPtn, res)) {
			return false;
		}
		

		ScheduleDao dao = new ScheduleDao(em);
		// 总数check
		Long total = dao.findTotal(Integer.valueOf(myParam.getDeviceId()));
		if (total >= StatusConst.MAX_SCHEDULE_AMOUNT) {

			res.add("status", -11)
				.add("msg", "已达计划任务数上限("+ StatusConst.MAX_SCHEDULE_AMOUNT + ")。");
			
			return false;
		}
		
		return true;
	}
	

	@SuppressWarnings("unchecked")
	public static boolean validate4datPtn(String datPtn, ZSimpleJsonObject res) throws Exception {
		if (StringUtils.isEmpty(datPtn)) {
			return true;
		}

		try {
			List<Object> ptnList = JsonHelper.json2list(datPtn);
			if (ptnList == null ||  ptnList.size() == 0) {
	
				res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
					.add("msg", "参数错误：datPtn无效, 必须是二维整型数组");
				
				return false;
			}

			for(Object ptn: ptnList) {
				int pos = ((List<Double>)ptn).get(0).intValue();
				int val = ((List<Double>)ptn).get(1).intValue();
				if (pos < 0 || val < 0 || val > 255) {

					res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
						.add("msg", "参数错误：datPtn无效, pos／val错误。");
					
					return false;
				}
			}
		} catch(Exception e) {

			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：datPtn无效。(" + e.getMessage() + ")");
			
			log.error("", e);
			return false;
		}
		
		return true;
	}


}
