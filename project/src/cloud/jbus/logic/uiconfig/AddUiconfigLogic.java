package cloud.jbus.logic.uiconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.UiconfigDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.uiconfig.param.AddUiconfigLogicParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

/*
 * 增加组态配置
 */
@Action(method="uiconfig.project.add")
public class AddUiconfigLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		AddUiconfigLogicParam myParam = (AddUiconfigLogicParam)logicParam;
		Integer pid = Integer.valueOf(myParam.getPid());
		String title = myParam.getTitle();
		String cover = myParam.getCover().trim();
		Integer sort = Integer.valueOf(myParam.getSort());
		
		UiconfigEntity uiconfig = new UiconfigEntity();
		uiconfig.setPid(pid);
		uiconfig.setTitle(title);
		uiconfig.setCover(cover);
		uiconfig.setSort(sort);
		String snList = getSnListFromCover(cover);
		if (!checkDeviceOwner(this.getLoginUserId(myParam.getSecretId()), res, em, snList)) {
			return false;
		}
		uiconfig.setDeviceSnList(snList);
		
		uiconfig.setOwnerId(this.getLoginUserId(myParam.getSecretId()));
		uiconfig.setStatus(StatusConst.STATUS_NORMAL);

		UiconfigDao dao = new UiconfigDao(em);
		dao.save(uiconfig);
		
		res.add("status", 1)
			.add("msg", "添加组态成功")
			.add("projectId", uiconfig.getId());
		
		return true;
	}

	
	@Override
	protected boolean auth(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em){
		if (!super.auth(logicParam, res, em)) {
			return false;
		}
		
		// 组态所有者
		AddUiconfigLogicParam myParam = (AddUiconfigLogicParam)logicParam;
		String pid = myParam.getPid();
		
		if (StringUtils.isEmpty(pid) || pid.equals("0")) {
			return true;
		}
		
		UiconfigEntity uiconfig = new UiconfigDao(em).findById(Integer.valueOf(pid));
		if (uiconfig == null 
				|| !uiconfig.getOwnerId().equals(this.getLoginUserId(myParam.getSecretId()))) {

			res.add("status", -11)
				.add("msg", "pid 不存在／没有权限");
			
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		String[][] matrix = new String[][]{
			{"pid", "1", "0", "1"},
			{"title", "1", "100", "0"},
			{"cover", "1", "100000", "0"},
			{"sort", "0", "0", "1"}
			
		};

		if (!checkParam(logicParam, res, matrix)) {
			return false;
		}

		AddUiconfigLogicParam myParam = (AddUiconfigLogicParam)logicParam;
		if (!checkCover(myParam.getCover(), res)) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean checkCover(String cover, ZSimpleJsonObject res) {
		
		cover = cover.replaceAll("^[\n|\t| ]+\\[", "[").replaceAll("\\][\n|\t| ]+$", "]");
		
		if (!(cover.startsWith("[") && cover.endsWith("]"))) {

			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数cover错误：必须是一个数组");
			
			return false;
		}
		
		List<Object> itemList = JsonHelper.json2list(cover);
		for(int i=0;i<itemList.size();i++){
			Map<String, Object> item = (Map<String, Object>)itemList.get(i);
			String type = (String)item.get("type");
			if (type==null || type.equals("dat")) {
				if (!checkContainsKey(item, res, i, 
						"deviceSn", "sensorNo", "field", "name")) {
					
					return false;
				}
				
			} else if (type.equals("cmd")) {

				if (!checkContainsKey(item, res, i, 
						"deviceSn", "cmd","name")) {
					
					return false;
				}
				
			} else if (type.equals("valuePtn")) {

				if (!checkContainsKey(item, res, i, 
						"pattern")) {
					
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	public static boolean checkContainsKey(Map<String, Object> map, ZSimpleJsonObject res, int idx, String...keys) {
		
		for(String key: keys) {
			if (!map.containsKey(key)) {

				res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
					.add("msg", String.format("参数cover错误：第%d项缺少 %s", idx, key));
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static String getSnListFromCover(String cover) {

		Map<String, String> snMap = new HashMap<String, String>();
		List<Object> itemList = JsonHelper.json2list(cover);
		
		itemList.forEach(E->{
			Map<String, Object> item = (Map<String, Object>)E;
			String deviceSn = (String)item.get("deviceSn");
			if(StringUtils.isNotEmpty(deviceSn)) {

				if (!snMap.containsKey(deviceSn)) {
					snMap.put(deviceSn, "");
				}
			}
		});
		
		StringBuffer result = new StringBuffer();
		for(String key: snMap.keySet()) {
			if (result.length() > 0) {
				result.append(",").append(key);
			} else {
				result.append(key);
			}
		}
		
		return result.toString();
	}
	
	public static boolean checkDeviceOwner(Integer userId, ZSimpleJsonObject res, EntityManager em, String snList) {
		
		if(StringUtils.isEmpty(snList)) {
			return true;
		}
		
		DeviceDao dao = new DeviceDao(em);
		
		for(String sn: snList.split(",")) {
			DeviceEntity device = dao.findByDeviceSn(sn);
			if (device == null || !userId.equals(device.getOwnerId())) {
				res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", String.format("参数cover错误：deviceSn(%s) 不存在/没有权限", sn));
				
				return false;
			}
		}
		
		return true;
	}
}
