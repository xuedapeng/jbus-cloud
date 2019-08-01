package cloud.jbus.logic.realtime.param;

import cloud.jbus.logic.BaseZLogicParam;

public class SendCmdLogicParam extends BaseZLogicParam {

	public SendCmdLogicParam() {
		
	}
	
	private String deviceSn;
	private String cmd;
	
	public String getDeviceSn() {
		return deviceSn;
	}
	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
}
