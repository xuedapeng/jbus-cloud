package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class DeviceUpdateLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public DeviceUpdateLogicParam() {
		
	}

	private String deviceId;
	private String deviceName;
	private String longitude;
	private String latitude;
	private Integer crcMode;
	private String memo;
	private String deviceSecretKey;

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public Integer getCrcMode() {
		return crcMode;
	}
	public void setCrcMode(Integer crcMode) {
		this.crcMode = crcMode;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDeviceSecretKey() {
		return deviceSecretKey;
	}
	public void setDeviceSecretKey(String deviceSecretKey) {
		this.deviceSecretKey = deviceSecretKey;
	}
}
