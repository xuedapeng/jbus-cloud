package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import cloud.jbus.db.bean.EventEntity;
import cloud.jbus.db.bean.VDeviceOnlineEntity;


public class VDeviceOnlineDao extends BaseZDao {

	static Logger logger = Logger.getLogger(VDeviceOnlineDao.class);
	
	public VDeviceOnlineDao(EntityManager _em) {
		super(_em);
	}
	
	public VDeviceOnlineEntity findById(Integer id) {
		return this.em.find(VDeviceOnlineEntity.class, id);
	}
	

	@SuppressWarnings("unchecked")
	public List<VDeviceOnlineEntity> searchEvent(Integer userId, List<String> deviceSnList, Integer page, Integer pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from VDeviceOnlineEntity");
		queryString.append(" where deviceSn in :deviceSnList");

		queryString.append(" order by time desc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("deviceSnList", deviceSnList);

		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);
		
		List<VDeviceOnlineEntity> list = (List<VDeviceOnlineEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<VDeviceOnlineEntity>();

	}
	

}
