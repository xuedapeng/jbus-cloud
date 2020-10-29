package cloud;

import java.util.HashMap;
import java.util.Map;

import fw.jbiz.common.conf.ZSystemConfig;

public class Test {

	public static void main(String[] args) {
		
		String s = "\n\n\n\n\n\n\n[\n    ,\n,\n]        \"sensorNo\": \"21\",\n        \"field\"\n        \"cmd\": \"21: 2\"\n    }\n] 	\n\n	\n\n \n\n";
		
		s = s.replaceAll("^\n+\\[", "[").replaceAll("\\][\n|\t| ]+$", "]");
		
		System.out.println(s);
	}

}
