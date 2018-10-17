package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.SensorEntity;


public class SensorDao extends BaseZDao {

	static Logger logger = Logger.getLogger(SensorDao.class);
	
	public SensorDao(EntityManager _em) {
		super(_em);
	}
	
	public SensorEntity findById(Integer id) {
		return this.em.find(SensorEntity.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<SensorEntity> findByDeviceId(Integer deviceId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from SensorEntity");
		queryString.append(" where deviceId=:deviceId");
		queryString.append(" order by sensorNo asc ");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);
		
		List<SensorEntity> list = (List<SensorEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<SensorEntity>();

	}
	
	
	
}
