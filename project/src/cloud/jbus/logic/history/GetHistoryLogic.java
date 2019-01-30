package cloud.jbus.logic.history;

import javax.persistence.EntityManager;

import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

/*
 * 查找历史数据
 */
@Action(method="history.data.query")
public class GetHistoryLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
