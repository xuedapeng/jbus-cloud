package uiconfig;

import cloud.jbus.db.bean.UiconfigEntity;
import cloud.jbus.service.uiengine.UIMaker;

public class Test {

	public static void main(String[] args) {
		
		UIMaker maker = new UIMaker(
				"/Users/xuedapeng/develop/github/jbus-cloud/html5/mui/",
				"uiconfigTemplate.html");
		
		UiconfigEntity uiconfig = new UiconfigEntity();
		uiconfig.setTitle("镜湖气象站");
		
		String elements = UIMaker.getTextFromFile(
				"/Users/xuedapeng/develop/github/jbus-cloud/html5/mui/test_elements.txt");
		System.out.println(elements);
		uiconfig.setElements(elements);
		
		uiconfig.setId(1);
		
		maker.make(uiconfig);
		System.out.println(maker._htmlResult);
		
		UIMaker.writeTextToFile(
				maker._htmlResult,
				"/Users/xuedapeng/develop/github/jbus-cloud/html5/mui/uiconfig_"
				+uiconfig.getId() + ".html"
				);
		
	}
	
	
}
