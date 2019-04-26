package cloud.jbus.logic.setting.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class ScheduleAddLogicParam extends BaseZLogicParam  implements IPrivilegedParam {

	public ScheduleAddLogicParam() {
		
	}

	private String deviceId;
	private String cmdHex;
	private String datPtn;
	private String interval;
	private String status;

	public String getCmdHex() {
		return cmdHex;
	}

	public void setCmdHex(String cmdHex) {
		this.cmdHex = cmdHex;
	}

	public String getDatPtn() {
		return datPtn;
	}

	public void setDatPtn(String datPtn) {
		this.datPtn = datPtn;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String getDeviceId() {

		return deviceId;
	}
	

}
