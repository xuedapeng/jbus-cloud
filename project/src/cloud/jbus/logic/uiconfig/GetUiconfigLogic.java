package cloud.jbus.logic.uiconfig;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.db.dao.UiconfigDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.uiconfig.param.GetUiconfigLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

/*
 * 获取组态配置
 */
@Action(method="uiconfig.project.get")
public class GetUiconfigLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		GetUiconfigLogicParam myParam = (GetUiconfigLogicParam)logicParam;
		Integer projectId = Integer.valueOf(myParam.getProjectId());

		UiconfigDao dao = new UiconfigDao(em);
		
		UiconfigEntity uiconfig = dao.findById(projectId);
		
		res.add("status", 1)
			.add("msg", "获取组态成功")
			.add("title", uiconfig.getTitle())
			.add("sort", uiconfig.getSort())
			.add("cover", uiconfig.getCover());
		
		return true;
	}

	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		// 组态所有者
		GetUiconfigLogicParam myParam = (GetUiconfigLogicParam)logicParam;
		Integer projectId = Integer.valueOf(myParam.getProjectId());
		
		
		UiconfigEntity uiconfig = new UiconfigDao(em).findById(projectId);
		if (uiconfig == null 
				|| !uiconfig.getStatus().equals(StatusConst.STATUS_NORMAL)
				|| !uiconfig.getOwnerId().equals(this.getLoginUserId(myParam.getSecretId()))) {

			res.add("status", -11)
				.add("msg", "projectId 不存在／没有权限");
			
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		String[][] matrix = new String[][]{
			{"projectId", "1", "0", "1"}
			
		};

		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}
		
		return true;
	}
	
}
