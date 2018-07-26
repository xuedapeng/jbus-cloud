package cloud.jbus.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import fw.jbiz.ext.json.ZGsonObject;
import fw.jbiz.logic.ZLogic;
import fw.jbiz.logic.ZLogicParam;
import fw.jbiz.logic.interfaces.IResponseObject;

@Consumes("application/json;charset=UTF-8")
@Produces("application/json;charset=UTF-8")
@Path("/")
public class DispatchAction extends BaseZAction {

	static Logger logger = Logger.getLogger(DispatchAction.class);

	static final String LOGIC_PKG = "cloud.jbus.logic";

	static {
		
		scanLogicClass(LOGIC_PKG);
	}
	
	@POST
	@Path("/")
	public String dispatch(
			String params,
			@Context HttpServletRequest request) {
		
		// params schema: {"method":"login", "auth":[secretId, secretKey],"data":{}}
		
		IResponseObject res = new  ZGsonObject();
		
		// 参数有效性
		Map<String, Object> mapParams = this.checkSchema(params, res);
		
		if (mapParams == null) {
			return res.toString();
		}
		
		// 设置参数
		ZLogicParam logicParam = this.makeParam(mapParams, res, request);
		if (logicParam == null) {
			return res.toString();
		}
		
		// 调用logic
		
		ZLogic logic = this.makeLogic(mapParams, res);
		if (logic == null) {
			return res.toString();
		}
		
		return logic.process(logicParam);
	}

}
