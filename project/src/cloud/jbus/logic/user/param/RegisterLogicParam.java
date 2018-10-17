package cloud.jbus.logic.user.param;

import cloud.jbus.logic.BaseZLogicParam;

public class RegisterLogicParam extends BaseZLogicParam {

	private String account;
	private String password;
	private String confirmCode;
	
	public RegisterLogicParam() {
		
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}

}
