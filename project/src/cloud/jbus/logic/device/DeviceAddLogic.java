package cloud.jbus.logic.device;

import javax.persistence.EntityManager;

import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.DeviceAddLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="device.add")
public class DeviceAddLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeviceAddLogicParam myParam = (DeviceAddLogicParam)logicParam;
		
		DeviceEntity device = new DeviceEntity();
		device.setDeviceSn(GuidHelper.genSeq8());
		device.setSecretKey(GuidHelper.genUUID().substring(0, 10));
		device.setDeviceName(myParam.getDeviceName());
		device.setOwnerId(getLoginUserId(myParam.getUserId()));
		if(myParam.getCrcMode() != null) {
			device.setCrcMode(myParam.getCrcMode());
		}
		device.setLongitude(myParam.getLongitude());
		device.setLatitude(myParam.getLatitude());
		device.setMemo(myParam.getMemo());
		
		DeviceDao dao = new DeviceDao(em);
		dao.save(device);
		
		res.add("status", 1)
			.add("msg", "device.add ok.")
			.add("id", device.getId())
			.add("deviceId", device.getDeviceSn())
			.add("secretKey", device.getSecretKey());
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		DeviceAddLogicParam myParam = (DeviceAddLogicParam)logicParam;
		
		if(!checkNotEmpty(
				myParam.getDeviceName()
				)) {
			
			res.add("status", -3)
				.add("msg", "必须字段未设置.");
			
			return false;
		}
		
		return true;
	}


}
