package cloud.jbus.logic.admin;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.admin.param.AddUserLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="admin.user.add")	
public class AddUserLogic extends BaseZLogic {

	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		AddUserLogicParam myParam = (AddUserLogicParam)logicParam;
		
		UserDao dao = new UserDao(em);
		
		UserEntity user = new UserEntity();
		user.setAccount(myParam.getAccount());
		user.setNickName(myParam.getNickName());
		
		user.setPassword(Md5SaltTool.getEncryptedPwd(StatusConst.PWD_DEFAULT));
		user.setSecretId(GuidHelper.genUUID());
		user.setSecretKey(GuidHelper.genUUID());
		user.setStatus(StatusConst.STATUS_NORMAL);
		user.setSysAdmin(StatusConst.SYS_ADMIN_NO);
		user.setCreateTime(new Date());
		
		dao.save(user);
		
		res.add("status", 1)
			.add("msg", "用户添加成功！")
			.add("userId", user.getId());
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		

		AddUserLogicParam myParam = (AddUserLogicParam)logicParam;
		myParam.setAccount(StringUtils.trim(myParam.getAccount()));
		myParam.setNickName(StringUtils.trim(myParam.getNickName()));
		
		String[][] matrix = new String[][]{
			{"account", "1", "50", "0"},
			{"nickName", "1", "100", "0"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		
		// account 唯一性

		UserDao dao = new UserDao(em);
		UserEntity user = dao.findByAccount(myParam.getAccount());
		if (user != null) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
			.add("msg", "account已存在："+ myParam.getAccount());
			return false;
		}
		
		return true;
	}

}
