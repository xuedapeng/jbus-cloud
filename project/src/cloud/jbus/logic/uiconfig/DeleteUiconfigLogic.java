package cloud.jbus.logic.uiconfig;

import java.util.List;

import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.db.dao.UiconfigDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.uiconfig.param.DeleteUiconfigLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

/*
 * 删除组态配置
 */
@Action(method="uiconfig.project.delete")
public class DeleteUiconfigLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		DeleteUiconfigLogicParam myParam = (DeleteUiconfigLogicParam)logicParam;
		Integer projectId = Integer.valueOf(myParam.getProjectId());

		UiconfigDao dao = new UiconfigDao(em);
		
		UiconfigEntity uiconfig = dao.findById(projectId);
		uiconfig.setStatus(StatusConst.STATUS_DEL);
		dao.save(uiconfig);
		
		res.add("status", 1)
			.add("msg", "删除组态成功");
		
		return true;
	}

	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		// 组态所有者
		DeleteUiconfigLogicParam myParam = (DeleteUiconfigLogicParam)logicParam;
		Integer projectId = Integer.valueOf(myParam.getProjectId());
		
		
		UiconfigEntity uiconfig = new UiconfigDao(em).findById(projectId);
		if (uiconfig == null 
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

		DeleteUiconfigLogicParam myParam = (DeleteUiconfigLogicParam)logicParam;
		Integer projectId = Integer.valueOf(myParam.getProjectId());
		// 是否有子项目
		List<UiconfigEntity> list = new UiconfigDao(em).findList(this.getLoginUserId(myParam.getSecretId()), projectId);
		if (list != null && !list.isEmpty()) {

			res.add("status", -11)
				.add("msg", "projectId 有子项目，不能删除");
			
			return false;
			
		}
		
		return true;
	}
	
}
