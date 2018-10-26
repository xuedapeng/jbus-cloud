package cloud.jbus.logic.device;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.DeviceDeleteLogicParam;
import cloud.jbus.logic.device.param.DeviceSecretKeyResetLogicParam;
import cloud.jbus.logic.device.param.DeviceStopLogicParam;
import cloud.jbus.logic.device.param.DeviceUpdateLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="device.delete")
public class DeviceDeleteLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceDeleteLogicParam myParam = (DeviceDeleteLogicParam)logicParam;

		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findById(Integer.valueOf(myParam.getDeviceId()));
		if (device == null) {
			res.add("status", -10)
			.add("msg", "device identified by id not found.");
			
			return false;
		}
		
		dao.delete(device);
		
		res.add("status", 1)
			.add("msg", "device.delete ok.")
			.add("deviceId", device.getId());
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		DeviceDeleteLogicParam myParam = (DeviceDeleteLogicParam)logicParam;

		if(!checkNotEmpty(
				myParam.getDeviceId()
				)) {
			
			res.add("status", -3)
				.add("msg", "必须字段未设置.");
			
			return false;
		}
		
		if (!NumericHelper.isInteger(myParam.getDeviceId())) {

			res.add("status", -3)
				.add("msg", "invalid id.");
			
			return false;
		}
		
		return true;
	}

}
