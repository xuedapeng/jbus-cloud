package cloud.jbus.logic.device;

import javax.persistence.EntityManager;

import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.share.privilege.DevicePrivilege;
import cloud.jbus.logic.device.param.DeviceUpdateLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="device.update")
public class DeviceUpdateLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceUpdateLogicParam myParam = (DeviceUpdateLogicParam)logicParam;

		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findById(Integer.valueOf(myParam.getDeviceId()));
		if (device == null) {
			res.add("status", -10)
			.add("msg", "device identified by id not found.");
			
			return false;
		}
		if (myParam.getDeviceName() != null) {
			device.setDeviceName(myParam.getDeviceName());
		}
		
		if(myParam.getCrcMode() != null) {
			device.setCrcMode(myParam.getCrcMode());
		}

		if(myParam.getLongitude() != null) {
			device.setLongitude(myParam.getLongitude());
		}
		
		if(myParam.getLatitude() != null) {
			device.setLatitude(myParam.getLatitude());
		}
		
		if(myParam.getMemo() != null) {
			device.setMemo(myParam.getMemo());
		}
		
		dao.save(device);
		
		res.add("status", 1)
			.add("msg", "device.update ok.")
			.add("id", device.getId());
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		DeviceUpdateLogicParam myParam = (DeviceUpdateLogicParam)logicParam;
		
		if(myParam.getDeviceId() == null) {
			
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
	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) {
		if (!super.auth(logicParam, res, em)) {
			return false;
		}

		DeviceUpdateLogicParam myParam = (DeviceUpdateLogicParam)logicParam;

		if (!DevicePrivilege.canConfig(
				Integer.valueOf(myParam.getDeviceId()), 
				getLoginUserId(myParam.getSecretId()), 
				em)) {

			res.add("status", -2)
				.add("msg", "privilege not enough.");
			
			return false;
		}
		
		return true;
	}


}
