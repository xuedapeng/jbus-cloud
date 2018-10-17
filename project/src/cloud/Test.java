package cloud;

import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.utils.FormatChecker;
import cloud.jbus.common.utils.Md5SaltTool;

public class Test {

	public static void main(String[] args) {
		
		System.out.println(FormatChecker.email("ab@.c@qq.com"));
	}
}
