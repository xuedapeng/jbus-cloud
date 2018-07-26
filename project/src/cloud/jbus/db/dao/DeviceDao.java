package cloud.jbus.db.dao;


import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.DeviceEntity;


public class DeviceDao extends BaseZDao {

	static Logger logger = Logger.getLogger(DeviceDao.class);
	
	public DeviceDao(EntityManager _em) {
		super(_em);
	}
	
	public DeviceEntity findById(Integer id) {
		return this.em.find(DeviceEntity.class, id);
	}
	
}
