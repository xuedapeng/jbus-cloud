package cloud.jbus.logic.realtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.persistence.EntityManager;

import com.google.common.collect.ImmutableMap;

import cloud.jbus.common.helper.JsonBuilder;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.realtime.param.GetOnlineInfoOfDevicesLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.common.conf.ZSystemConfig;
import fw.jbiz.common.helper.httpclient.HttpHelper;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;


@Action(method="realtime.device.online.query")
public class GetOnlineInfoOfDevicesLogic extends BaseZLogic {

	final static String URL = ZSystemConfig.getProperty("jbus_rpc_url");
	final static String APPID = ZSystemConfig.getProperty("jbus_rpc_appId");
	final static String APPTOKEN = ZSystemConfig.getProperty("jbus_rpc_appToken");
	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		GetOnlineInfoOfDevicesLogicParam myParam = (GetOnlineInfoOfDevicesLogicParam)logicParam;
		
		Map<String, Object> map = getOnlineStatus(myParam.getDeviceIds());
		res.add("status", map.get("status"))
			.add("msg", map.get("msg"))
			.add("result", map.get("result"));
		
		return true;
	}

	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		GetOnlineInfoOfDevicesLogicParam myParam = (GetOnlineInfoOfDevicesLogicParam)logicParam;
		if(myParam.getDeviceIds() == null || myParam.getDeviceIds().isEmpty()) {
			res.add("status", -3)
				.add("msg", "参数错误");
			
			return false;
		}
		
		return true;
	}

	public static Map<String, Object> getOnlineStatus(List<String> deviceSnList) {

		String response = HttpHelper.doPost(
				URL, 
				JsonBuilder.build()
					.add("method", "getOnlineInfoOfDevices")
					.add("auth", ImmutableMap.of("appId", APPID, "appToken", APPTOKEN))
					.add("data", ImmutableMap.of("deviceIds", deviceSnList)
					).toString());
		
		Map<String, Object> map = JsonHelper.json2map(response);
		
		return map;
	}
	

}
