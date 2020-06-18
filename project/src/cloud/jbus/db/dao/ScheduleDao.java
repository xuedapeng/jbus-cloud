package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.ScheduleEntity;


public class ScheduleDao extends BaseZDao {

	static Logger logger = Logger.getLogger(ScheduleDao.class);
	
	public ScheduleDao(EntityManager _em) {
		super(_em);
	}
	
	public ScheduleEntity findById(Integer id) {
		return this.em.find(ScheduleEntity.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ScheduleEntity> findByDeviceId(Integer deviceId, Integer page, Integer  pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from ScheduleEntity");
		queryString.append(" where deviceId=:deviceId");
		queryString.append(" and status=1");
		queryString.append(" order by id desc ");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);
		
		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<ScheduleEntity> list = (List<ScheduleEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<ScheduleEntity>();

	}

	@SuppressWarnings("unchecked")
	public Long findTotal(Integer deviceId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from ScheduleEntity");
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
	public ScheduleEntity findByScheduleId(Integer deviceId, Integer scheduleId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from ScheduleEntity");
		queryString.append(" where deviceId=:deviceId");
		queryString.append(" and id=:scheduleId ");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceId", deviceId);
		query.setParameter("scheduleId", scheduleId);
		
		
		List<ScheduleEntity> list = (List<ScheduleEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}
}
