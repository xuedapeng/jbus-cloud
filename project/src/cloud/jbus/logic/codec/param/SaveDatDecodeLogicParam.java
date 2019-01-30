package cloud.jbus.logic.codec.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("owner")
public class SaveDatDecodeLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	private String deviceId;
	private String scriptText;
	private String resultSchema;
	private String sampleCases;
	
	public String getScriptText() {
		return scriptText;
	}


	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}


	public String getResultSchema() {
		return resultSchema;
	}


	public void setResultSchema(String resultSchema) {
		this.resultSchema = resultSchema;
	}


	public String getSampleCases() {
		return sampleCases;
	}


	public void setSampleCases(String sampleCases) {
		this.sampleCases = sampleCases;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	@Override
	public String getDeviceId() {
		return deviceId;
	}

}
