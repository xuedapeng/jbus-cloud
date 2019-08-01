package cloud.jbus.logic.realtime;

import java.util.Arrays;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import cloud.jbus.common.constant.JbusConst;
import cloud.jbus.common.helper.HexHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.realtime.param.SendCmdLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.share.privilege.DevicePrivilege;
import cloud.jbus.proxy.MqttProxy;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="realtime.device.sendcmd")
public class SendCmdLogic extends BaseZLogic {

	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SendCmdLogicParam myParam = (SendCmdLogicParam)logicParam;
		
		String deviceSn = myParam.getDeviceSn();
		String cmd = myParam.getCmd();
		
		byte[] data = HexHelper.hexStringToBytes(cmd);
		MqttProxy.publish(JbusConst.TOPIC_PREFIX_CMD + deviceSn, data);
			
		res.add("status", 1)
			.add("msg","realtime.device.sendcmd ok");
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validate(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {

		SendCmdLogicParam myParam = (SendCmdLogicParam)logicParam;
		String deviceSn = myParam.getDeviceSn();
		
		String result = ValidateHelper.notEmptyCheck(
				"deviceSn",deviceSn,
				"cmd", myParam.getCmd());

		if (StringUtils.isNotEmpty(result)) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
				.add("msg", "参数缺失：" + result);
			
			return false;
		}
		
		// deviceSn 存在check
		DeviceEntity device = new DeviceDao(em).findByDeviceSn(deviceSn);
		if (device == null) {
			res.add("status", IResponseObject.RSP_CD_ERR_PARAM)
			.add("msg", "deviceSn不存在：" + deviceSn);
		
			return false;
		}

		// 权限check
		if (!DevicePrivilege.canControl(
				device.getId(), getLoginUserId(myParam.getSecretId()), em)) {

			res.add("status", -11)
				.add("msg", "权限错误：用户没有对该指定设备发送指令的权限");
			
			return false;
		}
			
		// device 在线check
		Map<String, Object> map = GetOnlineInfoOfDevicesLogic.getOnlineStatus(
				Arrays.asList(new String[]{deviceSn}));
		
		if (map == null) {
			res.add("status", -10)
				.add("msg", "jbus服务不可用");
			
			return false;
		} else {
			if (!"on".equals(((Map<String,String>)map.get("result")).get(deviceSn))) {

				res.add("status", -11)
					.add("msg", "device不在线：" + deviceSn);
				
				return false;
			}
		}
		
		
		return true;
	}

}
