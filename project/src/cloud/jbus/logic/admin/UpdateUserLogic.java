package cloud.jbus.logic.admin;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.admin.param.UpdateUserLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="admin.user.update")	
public class UpdateUserLogic extends BaseZLogic {

	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		UpdateUserLogicParam myParam = (UpdateUserLogicParam)logicParam;
		
		UserDao dao = new UserDao(em);
		
		UserEntity user = dao.findByUserId(toInt(myParam.getUserId()));
		if(user==null) {

			res.add("status", -11)
				.add("msg", "用户不存在！")
				.add("userId", myParam.getUserId());
			
			return false;
		}
		
		if(StringUtils.isNotEmpty(myParam.getAccount())) {

			user.setAccount(myParam.getAccount());
		}

		if(StringUtils.isNotEmpty(myParam.getNickName())) {

			user.setNickName(myParam.getNickName());
		}
		
		if("yes".equals(myParam.getResetPassword())) {

			user.setPassword(Md5SaltTool.getEncryptedPwd(StatusConst.PWD_DEFAULT));
		}
		
		if(StringUtils.isNotEmpty(myParam.getStatus())) {

			Integer status = toInt(myParam.getStatus());
			if(status.equals(StatusConst.STATUS_NORMAL) 
					|| status.equals(StatusConst.STATUS_STOP) 
					|| status.equals(StatusConst.STATUS_DEL)) {
				
				user.setStatus(status);
						
			}
		}
		
		dao.save(user);
		
		res.add("status", 1)
			.add("msg", "用户修改成功！")
			.add("userId", user.getId());
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		

		UpdateUserLogicParam myParam = (UpdateUserLogicParam)logicParam;
		myParam.setAccount(StringUtils.trim(myParam.getAccount()));
		myParam.setNickName(StringUtils.trim(myParam.getNickName()));
		
		String[][] matrix = new String[][]{
			{"userId", "1", "0", "1"},
			{"account", "0", "50", "0"},
			{"nickName", "0", "100", "0"},
			{"status", "0", "0", "1"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		
		// account 唯一性

		if(StringUtils.isNotEmpty(myParam.getAccount())) {
			UserDao dao = new UserDao(em);
			UserEntity user = dao.findByAccount(myParam.getAccount());
			if (user != null 
					&& !user.getId().equals(toInt(myParam.getUserId()))) {
				res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "account已存在："+ myParam.getAccount());
				return false;
			}
			
		}
		
		return true;
	}

}
