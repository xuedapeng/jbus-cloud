package cloud.jbus.logic.codec.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class EnableDatDecodeLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	private String deviceId;
	private String status; // enable, disable
	
	

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
