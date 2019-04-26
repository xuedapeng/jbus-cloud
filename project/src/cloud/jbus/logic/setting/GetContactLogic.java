package cloud.jbus.logic.setting;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.db.bean.ContactEntity;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.ContactDao;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.setting.param.GetContactLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="setting.contact.get")
public class GetContactLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		GetContactLogicParam myParam = (GetContactLogicParam)logicParam;
		
		String deviceSn = CommonLogic.getDeviceSnById(toInt(myParam.getDeviceId()), em);
		if (deviceSn == null) {
			res.add("status", -11)
				.add("msg", "设备不存在");
			return false;
		}
		
		ContactDao dao = new ContactDao(em);
		ContactEntity contact = dao.findByDeviceSn(deviceSn);
		
		if(contact == null) {
			res.add("status", 11)
				.add("msg", "没有联系人");
			return false;
		}
		
		res.add("status", 1)
			.add("msg", "setting.contact.get ok.")
			.add("deviceSn", deviceSn)
			.add("email", contact.getEmail());
		
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
