package cloud.jbus.logic;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.device.param.IPrivilegedParam;
import cloud.jbus.logic.share.annotation.Privilege;
import cloud.jbus.logic.share.privilege.DevicePrivilege;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.ext.memcache.ZCacheManager;
import fw.jbiz.ext.memcache.interfaces.ICache;
import fw.jbiz.logic.ZLogic;
import fw.jbiz.logic.ZLogicParam;

public abstract class BaseZLogic extends ZLogic {

	protected static ICache cacheUserSecretKey = ZCacheManager.getInstance("user.secretKey");
	protected static ICache cacheUserId = ZCacheManager.getInstance("user.id");

	
	public BaseZLogic() {
		// 增加访问统计过滤器
		//		addFilter(new ZStatsFilter());
	}
	
	protected String getPersistenceUnitName() {
		return ZSystemConfig.getProperty("persistence_unit_name");
	}
	

   /*
    * 身份认证
    */
	@Override
   protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){

		BaseZLogicParam baseParam = (BaseZLogicParam)logicParam;
		String secretId = baseParam.getSecretId();
		String secretKey = baseParam.getSecretKey();

		res.add("status", -2)
			.add("msg", "auth/priviege failed.");
		
		if (StringUtils.isEmpty(secretId) 
				|| StringUtils.isEmpty(secretKey)) {
			
			return false;
		}
		
		if (!cacheUserSecretKey.hit(secretId, secretKey)) {
			return false;
		}
		
		// cache没有，则查看数据库
		UserEntity user = new UserDao(em).findBySecretId(secretId);
		if (user != null) {
			cacheUserSecretKey.add(secretId, user.getSecretKey());
			cacheUserId.add(secretId, user.getId());
			if (!cacheUserSecretKey.hit(secretId, secretKey)) {
				return false;
			}
		}
		
		// 权限
		if (logicParam instanceof IPrivilegedParam) {
			if(!checkPrivilege((IPrivilegedParam)logicParam, em)) {
				return false;
			}
		}
		
		res.clear();
		return true;
   }
	
	protected boolean checkNotEmpty(String... vals) {
		for(String val: vals) {
			if (StringUtils.isEmpty(val)) {
				return false;
			}
		}
		
		return true;
	}
	
	protected Integer getLoginUserId(String secretId) {
		return (Integer)cacheUserId.get(secretId);
	}
	
	private boolean checkPrivilege(IPrivilegedParam priParam, EntityManager em) {
		
		Privilege priAnnotation = priParam.getClass().getAnnotation(Privilege.class);
		if (priAnnotation == null) {
			return false;
		}
		
		Integer userId =  getLoginUserId(priParam.getSecretId());
		Integer deviceId = Integer.valueOf(priParam.getDeviceId());

		if ("owner".equals(priAnnotation.value())) {
			return DevicePrivilege.isOwner(deviceId, userId, em);
		}
		
		if ("config".equals(priAnnotation.value())) {
			return DevicePrivilege.canConfig(deviceId, userId, em);
		}

		if ("control".equals(priAnnotation.value())) {
			return DevicePrivilege.canControl(deviceId, userId, em);
		}
		
		if ("view".equals(priAnnotation.value())) {
			return DevicePrivilege.canView(deviceId, userId, em);
		}
		
		return false;
	}
	
}
