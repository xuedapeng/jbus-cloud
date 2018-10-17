package cloud.jbus.logic.share;

import java.util.Date;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.bean.UserRegEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.db.dao.UserRegDao;
import cloud.jbus.logic.user.SendConfirmCodeLogic;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZDbProcessor;

public class CommonLogic {

	public static boolean checkConfirmCode(String account, String confirmCode, ZSimpleJsonObject res, EntityManager em) {

		
		// 验证码check
		UserRegDao regDao = new UserRegDao(em);
		UserRegEntity userReg = regDao.findByAccount(account);
		
		if (userReg == null) {

			res.add("status", -12)
				.add("msg", "验证失败：验证码不存在。");
			
			return false;
		}
		
		if (new Date().getTime() > userReg.getExpireTime().getTime() ) {

			res.add("status", -12)
				.add("msg", "验证失败：验证码过期。");
			
			return false;
		}
		
		if (userReg.getStatus().equals(StatusConst.CODE_INVALID)) {

			res.add("status", -12)
				.add("msg", "验证失败：验证码无效。");
			
			return false;
		}
		
		if(!confirmCode.toLowerCase().equals(userReg.getConfirmCode().toLowerCase())) {
			
			increaseRetryTimes(account);
			
			res.add("status", -12)
				.add("msg", "验证失败：验证码错误。");
			
			return false;
		}
		
		return true;
	}
	

	private static void increaseRetryTimes(String account) {
		new ZDbProcessor() {

			@Override
			public void execute(EntityManager em) {

				UserRegDao regDao = new UserRegDao(em);
				UserRegEntity userReg = regDao.findByAccount(account);
				
				userReg.setRetryTimes(userReg.getRetryTimes()+1);
				
				if (userReg.getRetryTimes() > SendConfirmCodeLogic._maxRetryTimes) {
					userReg.setStatus(StatusConst.CODE_INVALID);
				}
				
				regDao.save(userReg);
				
			}
			
		}.run();
	}
}
