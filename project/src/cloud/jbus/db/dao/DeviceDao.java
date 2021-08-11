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
	

	public List<DeviceEntity> findByPage(Integer userId, Integer page, Integer  pageSize) {
		return findByPage(userId, page, pageSize, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<DeviceEntity> findByPage(Integer userId, Integer page, Integer  pageSize, String category) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from DeviceEntity");
		queryString.append(" where status=1");
		if (userId != null) {
			queryString.append(" and ownerId=:userId");
		}
		if(StringUtils.isNotEmpty(category)) {
			queryString.append(" and category like concat('%',:category,'%')");
		}
		queryString.append(" order by id desc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		if(StringUtils.isNotEmpty(category)) {
			query.setParameter("category", category);
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
	public List<DeviceEntity> searchDevice(Integer userId, String filter, Integer page, Integer pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from DeviceEntity");
		queryString.append(" where ownerId=:userId");
		queryString.append(" and status=1");
		if (StringUtils.isNotEmpty(filter)) {
			queryString.append(" and (deviceSn like :filter or deviceName like :filter)");
		}
		queryString.append(" order by deviceName asc");
		
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

	public Long findTotal(Integer userId) {
		return findTotal(userId, null);
	}
	
	@SuppressWarnings("unchecked")
	public Long findTotal(Integer userId, String category) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from DeviceEntity");
		queryString.append(" where ownerId=:userId");
		queryString.append(" and status=1");
		
		if(StringUtils.isNotEmpty(category)) {
			queryString.append(" and category like concat('%',:category,'%')");
		}
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);
		if(StringUtils.isNotEmpty(category)) {
			query.setParameter("category", category);
		}

		List<Long> list = query.getResultList();
		if(list != null && list.size()>0) {
			return list.get(0);
		}
		return Long.valueOf(0);

	}
	

	@SuppressWarnings("unchecked")
	public DeviceEntity findByDeviceSn(String deviceSn) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from DeviceEntity");
		queryString.append(" where deviceSn =:deviceSn");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceSn", deviceSn);
		
		List<DeviceEntity> list = (List<DeviceEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}
	

	@SuppressWarnings("unchecked")
	public List<String> findCategory(Integer userId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select category from DeviceEntity ");
		queryString.append(" where ownerId=:userId ");
		queryString.append(" and category is not null ");
		queryString.append(" group by category ");

		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);

		List<String> list = query.getResultList();

		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<String>();
		
		
	}
}
