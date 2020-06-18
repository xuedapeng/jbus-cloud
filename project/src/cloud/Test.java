package cloud;

import fw.jbiz.common.conf.ZSystemConfig;

public class Test {

	public static void main(String[] args) {
		String key = ZSystemConfig.getEndecKey("/Users/xuedapeng/develop/tools/key/jbiz_key.txt");
		String en = ZSystemConfig.encrypt("jbuscloud5151", key);
		
		String de = ZSystemConfig.decrypt(en, key);
		
		System.out.println(en);
		System.out.println(de);

	}

}
