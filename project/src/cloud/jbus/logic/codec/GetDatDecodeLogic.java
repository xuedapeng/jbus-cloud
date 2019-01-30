package cloud.jbus.logic.codec;

import javax.persistence.EntityManager;

import com.google.common.collect.ImmutableMap;

import cloud.jbus.db.bean.DatDecodeEntity;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.DatDecodeDao;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.codec.param.GetDatDecodeLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="codec.datDecode.get")
public class GetDatDecodeLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		GetDatDecodeLogicParam myParam = (GetDatDecodeLogicParam)logicParam;

		DeviceEntity device = new DeviceDao(em).findById(toInt(myParam.getDeviceId()));
		if (device == null) {	
			res.add("status", -11)
				.add("msg", "设备不存在");
			return false;
		}
		
		DatDecodeEntity decode = new DatDecodeDao(em).findByDeviceId(toInt(myParam.getDeviceId()));
		if (decode == null) {
			res.add("status", 11)
				.add("msg", "没有解码器");
			return false;
		}
		

		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "解码器获取成功")
			.add("deviceInfo", ImmutableMap.of(
					"deviceSn", device.getDeviceSn(),
					"deviceName", device.getDeviceName()
					))
			.add("result", ImmutableMap.of(
					"id", decode.getId(),
					"scriptText", decode.getScriptText(),
					"resultSchema", decode.getResultSchema(),
					"sampleCases", decode.getSampleCases(),
					"status", decode.getStatus()
					));
		
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		
		return true;
	}

}
