package cloud.jbus.logic.device;

import javax.persistence.EntityManager;

import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.DeviceSecretKeyResetLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="device.secretkey.reset")
public class DeviceSecretKeyResetLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceSecretKeyResetLogicParam myParam = (DeviceSecretKeyResetLogicParam)logicParam;

		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findById(Integer.valueOf(myParam.getDeviceId()));
		if (device == null) {
			res.add("status", -10)
			.add("msg", "device identified by id not found.");
			
			return false;
		}

		device.setSecretKey(GuidHelper.genUUID().substring(0, 10));
		
		dao.save(device);
		
		res.add("status", 1)
			.add("msg", "device.secretkey.reset ok.")
			.add("deviceId", device.getId())
			.add("newSecretKey", device.getSecretKey());
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		DeviceSecretKeyResetLogicParam myParam = (DeviceSecretKeyResetLogicParam)logicParam;
		
		if(!checkNotEmpty(
				myParam.getDeviceId(),
				myParam.getOldSecretKey(),
				myParam.getOwnerPassword()
				)) {
			
			res.add("status", -3)
				.add("msg", "必须字段未设置.");
			
			return false;
		}
		
		if (!NumericHelper.isInteger(myParam.getDeviceId())) {

			res.add("status", -3)
				.add("msg", "invalid deviceId.");
			
			return false;
		}
		
		return true;
	}

	@Override
   protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		DeviceSecretKeyResetLogicParam myParam = (DeviceSecretKeyResetLogicParam)logicParam;
		
		// 验证用户密码
		UserDao userDao = new UserDao(em);
		UserEntity user = userDao.findBySecretId(myParam.getSecretId());
		if (user == null 
				|| !Md5SaltTool.validPassword(myParam.getOwnerPassword(), user.getPassword())) {

			res.add("status", -2)
				.add("msg", "owner password incorrect.");
			
			return false;
		}
		
		// 验证旧通讯密码
		DeviceDao deviceDao = new DeviceDao(em);
		DeviceEntity device = deviceDao.findById(Integer.valueOf(myParam.getDeviceId()));
		if (!myParam.getOldSecretKey().equals(device.getSecretKey())) {

			res.add("status", -2)
				.add("msg", "old secretKey incorrect.");
			
			return false;
		}
		
		return true;
	}
}
