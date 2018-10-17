package cloud.jbus.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.EmqUserEntity;
import cloud.jbus.db.bean.UserEntity;


public class EmqUserDao extends BaseZDao {

	static Logger logger = Logger.getLogger(EmqUserDao.class);
	
	public EmqUserDao(EntityManager _em) {
		super(_em);
	}
	

	public EmqUserEntity findById(Integer id) {
		return this.em.find(EmqUserEntity.class, id);
	}
}
