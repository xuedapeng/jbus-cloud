package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class DeviceDeleteLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public DeviceDeleteLogicParam() {
		
	}

	private String deviceId;

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
