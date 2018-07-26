package cloud.jbus.common.helper;

import java.util.regex.Pattern;

public class NumericHelper {
	  public static boolean isInteger(String str) {  
	        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
	        return pattern.matcher(str).matches();  
	  }
}
