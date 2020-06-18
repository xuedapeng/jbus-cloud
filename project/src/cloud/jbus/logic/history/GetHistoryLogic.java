package cloud.jbus.logic.history;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.history.param.GetHistoryLogicParam;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.proxy.DbProxy;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

/*
 * 查找历史数据
 */
@Action(method="history.data.query")
public class GetHistoryLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		GetHistoryLogicParam myParam = (GetHistoryLogicParam)logicParam;
		Integer deviceId = Integer.valueOf(myParam.getDeviceId());
		Integer sensorNo = Integer.valueOf(myParam.getSensorNo());
		Date fromTime = myParam.getFromTime()==null?null:DateHelper.fromYmdhms(myParam.getFromTime());
		Integer direction = myParam.getDirection().equals("1")?1:-1; // 默认值－1
		Integer pageSize = Integer.valueOf(myParam.getPageSize());
		
		String  deviceSn = CommonLogic.getDeviceSnById(deviceId, em);
		
		Map<String, Map<String, String>> fieldStyle = GetHydrographLogic.getFieldStyle(deviceId, myParam.getSensorNo(), res, em);
		if (fieldStyle == null) {
			return false;
		}
		
		List<Map<String, Object>> result = DbProxy.findForTimeline(deviceSn, sensorNo, fromTime, direction, pageSize, fieldStyle);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("time", result.stream().map(m->m.get("time")).collect(Collectors.toList()));
		
		
		fieldStyle.keySet().forEach((K)->{
			resultMap.put(K, result.stream().map(m->m.get(K)).collect(Collectors.toList()));
		});
		
		res.add("status", 1)
			.add("msg", "查找成功")
			.add("deviceId", Integer.valueOf(deviceId))
			.add("deviceSn", deviceSn)
			.add("deviceName", CommonLogic.getDeviceNameById(deviceId, em))
			.add("sensorNo", Integer.valueOf(sensorNo))
			.add("sensorName", CommonLogic.getSensorNameByNo(deviceId, Integer.valueOf(sensorNo), em))
			.add("fieldStyle", fieldStyle)
			.add("result", resultMap);
		
		return true;
	}

//	@Override
//	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
//		// todo: no auth for test
//		return true;
//	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		GetHistoryLogicParam myParam = (GetHistoryLogicParam)logicParam;
		
		if (myParam.getPageSize() == null) {
			myParam.setPageSize(String.valueOf(StatusConst.DEFAULT_PAGE_SIZE));
		}
		if (myParam.getDirection() == null) {
			myParam.setDirection(String.valueOf(-1)); // 默认fromTime为最新时间，向前翻页
		}
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"sensorNo", "1", "0", "1"},
			{"fromTime", "0", "19", "0"},
			{"pageSize", "0", "0", "1"},
			{"direction", "0", "0", "1"}
		};

		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}

		if (myParam.getFromTime() != null) {

			Date from = DateHelper.fromYmdhms(myParam.getFromTime());
			
			if (from == null) {
				res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误，fromTime必须是日期：" + myParam.getFromTime());
		
				return false;
			}
		}
		
		return true;
	}

}
