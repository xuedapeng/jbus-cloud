package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class DeviceSecretKeyResetLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public DeviceSecretKeyResetLogicParam() {
		
	}

	private String deviceId;
	private String ownerPassword;
	private String oldSecretKey;
	
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
	public String getOldSecretKey() {
		return oldSecretKey;
	}
	public void setOldSecretKey(String oldSecretKey) {
		this.oldSecretKey = oldSecretKey;
	}
}
