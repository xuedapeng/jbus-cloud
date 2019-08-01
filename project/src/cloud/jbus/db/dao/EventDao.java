package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import cloud.jbus.db.bean.EventEntity;


public class EventDao extends BaseZDao {

	static Logger logger = Logger.getLogger(EventDao.class);
	
	public EventDao(EntityManager _em) {
		super(_em);
	}
	
	public EventEntity findById(Integer id) {
		return this.em.find(EventEntity.class, id);
	}
	

	@SuppressWarnings("unchecked")
	public List<EventEntity> searchEvent(Integer userId, List<String> deviceSnList, Integer page, Integer pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from EventEntity");
		queryString.append(" where deviceSn in :deviceSnList");

		queryString.append(" order by time desc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceSnList", deviceSnList);

		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<EventEntity> list = (List<EventEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<EventEntity>();

	}
	

	@SuppressWarnings("unchecked")
	public List<EventEntity> searchEventLast(Integer userId, List<String> deviceSnList, Integer page, Integer pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from EventEntity");
		queryString.append(" where deviceSn in :deviceSnList and islast=1 ");

		queryString.append(" order by time desc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceSnList", deviceSnList);

		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<EventEntity> list = (List<EventEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<EventEntity>();

	}
	

}
