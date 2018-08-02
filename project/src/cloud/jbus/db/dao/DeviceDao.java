package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
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
	
	@SuppressWarnings("unchecked")
	public List<DeviceEntity> findByPage(Integer userId, Integer page, Integer  pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from DeviceEntity");
		queryString.append(" where ownerId=:userId");
		queryString.append(" and status=1");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);
		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<DeviceEntity> list = (List<DeviceEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<DeviceEntity>();

	}

	@SuppressWarnings("unchecked")
	public List<DeviceEntity> searchDevice(Integer userId, String filter, Integer page, Integer pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from DeviceEntity");
		queryString.append(" where ownerId=:userId");
		queryString.append(" and status=1");
		if (StringUtils.isNotEmpty(filter)) {
			queryString.append(" and (deviceSn like :filter or deviceName like :filter)");
		}
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter("filter", "%"+filter+"%");
		}
		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<DeviceEntity> list = (List<DeviceEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<DeviceEntity>();

	}
	
	
	@SuppressWarnings("unchecked")
	public Long findTotal(Integer userId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from DeviceEntity");
		queryString.append(" where ownerId=:userId");
		queryString.append(" and status=1");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);

		List<Long> list = query.getResultList();
		if(list != null && list.size()>0) {
			return list.get(0);
		}
		return Long.valueOf(0);

	}
	
}
