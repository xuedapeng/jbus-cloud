package cloud.jbus.logic.uiconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.UiconfigDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.uiconfig.param.GetUiconfigLogicParam;
import fw.jbiz.common.helper.JsonHelper;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;

/*
 * 获取组态配置
 */
@Action(method="uiconfig.projects.get")
public class GetUiconfigLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		GetUiconfigLogicParam myParam = (GetUiconfigLogicParam)logicParam;
		Integer projectId = StringUtils.isEmpty(myParam.getProjectId())?null:Integer.valueOf(myParam.getProjectId());

		List<UiconfigEntity> configList = new UiconfigDao(em).findList(
				this.getLoginUserId(myParam.getSecretId()), projectId);
		
		if (configList.isEmpty()) {

			res.add("status", -10)
				.add("msg", "没有找到组态配置");
			
			return false;
		}
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		int[] seq = {0};
		configList.forEach((E)->{
			seq[0]++;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("seq", String.valueOf(seq[0]));
			map.put("projectId", String.valueOf(E.getId()));
			map.put("title", E.getTitle());
			map.put("deviceSnList", getDeviceSnList(E.getDeviceSnList(), em));
			map.put("cover", getCover(E.getCover()));
			map.put("coverCmd", getCoverCmd(E.getCover()));
			
			resultList.add(map);
		});
		
		res.add("status", 1)
			.add("msg", "查找成功")
			.add("result", resultList);
		
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getCover(String cover) {
		List<Object> coverList = JsonHelper.json2list(cover);
		List<Object> coverDatList = new ArrayList<Object>();
		
		int[] seq = {0};
		coverList.forEach((E)->{
			Map<String, String> m = (Map<String, String>)E;
			if (!"cmd".equals(m.get("type"))) {
				seq[0]++;
				m.put("seq", String.valueOf(seq[0]));
				coverDatList.add(m);
			}
			
		});
		
		return coverDatList;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> getCoverCmd(String cover) {
		List<Object> coverList = JsonHelper.json2list(cover);
		List<Object> coverCmdList = new ArrayList<Object>();
		
		int[] seq = {0};
		coverList.forEach((E)->{
			Map<String, String> m = (Map<String, String>)E;
			if ("cmd".equals(m.get("type"))) {
				seq[0]++;
				m.put("seq", String.valueOf(seq[0]));
				coverCmdList.add(m);
			}
			
		});
		
		return coverCmdList;
	}
	
	private Map<String, Object> getDeviceSnList(String deviceSns, EntityManager em) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String[] sns = deviceSns.split(",");
		DeviceDao dao = new DeviceDao(em);
		for(int i=0; i<sns.length; i++) {
			String deviceSn = sns[i].trim();
			DeviceEntity device = dao.findByDeviceSn(deviceSn);
			if (device!=null) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("id", String.valueOf(device.getId()));
				m.put("name", device.getDeviceName());
				resultMap.put(deviceSn, m);
			}
		}
		
		return resultMap;
		
	}
	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		// 组态所有者
		GetUiconfigLogicParam myParam = (GetUiconfigLogicParam)logicParam;
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
