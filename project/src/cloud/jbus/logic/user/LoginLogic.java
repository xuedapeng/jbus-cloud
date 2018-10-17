package cloud.jbus.logic.user;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.EmqUserEntity;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.EmqUserDao;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.user.param.LoginLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="user.login")
public class LoginLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		LoginLogicParam myParam = (LoginLogicParam)logicParam;
		
		String account = myParam.getAccount();
		String password = myParam.getPassword();
		
		// find db
		UserEntity user = new UserDao(em).findByAccount(account);
		
		if (user == null) {
			res.add("status", -11)
			.add("msg", "用户不存在");
			
			return false;
		}
		
		if (!Md5SaltTool.validPassword(password, user.getPassword())) {

			res.add("status", -12)
			.add("msg", "密码错误");
			
			return false;
			
		}
		
		EmqUserEntity emqUser = new EmqUserDao(em).findById(user.getId());
		
		
		res.add("status", 1)
			.add("msg", "ok")
			.add("userId", user.getId())
			.add("account", user.getAccount())
			.add("secretId", user.getSecretId())
			.add("secretKey", user.getSecretKey())
			.add("nickName", user.getNickName())
			.add("emqUser", emqUser.getUsername())
			.add("emqPwd", emqUser.getPassword());

		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		LoginLogicParam myParam = (LoginLogicParam)logicParam;
		
		String account = myParam.getAccount();
		String password = myParam.getPassword();
		
		if (StringUtils.isEmpty(account)
				|| StringUtils.isEmpty(password)) {
			
			res.add("status", -3)
				.add("msg", "参数错误");
			
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
