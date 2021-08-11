package cloud.jbus.logic.device;

import java.util.List;

import javax.persistence.EntityManager;


import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.device.param.CategoryListLogicParam;

@Action(method="category.list")
public class CategoryListLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		CategoryListLogicParam myParam = (CategoryListLogicParam)logicParam;

		Integer userId = getLoginUserId(myParam.getSecretId());
		
		DeviceDao dao = new DeviceDao(em);
		List<String> catList = dao.findCategory(userId);
		
		res.add("status", 1)
			.add("msg", "category.list ok.")
			.add("total", catList.size())
			.add("result", catList);
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		return true;
	}
	
}
