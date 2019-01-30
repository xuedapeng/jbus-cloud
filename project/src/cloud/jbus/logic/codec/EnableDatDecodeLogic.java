package cloud.jbus.logic.codec;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.db.bean.DatDecodeEntity;
import cloud.jbus.db.dao.DatDecodeDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.codec.param.EnableDatDecodeLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.share.codec.DecodeValidator;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="codec.datDecode.enable")
public class EnableDatDecodeLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		EnableDatDecodeLogicParam myParam = (EnableDatDecodeLogicParam)logicParam;
		
		DatDecodeEntity decode = new DatDecodeDao(em).findByDeviceId(toInt(myParam.getDeviceId()));
		if (decode == null) {
			res.add("status", 11)
				.add("msg", "解码器不存在");
			return false;
		}
		
		if ("enable".equals(myParam.getStatus())){
			// 启用时，校验有效性
			String result = DecodeValidator.checkDatDecode(decode);
			if (!StringUtils.isEmpty(result)) {
				res.add("status", -11)
					.add("msg", result);
				return false;
			}
			
			decode.setStatus(StatusConst.STATUS_NORMAL);
		} else {
			decode.setStatus(StatusConst.STATUS_STOP);
		}

		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "解码器获取成功")
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
			{"deviceId", "1", "0", "1"},
			{"enable", "1", "10", "0"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		
		return true;
	}

}
