package cloud.jbus.logic.share.codec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


import cloud.jbus.common.helper.HexHelper;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.db.bean.DatDecodeEntity;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

public class DecodeValidator {

	static Logger log = Logger.getLogger(DecodeValidator.class);
	
	@SuppressWarnings("unchecked")
	private static String checkResultSchema(String resultSchema) {
		String title = "返回值模式错误";
		Map<String, Object> schema = JsonHelper.json2map(resultSchema);
		
		if (schema.size() == 0) {
			return String.format("%s: sno 必须项目缺失", title);
		}
		
		for(String key: schema.keySet()) {
			// 1. sensorNo:必须是数字
			if (!NumericHelper.isInteger(key)) {
				return String.format("%s: sensorNo:必须是数字(%s)", title, key);
			}

			Map<String, Object> item = (Map<String, Object>)schema.get(key);
			// 2. type=metric
			if (!"metric".equals(item.get("type"))) {
				return String.format("%s: type='metric'固定(%s)", title, key);
			}
			// 3. field
			Map<String, Object> field = (Map<String, Object>)item.get("field");
			if (field == null) {
				return String.format("%s: field必须项目缺失(%s)", title, key);
			}
			
			// 3.1 field > 0
			if (field.size() == 0) {
				return String.format("%s: field必须项目缺失(%s)", title, key);
			}
			
			// 3.2 display, format, unit
			for(String name: field.keySet()) {
				
				if (StringUtils.isEmpty(name)) {
					return String.format("%s: field 字段名称不能为空(%s)", title, key);
				}
				
				Map<String, Object> fd = (Map<String, Object>)field.get(name);
				String display = (String)fd.get("display");
				String format = (String)fd.get("format");
				String unit = (String)fd.get("unit");
				if (StringUtils.isEmpty(display)) {
					return String.format("%s: field.display 必须项目缺失(%s, %s)", title, key, name);
				}
				if (StringUtils.isEmpty(format)) {
					return String.format("%s: field.format 必须项目缺失(%s, %s)", title, key, name);
				}
				if (StringUtils.isEmpty(unit)) {
					return String.format("%s: field.unit 必须项目缺失(%s, %s)", title, key, name);
				}
			}
			
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private static String checkSampleCases(String sampleCases, String resultSchema) {

		String title = "测试用例错误";
		List<Object> cases = JsonHelper.json2list(sampleCases);
		Map<String, Object> schema = JsonHelper.json2map(resultSchema);
		
		if (cases.size() == 0) {
			return String.format("%s: 测试用例必须是非空数组", title);
		}
		
		for(int i=0; i<cases.size(); i++) {
			Map<String, Object> cs = (Map<String, Object>)cases.get(i);
			
			Object input = cs.get("input");
			Object output = cs.get("output");
			if (input == null || input.equals("")) {
				return String.format("%s: input必须项目缺失(index=%d)", title, i);
			}
			if (output == null) {
				return String.format("%s: output必须项目缺失(index=%d)", title, i);
			}
			
			String inputStr = ((String)input).toUpperCase().replaceAll(" ", "");
			byte[] inputBytes = HexHelper.hexStringToBytes(inputStr);
			String inputStr2 = HexHelper.bytesToHexStringNoBlank(inputBytes).toUpperCase();
			if (!inputStr.equals(inputStr2)) {
				return String.format("%s: input hexString 无效(index=%d)", title, i);
			}
			
			if(output.equals("null")) {
				continue;
			}
			
			Map<String, Object> outputMap = (Map<String, Object>)output;
			Object  sno = outputMap.get("sno");
			Object data = outputMap.get("data");
			if (sno == null) {
				return String.format("%s: output.sno 必须项目缺失(index=%d)", title, i);
			}
			if (data == null) {
				return String.format("%s: output.data 必须项目缺失(index=%d)", title, i);
			}
			
			String snoStr = String.valueOf(((Double)sno).intValue());
			if(!schema.containsKey(snoStr)) {
				return String.format("%s: output.sno:%s 在schema中未定义(index=%d)", title, snoStr, i);
			}
			
			Map<String, Object> dataMap =  (Map<String, Object>)data;
			if (dataMap.size() == 0){
				return String.format("%s: output.data 必须项目缺失(index=%d)", title, i);
			}
			for(String key: dataMap.keySet()) {
				Map<String, Object> schemaSno = (Map<String, Object>)schema.get(snoStr);
				if (!((Map<String, Object>)schemaSno.get("field")).containsKey(key)) {
					return String.format("%s: output.data field: %s 在schema中未定义(index=%d)", title, key, i);
				}
				
				if ((dataMap.get(key)) instanceof String) {
					return String.format("%s: output.data: %s 必须是数字类型的值(index=%d)", title, key, i);
				}
			}
			
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static String checkScript(String scriptText, String sampleCases) throws ScriptCPUAbuseException, ScriptException, NoSuchMethodException {

		String title = "运行结果错误";
		List<Object> cases = JsonHelper.json2list(sampleCases);
		NashornSandbox nsb = createJsEngin();
		for(int i=0; i<cases.size(); i++) {

			Map<String, Object> cs = (Map<String, Object>)cases.get(i);
			nsb.eval(scriptText);

			Invocable invocable = nsb.getSandboxedInvocable();
			String actual = (String)invocable.invokeFunction("decodeDat", 
					HexHelper.hexStringToBytes((String)cs.get("input")));

			Object expected = cs.get("output");
			if (actual == null) {
				if ("null".equals(expected)) {
					continue;
				} else {
					return String.format("%s: \n期待: %s \n实际：%s \n(caseIdx=%d)", 
							title, 
							JsonHelper.map2json((Map<String, Object>)expected), 
							"null", i);
				}
			}
			
			String expectedStr = JsonHelper.map2json((Map<String, Object>)expected);
			String actualStr = JsonHelper.map2json(JsonHelper.json2map(actual));
			
			if (!actualStr.equals(expectedStr)) {
				return String.format("%s: \n期待: %s \n实际：%s \n(caseIdx=%d)", 
						title, 
						expectedStr, 
						actualStr, 
						i);
			}
		}
		
		
		return null;
	}
	
	public static String checkDatDecode(DatDecodeEntity decode) {
		try {
			return doCheckDatDecode(decode);
		} catch (ScriptCPUAbuseException e) {
			log.error("", e);
			return String.format("脚本执行异常：CPU时间超限。\n%s", e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("", e);
			return String.format("脚本执行异常：decodeDat函数未定义。\n%s", e.getMessage());
		} catch (ScriptException e) {
			log.error("", e);
			if (e.getMessage().indexOf("ScriptCPUAbuseException")>=0) {
				return String.format("脚本执行异常：CPU时间超限。\n%s", e.getMessage());
			}
			return String.format("脚本执行异常：js代码错误。\n%s", e.getMessage());
		} catch (Exception e) {
			log.error("", e);
			return String.format("脚本执行异常：\n%s", e.getMessage());
		}
	}
	

	@SuppressWarnings("unused")
	private static String doCheckDatDecode(DatDecodeEntity decode) throws ScriptCPUAbuseException, NoSuchMethodException, ScriptException  {

		// 校验返回模式
		String result = checkResultSchema(decode.getResultSchema());

		// 校验测试用例
		if (StringUtils.isEmpty(result)) {
			result = checkSampleCases(decode.getSampleCases(), decode.getResultSchema());
		}

		// 校验js脚本
		if (StringUtils.isEmpty(result)) {
			List<Object> sampleCaseList = JsonHelper.json2list(decode.getSampleCases());
			result = checkScript(decode.getScriptText(), decode.getSampleCases());
		}
		
		return result;
	}

	private static NashornSandbox createJsEngin() {
		
		NashornSandbox _nashornSandbox = NashornSandboxes.create();
		
		_nashornSandbox.setMaxCPUTime(100);
		_nashornSandbox.setMaxMemory(10*1024*1024);
		_nashornSandbox.allowNoBraces(false);
		_nashornSandbox.setMaxPreparedStatements(30); // because preparing scripts for execution is expensive
		_nashornSandbox.setExecutor(Executors.newSingleThreadExecutor());
		
		return _nashornSandbox;
	}
}
