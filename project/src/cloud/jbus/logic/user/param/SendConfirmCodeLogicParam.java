package cloud.jbus.logic.user.param;

import cloud.jbus.logic.BaseZLogicParam;

public class SendConfirmCodeLogicParam extends BaseZLogicParam {

	private String account;
	private String func; 
	
	public SendConfirmCodeLogicParam() {
		
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

}
