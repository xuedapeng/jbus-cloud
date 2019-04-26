package cloud.jbus.logic.setting.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("view")
public class GetContactLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public GetContactLogicParam() {
		
	}
	
	private String deviceId;
	
	

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}
	
	
}
