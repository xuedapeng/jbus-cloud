package cloud.jbus.db.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.UserEntity;


public class UserDao extends BaseZDao {

	static Logger logger = Logger.getLogger(UserDao.class);
	
	public UserDao(EntityManager _em) {
		super(_em);
	}

	public UserEntity findByUserId(Integer userId) {
		
		logger.info(String.format("UserDao.findByUserId(%s) start.", userId));
		
		String queryString = "from UserEntity where id =:userId ";
		

		Query query = getEntityManager().createQuery(queryString);

		query.setParameter("userId", userId);
		
		@SuppressWarnings("unchecked")
		List<UserEntity> list = query.getResultList();
		
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
			
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
		
		@SuppressWarnings("unchecked")
		List<UserEntity> list = query.getResultList();
		
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
			
	}
	
	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserList(String keyword, Integer page, Integer  pageSize) {

		String queryString = "from UserEntity  ";
		if(StringUtils.isNotEmpty(keyword)) {
			queryString += " where account like concat('%',:keyword ,'%')"
					+ " or nickName like concat('%',:keyword ,'%')";
		}
		queryString += " order by id desc";

		Query query = getEntityManager().createQuery(queryString);

		if(StringUtils.isNotEmpty(keyword)) {
			query.setParameter("keyword", keyword);
		}
		query.setFirstResult((page-1)*pageSize);
		query.setMaxResults(pageSize);

		List<UserEntity> list = (List<UserEntity>)query.getResultList();
		
		if(list != null && list.size() > 0){
			return list;
		}
		
		return new ArrayList<UserEntity>();
	}

	public Long findTotal() {
		return findTotal(null);
	}

	@SuppressWarnings("unchecked")
	public Long findTotal(String keyword) {

		String queryString = "select count(id) from UserEntity";

		if(StringUtils.isNotEmpty(keyword)) {
			queryString += " where account like concat('%',:keyword ,'%')"
					+ " or nickName like concat('%',:keyword ,'%')";
		}
		
		Query query = getEntityManager().createQuery(queryString.toString());

		if(StringUtils.isNotEmpty(keyword)) {
			query.setParameter("keyword", keyword);
		}
		
		List<Long> list = query.getResultList();
		if(list != null && list.size()>0) {
			return list.get(0);
		}
		return Long.valueOf(0);

	}
	
}
