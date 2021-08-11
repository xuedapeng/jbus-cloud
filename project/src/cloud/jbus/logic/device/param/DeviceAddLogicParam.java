package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;

public class DeviceAddLogicParam extends BaseZLogicParam {

	public DeviceAddLogicParam() {
		
	}
	
	private String deviceName;
	private String longitude;
	private String latitude;
	private Integer crcMode;
	private String memo;
	private String category;
	
	
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
