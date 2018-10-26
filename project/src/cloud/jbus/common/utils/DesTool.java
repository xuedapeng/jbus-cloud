package cloud.jbus.common.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cloud.jbus.common.helper.HexHelper;

public class DesTool {
	//测试
    public static void main(String args[]) {
         //待加密内容
         String str = "BCFA496FC1";
         //密码，长度要是8的倍数
         String password = "2A833D59E99E4BCF877B7A4BD4278299";

         String result = DesTool.encrypt(str, password);
         System.out.println("加密后："+ result);
         //直接将如上内容解密
         try {
                 String decryResult = DesTool.decrypt(result, password);
                 System.out.println("解密后："+ decryResult);
         } catch (Exception e1) {
                 e1.printStackTrace();
         }
    }
    /**
     * 加密
     * @param datasource byte[]
     * @param password String
     * @return byte[]
     */
    public static  String encrypt(String datasource, String password) {            
        try{
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        //Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");
        //用密匙初始化Cipher对象,ENCRYPT_MODE用于将 Cipher 初始化为加密模式的常量
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
        //现在，获取数据并加密
        //正式执行加密操作
        byte[] result = cipher.doFinal(datasource.getBytes()); //按单部分操作加密或解密数据，或者结束一个多部分操作
        
        return HexHelper.bytesToHexString(result).replaceAll(" ", "");
        
        }catch(Throwable e){
                e.printStackTrace();
        }
        return null;
}
    /**
     * 解密
     * @param src byte[]
     * @param password String
     * @return byte[]
     * @throws Exception
     */
    public static String decrypt(String src, String password) throws Exception {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");//返回实现指定转换的 Cipher 对象
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            // 真正开始解密操作
            byte[] result = cipher.doFinal(HexHelper.hexStringToBytes(src));
            return new String(result);
        }
}
