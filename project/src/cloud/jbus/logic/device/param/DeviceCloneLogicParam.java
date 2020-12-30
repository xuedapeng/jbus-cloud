package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class DeviceCloneLogicParam extends BaseZLogicParam  implements IPrivilegedParam {

	public DeviceCloneLogicParam() {
		
	}
	
	private String fromDeviceId;
	private String fromDeviceSn;
	private String deviceId;
	
	public String getFromDeviceId() {
		return fromDeviceId;
	}
	public void setFromDeviceId(String fromDeviceId) {
		this.fromDeviceId = fromDeviceId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getFromDeviceSn() {
		return fromDeviceSn;
	}
	public void setFromDeviceSn(String fromDeviceSn) {
		this.fromDeviceSn = fromDeviceSn;
	}
	
	
	
}
