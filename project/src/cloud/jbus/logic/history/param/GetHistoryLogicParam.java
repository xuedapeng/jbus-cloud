package cloud.jbus.logic.history.param;

import cloud.jbus.logic.BaseZLogicParam;

public class GetHistoryLogicParam extends BaseZLogicParam {

	private String deviceSn;
	private String sensorNo;
	private String fromTime;
	private String toTime;
	private String pageSize;
	private String page;
	public String getDeviceSn() {
		return deviceSn;
	}
	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
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
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getFromTime() {
		return fromTime;
	}
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
	
}
