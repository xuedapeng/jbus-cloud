package cloud.jbus.common.utils;

import java.util.regex.Pattern;

public class FormatChecker {

	static String EMAIL = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
	
	public static boolean email(String email) {
		
		return Pattern.matches(EMAIL, email);
	}
	
	
	
}
