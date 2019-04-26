package cloud.jbus.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.DatDecodeEntity;

public class DatDecodeDao extends BaseZDao {

	static Logger logger = Logger.getLogger(DatDecodeDao.class);
	
	public DatDecodeDao(EntityManager _em) {
		super(_em);
	}
	
	public DatDecodeEntity findById(Integer id) {
		return this.em.find(DatDecodeEntity.class, id);
	}

	@SuppressWarnings("unchecked")
	public DatDecodeEntity findByDeviceId(Integer deviceId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from DatDecodeEntity");
		queryString.append(" where deviceId=:deviceId");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);
		
		List<DatDecodeEntity> list = (List<DatDecodeEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}
}
