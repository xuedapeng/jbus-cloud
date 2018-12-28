package cloud.jbus.logic.monitor.param;

import cloud.jbus.logic.BaseZLogicParam;

public class GetEventLogicParam extends BaseZLogicParam {

	public GetEventLogicParam() {
		
	}
	
	private String deviceKey;
	private String onlyLast;
	
	public String getDeviceKey() {
		return deviceKey;
	}
	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}
	public String getOnlyLast() {
		return onlyLast;
	}
	public void setOnlyLast(String onlyLast) {
		this.onlyLast = onlyLast;
	}
}
