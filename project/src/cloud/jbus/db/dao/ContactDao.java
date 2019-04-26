package cloud.jbus.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.ContactEntity;
import cloud.jbus.db.bean.DatDecodeEntity;

public class ContactDao extends BaseZDao {

	static Logger logger = Logger.getLogger(ContactDao.class);
	
	public ContactDao(EntityManager _em) {
		super(_em);
	}
	
	public ContactEntity findById(Integer id) {
		return this.em.find(ContactEntity.class, id);
	}

	@SuppressWarnings("unchecked")
	public ContactEntity findByDeviceSn(String deviceSn) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from ContactEntity");
		queryString.append(" where deviceSn=:deviceSn");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceSn", deviceSn);
		
		List<ContactEntity> list = (List<ContactEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}
}
