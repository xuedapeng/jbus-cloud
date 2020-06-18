import java.io.IOException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.ImmutableMap;

import cloud.jbus.common.helper.JsonBuilder;
import fw.jbiz.common.helper.httpclient.HttpHelper;

public class Test {

	static CookieStore cookieStore = null;
	
	public static void main(String[] args) {
		
		String response = doPost(
				"http://124.95.128.250:808/api/equ/curve/getdata", 
				JsonBuilder.build()
					.add("ID", "12459")
					.add("DataType_ID", 0)
					.add("DateRange", "2019-08-26 00:00 到 2019-08-26 16:01")
					.toString());

//		String response = doPost(
//				"http://124.95.128.250:808/loginbs/doAction", 
//				JsonBuilder.build()
//					.add("usercode", "kfcx")
//					.add("password", "123456")
//					.add("browserType", "Computer")
//					.add("city", "安徽省芜湖市")
//					.toString());
		
		
		System.out.println(response);
	}
	
		
	  public static String doPost4Cookie(String url, String jsonStr) {
		  
		  
		  CloseableHttpClient client = HttpClients.custom()
			        .setDefaultCookieStore(cookieStore).build();
		    HttpPost post = new HttpPost(url);
		    
		    try {

			      StringEntity s = new StringEntity(jsonStr, "UTF-8");
			      post.setHeader("Content-Type", "application/json");
			      HttpResponse httpResponse = client.execute(post);
		      // 执行get请求
//		      HttpResponse httpResponse = client.execute(httpGet);
//		      System.out.println("cookie store:" + cookieStore.getCookies());
		      printResponse(httpResponse);
		    } catch (IOException e) {
		      e.printStackTrace();
		    } finally {
		      try {
		        // 关闭流并释放资源
		        client.close();
		      } catch (IOException e) {
		        e.printStackTrace();
		      }
		    }
			    
			    
		return null;
		  
	  }
	  
	  public static String doPost(String url, String jsonStr){
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost post = new HttpPost(url);
	    String result = null;
	    
	    try {
	      StringEntity s = new StringEntity(jsonStr, "UTF-8");
	      // s.setContentEncoding("UTF-8");
//	      s.setContentType("application/json;charset=UTF-8");//发送json数据需要设置contentType
	      
	      post.setEntity(s);
	      post.setHeader("cookie", 
	    		  ".ASPXAUTH=3847593DF331E53597C3F021F845D57BB4508789F7E4E9B6CCFA2CB2FFCA7440689FCFAB068EFB0CF2233E601457AB1220BA1A526D7D27731DD941A69D09707A9D35EEB8E5C07D17B4E1D3627696CB8AF0C65D71EFD2D9864E8A50F3D3D259A7F5EC68C5CF1E9F998F2E604722F8CB61759907A6E3884B991E5F674D66EDA30DFF5A31AA1A29838C74CFA9C34687A8E941702C95996EC443760DAE8F51F43DBC29D302174D160EF54E1E55CE36E565608016A344C5E81EB900F2D2BDD97AD1BD011BFAD3CFDD2E1DD524F3801AA64ADF8E9613FA3DCCF4CDA84B46AAA75FE85418A4F900A66985B52C92F163D282947F86B2DF1B82967B69D1870648BA0AFCAC; expires=Tue, 03-Sep-2019 16:31:51 GMT; path=/; HttpOnly");
	    post.setHeader("Content-Type", "application/json");
	      HttpResponse res = client.execute(post);

//	      System.out.println("cookie:" + res.getFirstHeader("Set-Cookie").getValue());

//	      printResponse(res);
	      
	      if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
	        HttpEntity entity = res.getEntity();
	        result = EntityUtils.toString(entity);// 返回json格式：
	        
	      } else {

	    	  System.out.println(res.toString());
	      }
	    } catch (Exception e) {
	      
	    	e.printStackTrace(System.out);
	    }
	    return result;
	  }
	  

	  public static void printResponse(HttpResponse httpResponse)
		      throws ParseException, IOException {
		    // 获取响应消息实体
		    HttpEntity entity = httpResponse.getEntity();
		    // 响应状态
		    System.out.println("status:" + httpResponse.getStatusLine());
		    System.out.println("headers:");
		    HeaderIterator iterator = httpResponse.headerIterator();
		    while (iterator.hasNext()) {
		      System.out.println("\t" + iterator.next());
		    }
		    // 判断响应实体是否为空
		    if (entity != null) {
		      String responseString = EntityUtils.toString(entity);
		      System.out.println("response length:" + responseString.length());
		      System.out.println("response content:"
		          + responseString.replace("\r\n", ""));
		    }
		  }
	  
	  

	  public static void setCookieStore(HttpResponse httpResponse) {
	    System.out.println("----setCookieStore");
	    cookieStore = new BasicCookieStore();
	    // JSESSIONID
	    String setCookie = httpResponse.getFirstHeader("Set-Cookie")
	        .getValue();
	    String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
	        setCookie.indexOf(";"));
	    System.out.println("JSESSIONID:" + JSESSIONID);
	    // 新建一个Cookie
	    BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",
	        JSESSIONID);
	    cookie.setVersion(0);
	    cookie.setDomain("127.0.0.1");
	    cookie.setPath("/CwlProClient");
	    // cookie.setAttribute(ClientCookie.VERSION_ATTR, "0");
	    // cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "127.0.0.1");
	    // cookie.setAttribute(ClientCookie.PORT_ATTR, "8080");
	    // cookie.setAttribute(ClientCookie.PATH_ATTR, "/CwlProWeb");
	    cookieStore.addCookie(cookie);
	  }



}
