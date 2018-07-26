package cloud.jbus.common.helper;

import java.util.Date;

public class GuidHelper {
	
	static long _lastTimeSec = 0L;
	
	public static String genUUID() {
		return java.util.UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	}
	
	
	// 每秒产生一个,8位guid
	public synchronized static String genSeq8() {
		
		while (true) {
			long now = new Date().getTime()/1000;
			if (now == _lastTimeSec) {
				continue;
			}
			_lastTimeSec = now;
			break;
		}
		
		char[] uid8src = Long.toHexString(_lastTimeSec).toUpperCase().toCharArray();
		
		char[] uid8 = {'0','0','0','0','0','0','0','0'};
		
		// 01234567 -> 64201357
		int[] seq = {6,4,2,0,1,3,5,7};
		for (int i=0;i<uid8.length;i++) {
			uid8[i] = uid8src[seq[i]];
		}
		
		return String.copyValueOf(uid8);
	}
	

	
}
