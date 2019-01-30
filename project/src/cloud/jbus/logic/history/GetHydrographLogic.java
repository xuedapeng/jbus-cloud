package cloud.jbus.logic.history;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.DatDecodeEntity;
import cloud.jbus.db.dao.DatDecodeDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.history.param.GetHydrographLogicParam;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.proxy.DbProxy;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

/*
 * 生成过程线数据，自动根据期间长度适配时间单位（年、月、周、日、时、分)的平均值
 * 
 */
@Action(method="hydrograph.data.query")
public class GetHydrographLogic extends BaseZLogic {

	@SuppressWarnings("unchecked")
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		GetHydrographLogicParam myParam = (GetHydrographLogicParam)logicParam;
		Integer deviceId = Integer.valueOf(myParam.getDeviceId());
		String sensorNo = myParam.getSensorNo();
		Date fromTime = DateHelper.fromYmdhms(myParam.getFromTime());
		Date toTime = DateHelper.fromYmdhms(myParam.getToTime());
		String  deviceSn = CommonLogic.getDeviceSnById(deviceId, em);
		
		System.out.println(myParam.dumpParams());
		
		DatDecodeDao decodeDao = new DatDecodeDao(em);
		DatDecodeEntity decode = decodeDao.findByDeviceId(deviceId);
		
		//校验：decode存在
		if (decode == null || decode.getResultSchema() == null) {
			res.add("status", -21)
				.add("msg", "解码器错误。");
			return false;
		}
		
		String resultSchema = decode.getResultSchema();
		Map<String, Object> scheMap = JsonHelper.json2map(resultSchema);
		
		// 校验：resultSchema中sno存在
		if (!scheMap.containsKey(sensorNo)) {
			res.add("status", -22)
				.add("msg", "解码器中缺少sno(" + sensorNo + ")");
			return false;
		}
		
		Map<String, Map<String, String>> fieldStyle = (Map<String, Map<String, String>>) ((Map<String, Object>)scheMap.get(sensorNo.toString())).get("field");
		String type = (String) ((Map<String, Object>)scheMap.get(sensorNo.toString())).get("type");
		
		if (!"metric".equals(type) || fieldStyle == null){
			res.add("status", -23)
				.add("msg", "解码器模式错误");
			return false;
		}
		
		
		DbProxy.TIME_UNIT unit = getUnit(fromTime, toTime);
		List<Map<String, Object>> result = DbProxy.findForAvg(deviceSn, Integer.valueOf(sensorNo), fromTime, toTime, unit, fieldStyle);
		
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
			.add("unit", unit)
			.add("result", resultMap);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		GetHydrographLogicParam myParam = (GetHydrographLogicParam)logicParam;
		String deviceId = myParam.getDeviceId();
		String sensorNo = myParam.getSensorNo();
		String fromTime = myParam.getFromTime();
		String toTime = myParam.getToTime();
		
		String result = ValidateHelper.notEmptyCheck(
				"deviceId",deviceId, 
				"sensorNo", sensorNo, 
				"fromTime", fromTime, 
				"toTime",toTime);
		
		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数缺失：" + result);
			
			return false;
		}
		
		result = ValidateHelper.integerCheck("deviceId", deviceId, "sensorNo", sensorNo);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误, 必须是数值：" + result);
		
			return false;
		}
		
		Date from = DateHelper.fromYmdhms(myParam.getFromTime());
		Date to = DateHelper.fromYmdhms(myParam.getToTime());
		
		if (from == null || to == null || !from.before(to)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
			.add("msg", "参数错误，必须是日期：" + result);
	
			return false;
		}
		
		
		return true;
	}

	@Override
   protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		// todo: no auth for test
		return true;
	}
	

	/*
	 * 期间            时间单位   数据量           期间小时数
	 * 1小时            1分      60 				1
	 * 1小时～1天       10分 		7-144			1～24
	 * 1天～7天         小时		25-168			24～7*24
	 * 7天-90天          日		8-90			7*24～90*24
	 * 90天－3年         周		12-156			90*24～3*365*24
	 * 3年－10年         月		36-120			3*365*24～10*365*24
	 * 10年以上          年       10～100（100年）  10*365*24～
	 */
	private DbProxy.TIME_UNIT getUnit(Date from, Date to) {
		
		long periodHours = (to.getTime() - from.getTime())/1000/60/60;
		if (periodHours <= 1) {
			return DbProxy.TIME_UNIT.MINUTE;
		}
		
		if (periodHours > 1 && periodHours <= 24) {
			return DbProxy.TIME_UNIT.TEN_MINUTE;
		}

		if (periodHours > 24 && periodHours <= 7*24) {
			return DbProxy.TIME_UNIT.HOUR;
		}

		if (periodHours > 7*24 && periodHours <= 90*24) {
			return DbProxy.TIME_UNIT.DAY;
		}

		if (periodHours > 90*24 && periodHours <= 3*365*24) {
			return DbProxy.TIME_UNIT.WEEK;
		}

		if (periodHours > 3*365*24 && periodHours <= 10*365*24) {
			return DbProxy.TIME_UNIT.MONTH;
		}

		// periodHours > 10*365*24) 
		return DbProxy.TIME_UNIT.YEAR;
		
	}

}
