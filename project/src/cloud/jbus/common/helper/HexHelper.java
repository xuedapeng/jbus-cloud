package cloud.jbus.common.helper;

import org.apache.commons.lang3.StringUtils;

public class HexHelper {

	/**
     * Convert byte[] to hex string
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
    	return bytesToHexString(src, true);
    }
    
    public static String bytesToHexStringNoBlank(byte[] src) {
    	return bytesToHexString(src, false);
    }

    private static String bytesToHexString(byte[] src, boolean blank) {

        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (blank) {
            	stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
    
    public static byte[] hexStringToBytes(String hexString) {
    	if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.replaceAll(" ", "").toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            
        }
        return d;
    }
    
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    
    // 1f -> 001f, b -> 000b
    public static String intTo2BytesStr(int v) {
    	
    	if (v > 65535 || v < 0) {
    		return "ffff";
    	}
    	
    	return String.format("%4s", Integer.toHexString(v)).replaceAll(" ", "0");
    	
    }
    
    // 高低位反转
    public static String reverseBit(String n4char) {
    	n4char = n4char.replace(" ", "");
    	if (n4char.length() != 4) {
    		return "FFFF";
    	}
    	
    	return n4char.substring(2, 4) + n4char.substring(0,2);
    	
    }

    /**
     * 2位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2ToInt(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (1 - i));
        }
        return intValue;
    }
    
}
