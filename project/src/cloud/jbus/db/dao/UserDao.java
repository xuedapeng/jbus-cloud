package cloud.jbus.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import cloud.jbus.db.bean.UserEntity;


public class UserDao extends BaseZDao {

	static Logger logger = Logger.getLogger(UserDao.class);
	
	public UserDao(EntityManager _em) {
		super(_em);
	}
	
	
	public UserEntity findByAccount(String account) {
		
		logger.info(String.format("UserDao.findByAccount(%s) start.", account));
		
		String queryString = "from UserEntity where account =:paccount";
		

		Query query = getEntityManager().createQuery(queryString);

		query.setParameter("paccount", account);
		
		List<UserEntity> list = query.getResultList();
		
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
			
	}

	public UserEntity findBySecretId(String secretId) {
		
		logger.info(String.format("UserDao.findBySecretId(%s) start.", secretId));
		
		String queryString = "from UserEntity where secretId =:secretId and status=1";
		

		Query query = getEntityManager().createQuery(queryString);

		query.setParameter("secretId", secretId);
		
		List<UserEntity> list = query.getResultList();
		
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
			
	}
	
	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserList() {

		String queryString = "from UserEntity";
		

		Query query = getEntityManager().createQuery(queryString);

		return (List<UserEntity>)query.getResultList();
		
		
	}
}
