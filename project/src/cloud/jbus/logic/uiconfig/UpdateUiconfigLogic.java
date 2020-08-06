package cloud.jbus.logic.uiconfig;


import javax.persistence.EntityManager;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.db.dao.UiconfigDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.uiconfig.param.UpdateUiconfigLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

/*
 * 更新组态配置
 */
@Action(method="uiconfig.project.update")
public class UpdateUiconfigLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		UpdateUiconfigLogicParam myParam = (UpdateUiconfigLogicParam)logicParam;
		Integer projectId = Integer.valueOf(myParam.getProjectId());
		String title = myParam.getTitle();
		String cover = myParam.getCover().trim();
		Integer sort = Integer.valueOf(myParam.getSort());

		UiconfigDao dao = new UiconfigDao(em);
		
		UiconfigEntity uiconfig = dao.findById(projectId);
		
		uiconfig.setTitle(title);
		uiconfig.setCover(cover);
		uiconfig.setSort(sort);
		
		String snList = AddUiconfigLogic.getSnListFromCover(cover);
		if (!AddUiconfigLogic.checkDeviceOwner(this.getLoginUserId(myParam.getSecretId()), res, em, snList)) {
			return false;
		}
		uiconfig.setDeviceSnList(snList);
		
		dao.save(uiconfig);
		
		res.add("status", 1)
			.add("msg", "更新组态成功");
		
		return true;
	}

	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		// 组态所有者
		UpdateUiconfigLogicParam myParam = (UpdateUiconfigLogicParam)logicParam;
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
			{"projectId", "1", "0", "1"},
			{"title", "1", "100", "0"},
			{"cover", "1", "100000", "0"},
			{"sort", "0", "0", "1"}
			
		};

		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}

		UpdateUiconfigLogicParam myParam = (UpdateUiconfigLogicParam)logicParam;
		if (!AddUiconfigLogic.checkCover(myParam.getCover(), res)) {
			return false;
		}
		
		return true;
	}
	
}
