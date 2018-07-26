package cloud.jbus.logic.share.privilege;

import javax.persistence.EntityManager;

import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.DeviceDao;

public class DevicePrivilege {

	public static boolean canConfig(Integer deviceId, Integer userId, EntityManager em) {
		
		if (isOwner(deviceId, userId, em)) {
			return true;
		}
		
		return false;
		
	}

	public static boolean canControl(Integer deviceId, Integer userId, EntityManager em) {
		
		if (isOwner(deviceId, userId, em)) {
			return true;
		}
		
		return false;
		
	}
	
	public static boolean canView(Integer deviceId, Integer userId, EntityManager em) {
		
		if (isOwner(deviceId, userId, em)) {
			return true;
		}
		
		return false;
		
	}
	
	private static boolean isOwner(Integer deviceId, Integer userId, EntityManager em) {

		DeviceDao dao = new DeviceDao(em);
		DeviceEntity device = dao.findById(deviceId);
		if (device==null 
				|| !device.getOwnerId().equals(userId)) {
			
			return false;
		}
		
		return true;
	}
	
	
}
