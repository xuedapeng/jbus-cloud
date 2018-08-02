package cloud.jbus.action;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class MyCrossDomainFilter  implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletResponse hresp = (HttpServletResponse) resp;
			//跨域
			hresp.setHeader("Access-Control-Allow-Origin", "*");
			hresp.setHeader("access-control-allow-headers" , "Origin, X-Requested-With, Content-Type, Accept");
			hresp.setHeader("access-control-allow-methods", "GET,POST,OPTIONS");
			hresp.setHeader("access-control-allow-credentials", "true");
			// IE9以下
			hresp.setHeader("XDomainRequestAllowed","1");

			// Filter 只是链式处理，请求依然转发到目的地址。
			chain.doFilter(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}


}
