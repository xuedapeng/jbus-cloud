package cloud.jbus.logic.setting;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.ContactEntity;
import cloud.jbus.db.dao.ContactDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.setting.param.SaveContactLogicParam;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="setting.contact.save")
public class SaveContactLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SaveContactLogicParam myParam = (SaveContactLogicParam)logicParam;

		ContactDao dao = new ContactDao(em);
		
		Integer deviceId = toInt(myParam.getDeviceId());
		String deviceSn = CommonLogic.getDeviceSnById(deviceId, em);
		
		if (deviceSn == null) {
			res.add("status", -11)
				.add("msg", "设备不存在");
			
			return false;
		}
		
		ContactEntity contact = null;
		contact = dao.findByDeviceSn(deviceSn);
		
		if (contact == null) {
			contact = new ContactEntity();
			contact.setDeviceSn(deviceSn);
			contact.setEvent(15); // 1111
			contact.setStatus(StatusConst.STATUS_NORMAL);
		}
		
		contact.setEmail(myParam.getEmail());
		dao.save(contact);
		
		res.add("status", IResponseObject.RSP_CD_OK)
			.add("msg", "联系人保存成功");
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SaveContactLogicParam myParam = (SaveContactLogicParam)logicParam;
		
		String[][] matrix = new String[][]{
			{"deviceId", "1", "0", "1"},
			{"email", "0", "5000", "0"}
		};
		
		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		
		String email = myParam.getEmail();
		if (StringUtils.isNotEmpty(email)) {
			email = email.trim().replaceAll(" ", "").replaceAll("\n", "");
			// email check
			String result = ValidateHelper.emailCheck(email.split(","));
			if (StringUtils.isNotEmpty(result)) {

				res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
					.add("msg", "email格式不正确。" + result);
				return false;
			}
		}
		myParam.setEmail(email);
		
		return true;
	}

}
