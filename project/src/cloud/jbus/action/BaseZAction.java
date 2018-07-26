package cloud.jbus.action;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.logic.BaseZLogicParam;
import cloud.jbus.logic.share.annotation.Action;
import fw.jbiz.action.ZAction;
import fw.jbiz.common.ZException;
import fw.jbiz.common.helper.AnnotationHelper;
import fw.jbiz.common.helper.BeanHelper;
import fw.jbiz.logic.ZLogic;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseZAction extends ZAction {

	static Logger logger = Logger.getLogger(BaseZAction.class);

	// method, ZLogic
	static Map<String, Class<? extends ZLogic>> _logicClassMap = new HashMap();
	// method, BaseZLogicParam
	static Map<String, Class<? extends BaseZLogicParam>> _logicParamClassMap = new HashMap();
	
	// make logic
	protected ZLogic makeLogic(Map<String, Object> mapParams, final IResponseObject res) {
		
		try {
			ZLogic logic = _logicClassMap.get(mapParams.get("method")).newInstance();
			
			return logic;
			
			
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("", e);
			
			res.add("status", -1)
			.add("msg", "unkown error!");
			
			return null;
		}
		
		
		
	}
	// make param
	protected ZLogicParam makeParam(Map<String, Object> mapParams, final IResponseObject res, final HttpServletRequest request) {

		try {
			BaseZLogicParam logicParam = _logicParamClassMap.get(mapParams.get("method")).newInstance();
		
			// 设置 auth
			if (mapParams.containsKey("auth")) {
	
				List<String> auth = (List<String>)mapParams.get("auth");
				if (auth.size() == 2) {
					String secretId = auth.get(0);
					String secretKey = auth.get(1);
					logicParam.setSecretId(secretId);
					logicParam.setSecretKey(secretKey);
				}
			}
			
			// 设置request
			logicParam.setRequest(request);
			
			// 设置属性
			if (mapParams.containsKey("data")) {
				Map data = (Map) mapParams.get("data");
				BeanHelper.setValuesFromJson(logicParam, JsonHelper.map2json(data));
			}

			return logicParam;
			
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("", e);
			
			res.add("status", -1)
			.add("msg", "unkown error!");
			
			return null;
		}

	}
	
	// check schema
	protected Map<String, Object> checkSchema(String params, final IResponseObject res) {

		Map<String, Object> mapParams;
		
		try {
			mapParams = JsonHelper.json2map(params);
			if (!mapParams.containsKey("method")) {
				res.add("status", -11)
					.add("msg", "need method!");
				return null;
			}
			
			String method = (String)mapParams.get("method");
			
			if (!_logicClassMap.containsKey(method)) {
				res.add("status", -11)
					.add("msg", "invalid method!");
				return null;
			}
			
			if (mapParams.containsKey("auth")) {

				List<String> auth = (List<String>)mapParams.get("auth");
				if (auth.size() != 2) {
					res.add("status", -11)
						.add("msg", "invalid auth!");
					return null;
				}
			}
			
		
		} catch(Exception e) {
			
			logger.error("", e);
			
			res.add("status", -11)
				.add("msg", "参数格式错误");
			
			return null;
		}
		
		return mapParams;
	}
	
	// gether logic annotation info
	protected static void scanLogicClass(String packageName) {

		Map<String, Annotation> annMap = AnnotationHelper.getAnnotationOnClass(packageName, Action.class);
		
		for(String clsName: annMap.keySet()) {
			Action annotation = (Action)annMap.get(clsName);
			String method = annotation.method();
			
			// path 不可重复
			if (_logicClassMap.containsKey(method)) {
				String message = String.format("duplicate method for Logic Class: class=%s, method=%s", 
						clsName, method);
						
				logger.error(message);
				throw new ZException("jbus-cloud", message);
			}
			
			try {

				Class<? extends ZLogic> logicClass = (Class<? extends ZLogic>) Class.forName(clsName);
				
				_logicClassMap.put(method, logicClass);
				
				String pkg = logicClass.getPackage().getName();
				String clzName = logicClass.getSimpleName();
				
				// logic.param.XxxLogicParam;
				String paramClsName = pkg + ".param." + clzName + "Param";

				Class<? extends BaseZLogicParam> paramClass = (Class<? extends BaseZLogicParam>) Class.forName(paramClsName);

				_logicParamClassMap.put(method, paramClass);

				logger.info(String.format("logic class found:\n method=%s, class=%s", method, clsName));
				logger.info(String.format("logicParam class found:\n method=%s, class=%s", method, paramClsName));
			} catch (ClassNotFoundException e) {
						
				logger.error(trace(e));
				throw new ZException("jbus-cloud", e);
			}
		}
		
		if (_logicClassMap.isEmpty()) {
			
			String msg = String.format("not logic class found in %s", packageName);
			logger.error(msg);
			throw new ZException("jbus-cloud", msg);
		}
	}
}
