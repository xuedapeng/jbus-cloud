package cloud.jbus.db.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.DatDecodeEntity;


public class CmdEncodeDao extends BaseZDao {

	static Logger logger = Logger.getLogger(CmdEncodeDao.class);
	
	public CmdEncodeDao(EntityManager _em) {
		super(_em);
	}
	
	public CmdEncodeEntity findById(Integer id) {
		return this.em.find(CmdEncodeEntity.class, id);
	}


	public List<CmdEncodeEntity> findBySensorId(Integer sensorId) {
		return findBySensorId(sensorId, null, null);
		
	}
	@SuppressWarnings("unchecked")
	public List<CmdEncodeEntity> findBySensorId(Integer sensorId, Integer page, Integer  pageSize) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from CmdEncodeEntity");
		queryString.append(" where sensorId=:sensorId");
		queryString.append(" order by cmdNo asc");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("sensorId", sensorId);

		if (page != null && pageSize != null) {
			query.setFirstResult((page-1)*pageSize);
			query.setMaxResults(pageSize);
		}
		
		List<CmdEncodeEntity> list = (List<CmdEncodeEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<CmdEncodeEntity>();

	}

	@SuppressWarnings("unchecked")
	public CmdEncodeEntity findByCmdNo(Integer sensorId, Integer cmdNo) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from CmdEncodeEntity");
		queryString.append(" where sensorId=:sensorId");
		queryString.append(" and cmdNo=:cmdNo");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("sensorId", sensorId);
		query.setParameter("cmdNo", cmdNo);
		
		List<CmdEncodeEntity> list = (List<CmdEncodeEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}

	@SuppressWarnings("unchecked")
	public CmdEncodeEntity findByCmdId(Integer sensorId, Integer cmdId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("from CmdEncodeEntity");
		queryString.append(" where sensorId=:sensorId");
		queryString.append(" and id=:cmdId");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("sensorId", sensorId);
		query.setParameter("cmdId", cmdId);
		
		List<CmdEncodeEntity> list = (List<CmdEncodeEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		
		return null;

	}

	@SuppressWarnings("unchecked")
	public Long findTotal(Integer sensorId) {

		StringBuffer queryString = new StringBuffer();
		queryString.append("select count(id) from CmdEncodeEntity");
		queryString.append(" where sensorId=:sensorId");
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("sensorId", sensorId);

		List<Long> list = query.getResultList();
		if(list != null && list.size()>0) {
			return list.get(0);
		}
		return Long.valueOf(0);

	}
	
}
