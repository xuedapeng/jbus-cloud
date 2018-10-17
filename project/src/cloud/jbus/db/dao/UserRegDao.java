package cloud.jbus.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.bean.UserRegEntity;


public class UserRegDao extends BaseZDao {

	static Logger logger = Logger.getLogger(UserRegDao.class);
	
	public UserRegDao(EntityManager _em) {
		super(_em);
	}
	
	
	public UserRegEntity findByAccount(String account) {
		
		logger.info(String.format("UserDao.findLastByAccount(%s) start.", account));
		
		String queryString = "from UserRegEntity where account =:account";
		

		Query query = getEntityManager().createQuery(queryString);

		query.setParameter("account", account);
		
		List<UserRegEntity> list = query.getResultList();
		
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
			
	}

}
