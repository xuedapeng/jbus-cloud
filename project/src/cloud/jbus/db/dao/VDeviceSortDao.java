package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cloud.jbus.db.bean.VDeviceSortEntity;

public class VDeviceSortDao extends BaseZDao {

	static Logger logger = Logger.getLogger(VDeviceSortDao.class);
	
	public VDeviceSortDao(EntityManager _em) {
		super(_em);
	}

	@SuppressWarnings("unchecked")
	public List<VDeviceSortEntity> searchDevice(Integer userId, String filter, Integer page, Integer pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from VDeviceSortEntity");
		queryString.append(" where ownerId=:userId");
		queryString.append(" and status=1");
		if (StringUtils.isNotEmpty(filter)) {
			queryString.append(" and (deviceSn like :filter or deviceName like :filter)");
		}
		queryString.append(" order by sort desc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("userId", userId);
		
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter("filter", "%"+filter+"%");
		}
		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<VDeviceSortEntity> list = (List<VDeviceSortEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<VDeviceSortEntity>();

	}
	
}
