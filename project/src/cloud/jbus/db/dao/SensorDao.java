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
	public List<SensorEntity> findByDeviceId(Integer deviceId, Integer page, Integer  pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from SensorEntity");
		queryString.append(" where deviceId=:deviceId");
		queryString.append(" and status=1");
		queryString.append(" order by sensorNo asc ");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);
		
		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<SensorEntity> list = (List<SensorEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<SensorEntity>();

	}

	@SuppressWarnings("unchecked")
	public Long findTotal(Integer deviceId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from SensorEntity");
		queryString.append(" where deviceId=:deviceId");
		queryString.append(" and status=1");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);

		List<Long> list = query.getResultList();
		if(list != null && list.size()>0) {
			return list.get(0);
		}
		return Long.valueOf(0);

	}
	
	@SuppressWarnings("unchecked")
	public SensorEntity findBySensorNo(Integer deviceId, Integer sensorNo) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from SensorEntity");
		queryString.append(" where deviceId=:deviceId");
		queryString.append(" and sensorNo=:sensorNo ");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);
		query.setParameter("sensorNo", sensorNo);
		
		
		List<SensorEntity> list = (List<SensorEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}

	@SuppressWarnings("unchecked")
	public SensorEntity findBySensorId(Integer deviceId, Integer sensorId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from SensorEntity");
		queryString.append(" where id=:sensorId ");
		if (deviceId != null) {
			queryString.append(" and deviceId=:deviceId ");
		}
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("sensorId", sensorId);
		if (deviceId != null) {
			query.setParameter("deviceId", deviceId);
		}
		
		
		List<SensorEntity> list = (List<SensorEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}
	
	
}
