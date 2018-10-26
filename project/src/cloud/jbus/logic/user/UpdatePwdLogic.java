package cloud.jbus.logic.user;

import javax.persistence.EntityManager;

import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.user.param.UpdatePwdLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="user.password.update")	
public class UpdatePwdLogic extends BaseZLogic {
	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		UpdatePwdLogicParam myParam = (UpdatePwdLogicParam)logicParam;
		
		UserDao dao = new UserDao(em);
		UserEntity user = dao.findBySecretId(myParam.getSecretId());
		user.setPassword(Md5SaltTool.getEncryptedPwd(myParam.getNewPassword()));
		dao.save(user);
		
		res.add("status", 1)
			.add("msg", "密码修改成功！");
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		UpdatePwdLogicParam myParam = (UpdatePwdLogicParam)logicParam;
		
		String oldPassword = myParam.getOldPassword();
		String newPassword = myParam.getNewPassword();

		String checkResult = ValidateHelper.notEmptyCheck(
				"旧密码", oldPassword,
				"新密码", newPassword
				);
		// 必须
		if (!checkResult.isEmpty()) {
			
			res.add("status", -3)
				.add("msg", checkResult);
			
			return false;
		}

		// 旧密码check
		UserEntity user = new UserDao(em).findBySecretId(myParam.getSecretId());
		if (user == null || !Md5SaltTool.validPassword(oldPassword, user.getPassword())) {

			res.add("status", -11)
				.add("msg", "旧密码错误。");
			
			return false;
		}
		
		return true;
	}
	

}
