package cloud.jbus.logic.user;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.common.utils.FormatChecker;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.bean.UserRegEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.db.dao.UserRegDao;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.user.param.ResetPwdLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="user.password.reset")	
public class ResetPwdLogic extends BaseZLogic {

	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ResetPwdLogicParam myParam = (ResetPwdLogicParam)logicParam;
		
		UserDao dao = new UserDao(em);
		UserEntity user = dao.findByAccount(myParam.getAccount());
		user.setPassword(Md5SaltTool.getEncryptedPwd(myParam.getPassword()));
		dao.save(user);

		UserRegDao regDao = new UserRegDao(em);
		UserRegEntity userReg = regDao.findByAccount(myParam.getAccount());
		userReg.setStatus(StatusConst.CODE_CONFIRMED);
		regDao.save(userReg);
		
		res.add("status", 1)
			.add("msg", "密码重置成功！");
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ResetPwdLogicParam myParam = (ResetPwdLogicParam)logicParam;
		
		String account = myParam.getAccount();
		String password = myParam.getPassword();
		String confirmCode = myParam.getConfirmCode();

		String checkResult = ValidateHelper.notEmptyCheck(
				"用户名", account,
				"新密码", password,
				"验证码", confirmCode
				);
		// 必须
		if (!checkResult.isEmpty()) {
			
			res.add("status", -3)
				.add("msg", "必填项目缺失：" + checkResult);
			
			return false;
		}
		
		// email 格式check
		if (!FormatChecker.email(account)) {

			res.add("status", -3)
				.add("msg", "email格式错误。");
			
			return false;
		}

		// 不存在check
		UserEntity user = new UserDao(em).findByAccount(account);
		if (user == null) {

			res.add("status", -11)
				.add("msg", "用户名（email）不存在。");
			
			return false;
		}
		
		if (user.getStatus().equals(StatusConst.STATUS_STOP)) {

			res.add("status", -11)
				.add("msg", "用户名已停用。");
			
			return false;
		}
		
		// 验证码check
		if (!CommonLogic.checkConfirmCode(account, confirmCode, res, em)) {
			return false;
		}
		
		return true;
	}

	
   /*
    * 身份认证
    */
	@Override
   protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		return true;
	}
	

}
