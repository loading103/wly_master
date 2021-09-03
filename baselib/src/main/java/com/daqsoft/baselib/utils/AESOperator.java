package com.daqsoft.baselib.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AESOperator {

    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     * key，可自行修改
     */
    private String sKey = "smkldospdosldaaa";
    /**
     * 偏移量,可自行修改
     */
    private static String ivParameter = "0392039203920300";
    private static AESOperator instance = null;
    public static String KM_ = "kyanroidioscswen";

    public static String OSS_KEY = "wlcloud012345678";

    public static String OSS_IV = "cultural-tourism";

    private static String WLY_KEY = "js7ksl3nhnfivl4m";

    private static String WLY_IV = "3859345501849051";

    private AESOperator() {

    }

    public static AESOperator getInstance() {
        if (instance == null)
            instance = new AESOperator();
        return instance;
    }

    public static String Encrypt(String encData, String secretKey, String vector) throws Exception {

        if (secretKey == null) {
            return null;
        }
        if (secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes(StandardCharsets.UTF_8));
        // 此处使用BASE64做转码。
        return new BASE64Encoder().encode(encrypted);
    }

    public static String encryptWLYCBC(String encData) throws Exception {
        //偏移量
        byte[] iv = WLY_IV.getBytes();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = encData.getBytes();
            int length = dataBytes.length;
            //计算需填充长度
            if (length % blockSize != 0) {
                length = length + (blockSize - (length % blockSize));
            }
            byte[] plaintext = new byte[length];
            //填充
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keySpec = new SecretKeySpec(WLY_KEY.getBytes(), "CBC");
            //设置偏移量参数
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryped = cipher.doFinal(plaintext);

            return new BASE64Encoder().encode(encryped);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeWLYCBC(String decData) {
        if (decData == null) {
            return "";
        }
        try {
//            System.out.println(decData);
            byte[] raw = WLY_KEY.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "CBC");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec(WLY_IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            // 先用base64解密
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(decData);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString.trim();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String set16String(String str) {

        if (str.length() % 16 != 0) {

            while (str.length() % 16 != 0) {
                str = str + " ";
            }
        }
        return str;
    }

    /**
     * 解密
     */
    public static String decrypt(String sSrc, String dekey) throws Exception {
        try {
//            System.out.println(sSrc);
            byte[] raw = dekey.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // new BASE64Decoder().decodeBuffer(sSrc);
            // 先用base64解密
            byte[] encrypted1 = parseHexStr2Byte(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     */
    public static String decryptWLY(String sSrc) throws Exception {
        try {
//            System.out.println(sSrc);
            byte[] raw = WLY_KEY.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "CBC");
            Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            IvParameterSpec iv = new IvParameterSpec(WLY_IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // new BASE64Decoder().decodeBuffer(sSrc);
            // 先用base64解密
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 解密 OSS
     *
     * @param sSrc
     * @return
     * @throws Exception
     */
    public String decryptOss(String sSrc) throws Exception {
        try {
            byte[] raw = OSS_KEY.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            IvParameterSpec iv = new IvParameterSpec(OSS_IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 中科景信 des加密
     *
     * @return
     */
    public static String desJingXinEncrpt(String sSrc) {
        String result = "";
        String ivStr = "12345678";
        String keys = "zkjxsoft";
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes(StandardCharsets.UTF_8));
            byte[] raw = keys.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "DES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return new BASE64Encoder().encode(original);
        } catch (Exception e) {
            Log.e("e", e.getMessage());
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        // 需要加密的字串
//        String cSrc = "[{\"request_no\":\"1001\",\"service_code\":\"FS0001\",\"contract_id\":\"100002\",\"order_id\":\"0\",\"phone_id\":\"13913996922\",\"plat_offer_id\":\"100094\",\"channel_id\":\"1\",\"activity_id\":\"100045\"}]";
        String idCard = "510723199208161115";
        // 加密
//        long lStart = System.currentTimeMillis();
        String enString = AESOperator.encryptWLYCBC(idCard);
//        System.out.println("加密后的字串是：" + enString);

        // 解密
        String DeString = AESOperator.decryptWLY(enString);
//        System.out.println("解密后的字串是：" + DeString);
    }

}