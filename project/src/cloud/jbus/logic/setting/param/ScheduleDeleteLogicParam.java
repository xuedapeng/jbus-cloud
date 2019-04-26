package cloud.jbus.logic.setting.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class ScheduleDeleteLogicParam extends BaseZLogicParam  implements IPrivilegedParam {

	public ScheduleDeleteLogicParam() {
		
	}

	private String deviceId;
	private String scheduleId;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
}
