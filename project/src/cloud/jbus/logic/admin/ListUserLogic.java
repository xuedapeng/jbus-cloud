package cloud.jbus.logic.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.UserEntity;
import cloud.jbus.db.dao.UserDao;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.admin.param.ListUserLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="admin.user.list")	
public class ListUserLogic extends BaseZLogic {

	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ListUserLogicParam myParam = (ListUserLogicParam)logicParam;
		
		UserDao dao = new UserDao(em);
		List<UserEntity> userList = dao.findUserList(
				myParam.getKeyword(), 
				toInt(myParam.getPage()), 
				toInt(myParam.getPageSize()));
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		userList.forEach(E->{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", E.getId());
			map.put("account", E.getAccount());
			map.put("nickName", E.getNickName());
			map.put("status", E.getStatus());
			
			resultList.add(map);
			
		});
		
		res.add("status", 1)
			.add("msg", "用户查询成功！")
			.add("result", resultList);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		ListUserLogicParam myParam = (ListUserLogicParam)logicParam;
		
		if (myParam.getPage() == null) {
			myParam.setPage("1");
		}
		if (myParam.getPageSize() == null) {
			myParam.setPageSize("20");
		}

		String[][] matrix = new String[][]{
			{"page", "0", "0", "1"},
			{"pageSize", "0", "0", "1"}
		};
		
		String result = ValidateHelper.checkByMatrix(myParam, matrix);

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数错误：" + result);
			
			return false;
		}
		
		return true;
	}

}
