package cloud.jbus.logic.admin.param;

import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;

@Privilege("admin")
public class AddUserLogicParam extends BaseZLogicParam implements IPrivilegedParam {

	public AddUserLogicParam() {
		
	}

	private String account;
	private String nickName;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}



	@Override
	public String getDeviceId() {

		return "0";
	}

	
}
