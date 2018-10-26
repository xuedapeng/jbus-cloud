package cloud.jbus.logic.user.param;

import cloud.jbus.logic.BaseZLogicParam;

public class UpdatePwdLogicParam extends BaseZLogicParam {

	
	private String oldPassword;
	private String newPassword;
	
	public UpdatePwdLogicParam() {
		
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	
}
