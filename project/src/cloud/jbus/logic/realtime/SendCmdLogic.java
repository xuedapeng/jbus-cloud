package cloud.jbus.logic.realtime;

import java.util.Arrays;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cloud.jbus.common.constant.JbusConst;
import cloud.jbus.common.helper.HexHelper;
import cloud.jbus.common.helper.ValidateHelper;
import cloud.jbus.db.bean.CmdEncodeEntity;
import cloud.jbus.db.bean.DeviceEntity;
import cloud.jbus.db.dao.CmdEncodeDao;
import cloud.jbus.db.dao.DeviceDao;
import cloud.jbus.db.dao.SensorDao;
import cloud.jbus.logic.BaseZLogic;
import cloud.jbus.logic.realtime.param.SendCmdLogicParam;
import cloud.jbus.logic.share.CommonLogic;
import cloud.jbus.logic.share.annotation.Action;
import cloud.jbus.logic.share.privilege.DevicePrivilege;
import cloud.jbus.proxy.MqttProxy;
import fw.jbiz.ext.json.ZSimpleJsonObject;
import fw.jbiz.logic.ZDbProcessor;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Action(method="realtime.device.sendcmd")
public class SendCmdLogic extends BaseZLogic {

	/*
	 * cmd:
	 * 1) 单条，明码：11 22 33 44
	 * 2）单条，引用： 3:2 （sensorNo:cmdNo）
	 * 3）多条，明码：30;00 11,11 22,33 44  （ms;cmdstr1,cmdstr2,...)
	 * 4) 多条，引用：30;3:2,4:1 (ms;sensorNo1:cmdNo1, sensorNo2:cmdNo2)
	 */

	static Logger log = Logger.getLogger(SendCmdLogic.class);
	
	@Override
	protected boolean execute(ZLogicParam logicParam, ZSimpleJsonObject res, EntityManager em) throws Exception {
		
		SendCmdLogicParam myParam = (SendCmdLogicParam)logicParam;
		
		String deviceSn = myParam.getDeviceSn();
		String cmd = myParam.getCmd().replaceAll(" ", "").replaceAll("	", ""); // 消除空白和tab
		
		Integer deviceId = CommonLogic.getDeviceIdBySn(deviceSn);
		
		if (cmd.indexOf(";")>0) { 
			// 多条指令: "30;00 11,11 22,33 44"
			String[] c = cmd.split(";");
			String[] cmds = c[1].split(",");
			sendMutiCmd(deviceSn, deviceId, Integer.valueOf(c[0]) ,cmds);
		} else {

			// 单条指令
			cmd = getDirectiveCmd(deviceId, cmd, em); // 引用转换
			String topic = JbusConst.TOPIC_PREFIX_CMD;
			if(cmd.startsWith("FW-")) {
				int firstBlank = cmd.indexOf(" ");
				topic = JbusConst.TOPIC_PREFIX_FWCMD + cmd.substring(3, firstBlank) + "-";
				cmd = cmd.substring(firstBlank+1);
			}
			
			byte[] data = HexHelper.hexStringToBytes(cmd);
			MqttProxy.publish(topic + deviceSn, data);
		}
			
		res.add("status", 1)
			.add("msg","realtime.device.sendcmd ok");
		
		return true;
	}

	private void sendMutiCmd(String deviceSn, Integer deviceId, Integer interval, String[] cmds) {
		
		
		new Thread(){

			@Override
			public void run() {

				new ZDbProcessor() {

					@Override
					public void execute(EntityManager em) {

						for(String cmd: cmds) {
							
							cmd = getDirectiveCmd(deviceId, cmd, em); // 引用转换
							String topic = JbusConst.TOPIC_PREFIX_CMD;
							if(cmd.startsWith("FW-")) {
								int firstBlank = cmd.indexOf(" ");
								topic = JbusConst.TOPIC_PREFIX_FWCMD + cmd.substring(3, firstBlank) + "-";
								cmd = cmd.substring(firstBlank+1);
							}
							
							byte[] data = HexHelper.hexStringToBytes(cmd);
							MqttProxy.publish(topic + deviceSn, data);
							try {
								Thread.sleep(interval);
							} catch (InterruptedException e) {
								log.error(trace(e));
							}
						}
					}
					
				}.run();
				
			}
			
		}.start();
		
		
	}

	private String getDirectiveCmd(Integer deviceId, String cmd, EntityManager em) {

		if(cmd.contains(":")) {
			// 引用
			String[] cmds = cmd.split(":");
			Integer sensorNo = Integer.valueOf(cmds[0]);
			Integer cmdNo = Integer.valueOf(cmds[1]);
			Integer sensorId = CommonLogic.getSensorIdByNo(deviceId, sensorNo, em);
			cmd = getCmdBySensorIdCmdno(sensorId, cmdNo, em);
		} else {

			// 明码
			// 不转换
		}
		
		return cmd;
	}
	
	private String getCmdBySensorIdCmdno(Integer sensorId, Integer cmdNo, EntityManager em) {

		CmdEncodeDao dao = new CmdEncodeDao(em);
		CmdEncodeEntity cmd = dao.findByCmdNo(sensorId, cmdNo);
		
		String script =  cmd.getScriptText();
		
		if(script.contains("function")) {
			script = script.split("'")[1];
		}
		
		return script;
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
