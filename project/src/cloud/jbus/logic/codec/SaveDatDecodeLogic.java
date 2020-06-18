package cloud.jbus.logic.codec;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.db.bean.DatDecodeEntity;
import cloud.jbus.db.dao.DatDecodeDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.codec.param.SaveDatDecodeLogicParam;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.share.codec.DecodeValidator;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="codec.datDecode.save")
public class SaveDatDecodeLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SaveDatDecodeLogicParam myParam = (SaveDatDecodeLogicParam)logicParam;

		Integer deviceId = toInt(myParam.getDeviceId());
		String scriptText = myParam.getScriptText();
		String resultSchema = myParam.getResultSchema();
		String sampleCases = myParam.getSampleCases();
		
		DatDecodeDao dao = new DatDecodeDao(em);
		DatDecodeEntity decode = dao.findByDeviceId(deviceId);
		if (decode == null) {
			decode = new DatDecodeEntity();
			decode.setDeviceId(deviceId);
			decode.setDeviceSn(CommonLogic.getDeviceSnById(deviceId, em));
			decode.setStatus(StatusConst.STATUS_NORMAL);
		}
		
		decode.setScriptText(scriptText);
		decode.setResultSchema(resultSchema);
		decode.setSampleCases(sampleCases);
		
		dao.save(decode);
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "解码器保存成功");
		
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"scriptText", "1", "100000", "0"},
			{"resultSchema", "1", "100000", "0"},
			{"sampleCases", "1", "5000", "0"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		

		SaveDatDecodeLogicParam myParam = (SaveDatDecodeLogicParam)logicParam;
		
		String scriptText = myParam.getScriptText();
		String resultSchema = myParam.getResultSchema();
		String sampleCases = myParam.getSampleCases();
		
		DatDecodeEntity decode = new DatDecodeEntity();
		decode.setScriptText(scriptText);
		decode.setResultSchema(resultSchema);
		decode.setSampleCases(sampleCases);
		
		String result = DecodeValidator.checkDatDecode(decode);

		if (!StringUtils.isEmpty(result)) {
			res.add("status", -11)
				.add("msg", result);
			return false;
		}
		
		return true;
	}

}
