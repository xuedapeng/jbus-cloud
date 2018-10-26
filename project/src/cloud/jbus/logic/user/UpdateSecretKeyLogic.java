package cloud.jbus.logic.user;

import javax.persistence.EntityManager;

import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.common.utils.Md5SaltTool;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.user.param.UpdatePwdLogicParam;
import cloud.jbus.logic.user.param.UpdateSecretKeyLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.ext.memcache.ZCacheManager;
import fw.jbiz.logic.ZLogicParam;

@Action(method="user.secretKey.update")	
public class UpdateSecretKeyLogic extends BaseZLogic {
	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		UpdateSecretKeyLogicParam myParam = (UpdateSecretKeyLogicParam)logicParam;
		
		UserDao dao = new UserDao(em);
		UserEntity user = dao.findBySecretId(myParam.getSecretId());
		user.setSecretKey(GuidHelper.genUUID());
		dao.save(user);
		
		ZCacheManager.getInstance("user.secretKey").add(user.getSecretId(), user.getSecretKey());
		
		res.add("status", 1)
			.add("msg", "secretKey修改成功！")
			.add("secretKey", user.getSecretKey());
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		return true;
	}
	

}
