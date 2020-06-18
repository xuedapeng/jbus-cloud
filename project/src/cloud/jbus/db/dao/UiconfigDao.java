package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.UiconfigEntity;


public class UiconfigDao extends BaseZDao {

	static Logger logger = Logger.getLogger(UiconfigDao.class);
	
	public UiconfigDao(EntityManager _em) {
		super(_em);
	}
	
	public UiconfigEntity findById(Integer id) {
		return this.em.find(UiconfigEntity.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<UiconfigEntity> findList(Integer userId, Integer projectId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from UiconfigEntity");
		queryString.append(" where status=1");
		queryString.append(" and ownerId=:userId");
		if (projectId != null) {
			queryString.append(" and pid=:projectId");
		} else {
			queryString.append(" and pid=0");
		}
		queryString.append(" order by sort asc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);
		if (projectId != null) {
			query.setParameter("projectId", projectId);
		}
		
		List<UiconfigEntity> list = (List<UiconfigEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<UiconfigEntity>();

	}

	
}
