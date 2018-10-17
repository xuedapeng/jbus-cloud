package cloud.jbus.logic.user;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.utils.FormatChecker;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.bean.UserRegEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.db.dao.UserRegDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.user.param.LoginLogicParam;
import cloud.jbus.logic.user.param.SendConfirmCodeLogicParam;
import fw.jbiz.ext.comms.mail.ZMailManager;
import fw.jbiz.ext.comms.mail.bean.ZMailBean;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

@Action(method="user.confirmcode.send")	
public class SendConfirmCodeLogic extends BaseZLogic {

	public static final int _interverTime = 30000; // 30秒
	public static final int _validPeriod = 600000; // 10分钟
	public static final int _maxRetryTimes = 10; // 可以试探10次
	
	String FUNC_REG = "reg";
	String FUNC_PWD = "pwd";
	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SendConfirmCodeLogicParam myParam = (SendConfirmCodeLogicParam)logicParam;
		
		String account = myParam.getAccount();
		
		UserRegDao dao = new UserRegDao(em);
		UserRegEntity userReg = dao.findByAccount(account);
		if (userReg == null) {
			userReg = new UserRegEntity();
			userReg.setAccount(account);
		}

		Date now = new Date();
		userReg.setConfirmCode(GuidHelper.genUUID().substring(0, 6));
		userReg.setCodeCreateTime(now);	
		userReg.setExpireTime(new Date(now.getTime()+_validPeriod));
		userReg.setRetryTimes(0);
		userReg.setStatus(StatusConst.CODE_NOT_CONFIRMED);
		
		dao.save(userReg);
		
		ZMailBean mailBean = new ZMailBean();
		mailBean.setAddress(account);
		mailBean.setSubject("Moqbus 注册验证码");
		mailBean.setContent(
				String.format("验证码: %s , %s", userReg.getConfirmCode() , "10分钟内有效"));
		mailBean.setFromNickname("moqbus cloud");
		mailBean.setFromAddressDisp("xuedp@bz12306.com");
		ZMailManager.send(mailBean);
		
		res.add("status", 1)
			.add("msg", "验证码已发送到邮箱:"+account);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SendConfirmCodeLogicParam myParam = (SendConfirmCodeLogicParam)logicParam;
		
		String account = myParam.getAccount();

		// 必须
		if (StringUtils.isEmpty(account)) {
			
			res.add("status", -3)
				.add("msg", "account必须。");
			
			return false;
		}
		
		// email 格式check
		if (!FormatChecker.email(account)) {

			res.add("status", -3)
				.add("msg", "email格式错误。");
			
			return false;
		}
		
		// 已／不存在check
		UserEntity user = new UserDao(em).findByAccount(account);
		if (FUNC_REG.equals(myParam.getFunc())) {
			
			if (user != null) {

				res.add("status", -11)
					.add("msg", "用户名（email）已存在。");
				return false;
			}
		} else if (FUNC_PWD.equals(myParam.getFunc())) {
			
			if (user == null) {

				res.add("status", -11)
					.add("msg", "用户名（email）不存在。");
				return false;
			}
		} else {

			res.add("status", -11)
				.add("msg", "func 参数错误。");
			return false;
		}
		
		// 时间间隔30秒
		UserRegEntity userReg = new UserRegDao(em).findByAccount(account);
		if (userReg != null) {
			if (new Date().getTime() - userReg.getCodeCreateTime().getTime() < _interverTime) {

				res.add("status", -12)
					.add("msg", "30秒后重试。");
				
				return false;
			}
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
