package cloud.jbus.common.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fw.jbiz.logic.ZLogicParam;

public class ValidateHelper {

	static Logger logger = Logger.getLogger(ValidateHelper.class);
	
	/*
	 *  [[fieldName, not_empty("0"/"1"), max_len("0不校验"/1+), int(0/1)],[],...]
	 *  {
	 *  	{"deviceId", "1", "0", "1"},
	 *  	{"deviceName, "1", "20", "0"},
	 *  	...
	 *  }
	 */
	public static String checkByMatrix(ZLogicParam myParam, String[][] matrix ) {

		List<String> errorNames = new ArrayList<String>();
		
		for(int i = 0; i < matrix.length; i++) {
			String field = matrix[i][0];
			String itemError = "";
			boolean chkRequired = "1".equals(matrix[i][1])?true:false;
			Integer chkMaxLen = Integer.valueOf(matrix[i][2]);
			boolean chkInt = "1".equals(matrix[i][3])?true:false;
			
			String methodName = "get" + field.substring(0, 1).toUpperCase()  + field.substring(1);
			Method method;
			String value = null;
			try {
				method = myParam.getClass().getMethod(methodName);
	            value = (String) method.invoke(myParam);  
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "参数校验异常";
			} 
            
			if (chkRequired) {
				if (StringUtils.isEmpty(value)) {
					itemError += ",必须";
				}
			}
			
			if (StringUtils.isNotEmpty(value)) {
				if (chkMaxLen > 0 && value.length() > chkMaxLen) {
					itemError += (",长度限制(" + chkMaxLen + ")");
				}
				
				if (chkInt && !NumericHelper.isInteger(value)) {
					itemError += ",整数";
				}
			}
			
			if (itemError.length() > 0) {
				itemError = field + itemError;
				errorNames.add(itemError);
			}
		}

		return errorNames.stream().reduce("", (acc, element)->acc + (acc.isEmpty()?"":"; ") + element);
	}
	
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
