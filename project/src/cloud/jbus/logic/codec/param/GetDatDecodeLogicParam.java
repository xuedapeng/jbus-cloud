package cloud.jbus.logic.codec.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("view")
public class GetDatDecodeLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	private String deviceId;
	
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	@Override
	public String getDeviceId() {
		return deviceId;
	}

}
