package cloud.jbus.logic.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.monitor.param.GetMsgLogLogicParam;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.proxy.DbProxy;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

/*
 * 查找消息日志
 */
@Action(method="monitor.msglog.query")
public class GetMsgLogLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		GetMsgLogLogicParam myParam = (GetMsgLogLogicParam)logicParam;
		String deviceSn = myParam.getDeviceSn();
		Date fromTime = DateHelper.fromYmdhm(myParam.getFromTime());
		Date toTime = DateHelper.fromYmdhm(myParam.getToTime());
		
		
		List<Map<String, Object>> resultList = DbProxy.findMsgLogForPeriod(deviceSn, fromTime, toTime, 1000);
		
		res.add("status", 1)
			.add("msg", "查找成功")
			.add("result", resultList);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		GetMsgLogLogicParam myParam = (GetMsgLogLogicParam)logicParam;

		String deviceId = myParam.getDeviceId();
		String deviceSn = myParam.getDeviceSn();
		
		// attachDeviceId for privilege check
		if(StringUtils.isNotEmpty(deviceSn)) {
			deviceId = String.valueOf(CommonLogic.getDeviceIdBySn(deviceSn));
			myParam.setDeviceId(deviceId);
		}
		
		String[][] matrix = new String[][]{
			{"deviceSn", "1", "8", "0"},
			{"fromTime", "1", "16", "0"},
			{"toTime", "1", "16", "0"}
		};

		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}


		Date from = DateHelper.fromYmdhm(myParam.getFromTime());
		
		if (from == null) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
			.add("msg", "参数错误，fromTime必须是日期：" + myParam.getFromTime());
	
			return false;
		}
		
		Date to = DateHelper.fromYmdhm(myParam.getToTime());
		
		if (to == null) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
			.add("msg", "参数错误，toTime必须是日期：" + myParam.getToTime());
	
			return false;
		}
		
		return true;
	}

}
