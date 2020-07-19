package cloud.jbus.db.dao;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import cloud.jbus.db.bean.EventEntity;
import cloud.jbus.db.bean.LastDataEntity;


public class LastDataDao extends BaseZDao {

	static Logger logger = Logger.getLogger(LastDataDao.class);
	
	public LastDataDao(EntityManager _em) {
		super(_em);
	}
	

	@SuppressWarnings("unchecked")
	public List<LastDataEntity> findByDsKey(List<String> dsKeyList) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from LastDataEntity");
		queryString.append(" where dsKey in :dsKeyList");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("dsKeyList", dsKeyList);
		
		List<LastDataEntity> list = (List<LastDataEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<LastDataEntity>();

	}
	


}
