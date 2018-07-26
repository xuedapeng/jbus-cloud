package cloud.jbus.db.dao;

import javax.persistence.EntityManager;

import fw.jbiz.db.ZDao;

public class BaseZDao extends ZDao {

	public BaseZDao(EntityManager _em) {
		super(_em);
	}
	
}
