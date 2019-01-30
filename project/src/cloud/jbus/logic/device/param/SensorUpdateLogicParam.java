package cloud.jbus.logic.device.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class SensorUpdateLogicParam extends BaseZLogicParam  implements IPrivilegedParam {

	public SensorUpdateLogicParam() {
		
	}

	private String deviceId;
	private String sensorId;
	private String sensorNo;
	private String sensorName;
	private String memo;
	
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

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	

}
