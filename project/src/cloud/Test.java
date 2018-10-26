package cloud;

import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.helper.HexHelper;
import cloud.jbus.common.utils.FormatChecker;
import cloud.jbus.common.utils.Md5SaltTool;

public class Test {

	public static void main(String[] args) {
		String src = "artc8YT";
		byte[] b = src.getBytes();
		byte[] b1 = new byte[b.length];
		for (int i=0; i<b.length; i++) {
			b1[i] = (byte) (b[i] + 10);
		}
		String hex = HexHelper.bytesToHexString(b1);
		System.out.println(hex);
		
		byte[] b2 = HexHelper.hexStringToBytes(hex);
		byte[] b3 = new byte[b2.length];

		for (int i=0; i<b2.length; i++) {
			b3[i] = (byte) (b2[i] - 10);
		}
		
		System.out.println(new String(b3));
	}
}
