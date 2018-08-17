package cloud.jbus.logic.realtime.param;

import java.util.List;

import cloud.jbus.logic.BaseZLogicParam;

public class GetOnlineInfoOfDevicesLogicParam extends BaseZLogicParam {

	public GetOnlineInfoOfDevicesLogicParam() {
		
	}
	
	private List<String> deviceIds;

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}

}
