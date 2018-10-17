package cloud.jbus.common.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ValidateHelper {

	// return: error names
	public static String notEmptyCheck(String...fieldNameValues) {
		
		List<String> errorNames = new ArrayList<String>();
		
		for (int i=0; i<fieldNameValues.length; i++) {
			if (i%2==0) {
				continue;
			}
			if (StringUtils.isEmpty(fieldNameValues[i])) {
				errorNames.add(fieldNameValues[i-1]);
			}
		}
		
		return errorNames.stream().reduce("", (acc, element)->acc + (acc.isEmpty()?"":",") + element);
	}
	
	public static String checkLength(String...fieldNameValueMaxlengths) {

		List<String> errorNames = new ArrayList<String>();
		
		for (int i=0; i<fieldNameValueMaxlengths.length; i++) {
			if (i%3==0 || i%3==2) {
				continue;
			}
			
			if (StringUtils.isNotEmpty(fieldNameValueMaxlengths[i]) 
					&& fieldNameValueMaxlengths[i].length() > Integer.valueOf(fieldNameValueMaxlengths[i+1])) {
				
				errorNames.add(fieldNameValueMaxlengths[i-1] + ":" + fieldNameValueMaxlengths[i+1]);
			}
		}
		
		return errorNames.stream().reduce("", (acc, element)->acc + (acc.isEmpty()?"":",") + element);
		
	}
	

	// return: error names
	public static String integerCheck(String...fieldNameValues) {
		
		List<String> errorNames = new ArrayList<String>();
		
		for (int i=0; i<fieldNameValues.length; i++) {
			if (i%2==0) {
				continue;
			}
			if (!NumericHelper.isInteger(fieldNameValues[i])) {
				errorNames.add(fieldNameValues[i-1]);
			}
		}
		
		return errorNames.stream().reduce("", (acc, element)->acc + (acc.isEmpty()?"":",") + element);
	}
}
