package com.daqsoft.baselib.utils;


import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * @Description 国密工具类
 * @ClassName SM4Util
 * @Author luoyi
 * @Time 202011/6 9:20
 */
public class SM4Util {

    public static void main(String[] args) throws Exception {
        String key = "JeF8U9wHFOMfs2Y8";
        String content = "李雨林123China";
        String encodeContent = "kDFCV1VoAeXiqhaD6ANwIYfPihXvb0XlkmLEaaOv4U4=";
        System.out.println("" + decryptByEcb(encodeContent, key));
    }

    private final static String KEY = "JeF8U9wHFOMfs2Y8";


    static {
        try {
            Security.addProvider(new BouncyCastleProvider());

        } catch (Exception e) {
        }
    }

    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    // 加密算法/分组加密模式/分组填充方式
    // PKCS5Padding-以8个字节为一组进行分组加密
    // 定义分组加密模式使用：PKCS5Padding
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    // 128-32位16进制；256-64位16进制
    public static final int DEFAULT_KEY_SIZE = 128;

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, new BouncyCastleProvider());
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static String encryptEcb(byte[] hexKey, String paramStr) throws Exception {
        String cipherText = null;
        byte[] keyData = hexKey;

        byte[] srcData = paramStr.getBytes(ENCODING);
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = Base64.toBase64String(cipherArray);
        return cipherText;
    }

    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static String decryptEcb(byte[] hexKey, String cipherText) throws Exception {
        String decryptStr = "";
        byte[] keyData = hexKey;
        byte[] cipherData = Base64.decode(cipherText);
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }

    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    public static boolean verifyEcb(byte[] hexKey, String cipherText, String paramStr) throws Exception {
        boolean flag = false;
        byte[] keyData = hexKey;
        byte[] cipherData = Base64.decode(cipherText);
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        byte[] srcData = paramStr.getBytes(ENCODING);
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }

    /**
     * SM4的ECB加密算法
     *
     * @param content 待加密内容
     * @param key     密钥
     * @return
     */
    public static String encryptByEcb(String content, String key) throws Exception {
        byte[] in = key.getBytes("UTF-8");
        String cipher = encryptEcb(in, content);
        return cipher;
    }

    /**
     * SM4的ECB加密算法
     *
     * @param content 待加密内容
     * @return
     */
    public static String encryptByEcb(String content) {
        String cipher = "";
        try {
            byte[] in = KEY.getBytes("UTF-8");
            cipher = encryptEcb(in, content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipher;
    }

    /**
     * SM4的ECB解密算法
     *
     * @param cipher 密文内容
     * @param key    密钥
     * @return
     */
    public static String decryptByEcb(String cipher, String key) throws Exception {
        byte[] in = key.getBytes("UTF-8");

        String plain = decryptEcb(in, cipher);
        return plain;
    }

    /**
     * SM4的ECB解密算法
     *
     * @param cipher 密文内容
     * @return
     */
    public static String decryptByEcb(String cipher) {
        String plain = "";
        try {
            byte[] in = KEY.getBytes("UTF-8");
            plain = decryptEcb(in, cipher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plain;
    }

}
