package cloud.jbus.logic.history.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;


@Privilege("view")
public class GetHistoryLogicParam extends BaseZLogicParam  implements IPrivilegedParam {

	private String deviceId;
	private String deviceSn; // 可选
	private String sensorNo;
	private String fromTime;
	private String pageSize;
	private String direction; // -1:过去，1:将来
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getSensorNo() {
		return sensorNo;
	}
	public void setSensorNo(String sensorNo) {
		this.sensorNo = sensorNo;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getDeviceSn() {
		return deviceSn;
	}
	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}
	
	
}
