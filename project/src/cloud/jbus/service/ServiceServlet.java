package cloud.jbus.service;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import cloud.jbus.proxy.MqttProxy;


public class ServiceServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(ServiceServlet.class);

	@Override
	public void init() {

//		MqttProxy.init();

		// 在线状态更新
//		EventService.subscribeEventOfAll();
//		EventService.updateStatusOnStart();
		
	}
	
	
}
