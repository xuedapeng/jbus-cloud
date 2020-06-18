package cloud.jbus.service.uiengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cloud.jbus.common.exception.JbusException;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.db.bean.UiconfigEntity;
import fw.jbiz.common.conf.ZSystemConfig;

public class UIMaker {

	static Logger log = Logger.getLogger(UIMaker.class);
	
	static String TMP_TITLE = "##title##";
	static String TMP_ELEMENTS = "##elements##";
	static String TMP_VUE_DATA = "##vue_data##";
	static String TMP_VUE_METHODS = "##vue_methods##";
	static String TMP_JS_FUNCTION = "##js_function##";
	
	public String _htmlTemplate;
	UiconfigEntity _uiconfig;
	public String _htmlResult;
	
	String _conf_root = ZSystemConfig.getProperty("uiengine_root");
	String _conf_template = ZSystemConfig.getProperty("uiengine_template");
	
	public UIMaker() {
		loadTemplate();
	}

	public UIMaker(String root, String template) {
		_conf_root = root;
		_conf_template = template;
		
		loadTemplate();
	}
	
	public void make(UiconfigEntity uiconfig) {
		this._uiconfig = uiconfig;
		List<Object> elementList = JsonHelper.json2list(_uiconfig.getElements());
		
		makeTitle();
		makeElements(elementList);
		makeVuedata(elementList);
		
	}
	
	@SuppressWarnings("unchecked")
	private void makeElements(List<Object> elementList) {
		
		StringBuffer divStr = new StringBuffer();
		elementList.forEach((E)->{
			Map<String, Object> e = (Map<String, Object>)E;
			divStr.append(genDivFromElement(e));
			divStr.append("\n");
		});
		
		_htmlResult = _htmlResult.replaceAll(TMP_ELEMENTS, divStr.toString());
	}
	
	@SuppressWarnings("unchecked")
	private void makeVuedata(List<Object> elementList) {
		
		StringBuffer vuedataStr = new StringBuffer();
		elementList.forEach((E)->{
			Map<String, Object> e = (Map<String, Object>)E;
			vuedataStr.append(genVuedataFromElement(e));
			vuedataStr.append("\n");
		});
		
		_htmlResult = _htmlResult.replaceAll(TMP_VUE_DATA, vuedataStr.toString());
	}
	
	@SuppressWarnings("unchecked")
	private String genDivFromElement(Map<String, Object> element) {
		
		StringBuilder sb = new StringBuilder();
		
		Map<String, String> posMap = (Map<String, String>) ((Map<String, Object>)element.get("style")).get("position");
		Map<String, String> displayMap = (Map<String, String>)element.get("display");
		Map<String, String> bindMap = (Map<String, String>)element.get("bind");
		String type = (String)element.get("type");
		
		
		sb.append("<div class=\"element\" style=\"position:absolute;left:"+ posMap.get("left") + "px;top:"+ posMap.get("top") + "px\">");
		
		if (type.equals("dat")) {
			sb.append("<span class=\"dat\">" + displayMap.get("text") + "</span>");
			sb.append("<label>: {{" + 
					concatByUnderline(bindMap.get("deviceSn"),bindMap.get("sensorNo"),bindMap.get("field")) + "}}</label>");
		} else if(type.equals("cmd")) {
			sb.append("<span class=\"cmd\">" + displayMap.get("text") + "</span>");
		}
		sb.append("</div>");
		
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String genVuedataFromElement(Map<String, Object> element) {

		StringBuilder sb = new StringBuilder();
		Map<String, String> bindMap = (Map<String, String>)element.get("bind");
		String type = (String)element.get("type");
		if (type.equals("dat")) {
			sb.append(concatByUnderline(bindMap.get("deviceSn"),bindMap.get("sensorNo"),bindMap.get("field")));
			sb.append(":");
			sb.append("\"123\"");
		}
		
		return sb.toString();
	}
	
	private void makeTitle() {
		String title = _uiconfig.getTitle();
		_htmlResult = _htmlResult.replaceAll(TMP_TITLE, title);
	}
	
	private void loadTemplate() {
        _htmlTemplate = getTextFromFile(_conf_root + _conf_template);
		_htmlResult = _htmlTemplate;
	}
	
	public static String getTextFromFile(String fileName) {

		try {
			FileInputStream fileinputstream = new FileInputStream(fileName);
			
			int lenght = fileinputstream.available();
            byte bytes[] = new byte[lenght];
            fileinputstream.read(bytes);
            fileinputstream.close();
            return new String(bytes, "utf-8");
		} catch (IOException e) {
			log.error("",e);
			throw new JbusException(e);
		}
	}

	public static void writeTextToFile(String text,String fileName) {

		try {

			FileOutputStream fos = new FileOutputStream(fileName);
			
			fos.write(text.getBytes("utf-8"));
			
            fos.close();
            
		} catch (IOException e) {
			log.error("",e);
			throw new JbusException(e);
		}
	}
	
	public static String concatByUnderline(String... s) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<s.length;i++) {
			sb.append(s[i]);
			if (i<s.length-1) {
				sb.append("_");
			}
		}
		
		return sb.toString();
	}
	
	
}
