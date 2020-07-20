package cloud;

import java.util.HashMap;
import java.util.Map;

import fw.jbiz.common.conf.ZSystemConfig;

public class Test {

	public static void main(String[] args) {
		
		Map<String, Object> m = new HashMap<String, Object>();
		
		m.put("1", 2);
		
		System.out.println((Integer)m.get("1"));
		System.out.println((Integer)m.get("2"));
		System.out.println((Integer)m.get(null));


	}

}
