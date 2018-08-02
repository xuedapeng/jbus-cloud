package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class DeviceStopLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public DeviceStopLogicParam() {
		
	}

	private String deviceId;
	private String ownerPassword;

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getOwnerPassword() {
		return ownerPassword;
	}
	public void setOwnerPassword(String ownerPassword) {
		this.ownerPassword = ownerPassword;
	}
	
}
