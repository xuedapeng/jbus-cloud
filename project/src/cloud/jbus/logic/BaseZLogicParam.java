package cloud.jbus.logic;

import fw.jbiz.logic.ZLogicParam;

public class BaseZLogicParam extends ZLogicParam {

	public BaseZLogicParam() {
		super(null, null);
	}
	
	private String secretId;
	private String secretKey;
	
	public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	

}
