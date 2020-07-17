package cloud.jbus.logic.uiconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.db.dao.UiconfigDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.history.GetHydrographLogic;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.uiconfig.param.GetLastDataLogicParam;
import cloud.jbus.proxy.DbProxy;
import fw.jbiz.common.helper.JsonHelper;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

/*
 * 获取组态数据
 */
@Action(method="uiconfig.data.get")
public class GetLastDataLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		GetLastDataLogicParam myParam = (GetLastDataLogicParam)logicParam;
		Integer projectId = StringUtils.isEmpty(myParam.getProjectId())?null:Integer.valueOf(myParam.getProjectId());

		List<UiconfigEntity> configList = new UiconfigDao(em).findList(
				this.getLoginUserId(myParam.getSecretId()), projectId);
		
		if (configList.isEmpty()) {

			res.add("status", -10)
				.add("msg", "没有找到组态配置");
			
			return false;
		}
		
		// {deviceSn:{sensorNo:{fd1:xx,fd2:y ..},...},...}
		Map<String, Map<String, Map<String, Object>>> resultMap = 
				new HashMap<String, Map<String, Map<String, Object>>>();
		
		// {deviceSn:{sensorNo:fieldStyle},...}
		Map<String, Map<String, Map<String, Map<String, String>>>> fieldStyleMap = 
				new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
		
		// 数据
		// {deviceSn:{sensorNo:[fd1,fd2,...]}
		Map<String, Map<String, List<String>>> fields = getFields(configList);
		fields.forEach((sn, snoMap)->{
			
			Integer[] deviceId = {CommonLogic.getDeviceIdBySn(sn)};
			if(!fieldStyleMap.containsKey(sn)) {
				fieldStyleMap.put(sn, new HashMap<String, Map<String, Map<String, String>>>());
			}

			if(!resultMap.containsKey(sn)) {
				resultMap.put(sn, new HashMap<String, Map<String, Object>>());
			}
			
			snoMap.forEach((sno, fdList)->{

				Map<String, Map<String, String>> fieldStyle = 
						GetHydrographLogic.getFieldStyle(deviceId[0], sno, res, em);
				
				fieldStyleMap.get(sn).put(sno, fieldStyle);
				

				List<Map<String, Object>> result = DbProxy.findForTimeline(sn, Integer.valueOf(sno), null, -1, 1, fieldStyle);
				
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("time", result.isEmpty()?null:result.get(0).get("time"));
				
				
				fdList.forEach((K)->{
					dataMap.put(K, result.isEmpty()?null:result.get(0).get(K));
				});
				
				resultMap.get(sn).put(sno, dataMap);
				
			});
		});
		
		
		res.add("status", 1)
			.add("msg", "查找成功")
			.add("resultMap", resultMap)
			.add("styleMap", fieldStyleMap);
		
		return true;
	}
	
	private Map<String, Map<String, List<String>>> getFields(List<UiconfigEntity> configList) {

		Map<String, Map<String, List<String>>> coverDatMap = new HashMap<String, Map<String, List<String>>>();
		
		configList.forEach((E)->{
			makeCoverDat(E.getCover(), coverDatMap);
			
		});
		
		
		
		return coverDatMap;
	}
	

	/*
	 * {deviceSn1:{sensoNo1:[fd1,fd2...], sensoNo2:[fd1,fd2...]}, deviceSn2:...}
	 */
	@SuppressWarnings("unchecked")
	private void makeCoverDat(String cover, Map<String, Map<String, List<String>>> coverDatMap) {
		
		
		List<Object> coverList = JsonHelper.json2list(cover);
		
		coverList.forEach((E)->{
			Map<String, String> m = (Map<String, String>)E;
			if (!"cmd".equals(m.get("type"))) {

				String sn = m.get("deviceSn");
				String sno = m.get("sensorNo");
				String fd = m.get("field");
				if (!coverDatMap.containsKey(sn)) {
					coverDatMap.put(sn, new HashMap<String, List<String>>());
				}
				Map<String, List<String>> snoMap = coverDatMap.get(sn);
				
				if (!snoMap.containsKey(sno)) {
					snoMap.put(sno, new ArrayList<String>());
				}
				
				List<String> fdList = snoMap.get(sno);
				
				if (!fdList.contains(fd)) {
					fdList.add(fd);
				}
				
			}
		});
		
	}
	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		// 组态所有者
		GetLastDataLogicParam myParam = (GetLastDataLogicParam)logicParam;
		String projectId = myParam.getProjectId();
		
		if (StringUtils.isEmpty(projectId) || projectId.equals("0")) {
			return true;
		}
		
		UiconfigEntity uiconfig = new UiconfigDao(em).findById(Integer.valueOf(projectId));
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
			{"projectId", "0", "0", "1"}
		};

		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}

		
		return true;
	}

}
