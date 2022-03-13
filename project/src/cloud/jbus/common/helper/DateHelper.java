package cloud.jbus.common.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


public class DateHelper {

	static Logger log = Logger.getLogger(DateHelper.class);

	static SimpleDateFormat _ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	static SimpleDateFormat _ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat _ymdhmsMs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
	
	public static String toYmdhms(Date date) {
		return _ymdhms.format(date);
	}

	public static String toYmdhmsMs(Date date) {
		return _ymdhmsMs.format(date);
	}
	

	public static Date fromYmdhm(String dateStr) {
		try {
			return _ymdhm.parse(dateStr);
		} catch (ParseException e) {
			
			log.error(e.getMessage(), e);
			
			return null;
		}
	}
	
	public static Date fromYmdhms(String dateStr) {
		try {
			return _ymdhms.parse(dateStr);
		} catch (ParseException e) {
			
			log.error(e.getMessage(), e);
			
			return null;
		}
	}
}
