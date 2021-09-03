package com.daqsoft.baselib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @Description 本地校验数据工具
 * @ClassName  RegexUtil
 * @Author     luoyi
 * @Time        2020/5/14 11:40
 */
public class RegexUtil {

	public final static String userName = "[azA-Z0-9_]{5,18}";
	public final static String userPass = "[a-zA-Z0-9_]{6,18}";
	public final static String nickName ="[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
	//	public final static String mobilePhone = "^1[3-7|8]\\d{9}$";
	//手机号码验证
	public final static String mobilePhone = "^1[23456789][0-9]{9}$|^0[0-9]{2,3}[-_/\\\\.]?[0-9]{7,8}$";

	public final static String email ="^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$";
	public final static String adderss = "{0,50}$";
	public final static String isUserLogin = "^\\{\\\"code\\\":110001,\\}$";

	public final static String idCard_15 = "^[1-9]\\\\d{7}((0[1-9])||(1[0-2]))((0[1-9])||(1\\\\d)||(2\\\\d)||(3[0-1]))\\\\d{3}$";
	private final static String idCard_18 ="^[1-9]\\\\d{5}[1-9]\\\\d{3}((0[1-9])||(1[0-2]))((0[1-9])||(1\\\\d)||(2\\\\d)||(3[0-1]))\\\\d{3}([0-9]||X)$";
	private final static String chineseOrEnglish = "[\\u4e00-\\u9fa5a-zA-Z]{1,20}";
	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobilePhone(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern
				.compile(mobilePhone); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	public static boolean isHaveSpecialStr(String str){
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern
				.compile(nickName); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	/**
	 * 邮箱验证
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isEmail(String str){
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern
				.compile(email); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	/**
	 * 密码验证
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isUserPwd(String str){
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern
				.compile(userPass); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isTelPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^(010|02\\d|0[3-9]\\d{2})-\\d{6,8}$"); // 验证带区号的
		p2 = Pattern.compile("^(010|02\\d|0[3-9]\\d{2})-?\\d{6,8}$"); // 验证没有区号的
		if (str.length() >10) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}
	public static boolean isIdCard(String idCard){
		Pattern p1 = null;
		Matcher m = null;
//		if(idCard.trim().length()==15){
//			p1 = Pattern.compile(idCard_15);
//			m =p1.matcher(idCard);
//			return m.matches();
//		}else
		if(idCard.trim().length()==18){
			p1 =Pattern.compile(idCard_18);
			m = p1.matcher(idCard);
			return m.matches();
		}else {
			return false;
		}
	}

	public static boolean isChineseOrEnglish(String str){
		Pattern p1 = null;
		Matcher m = null;
		p1=Pattern.compile(chineseOrEnglish);
		m = p1.matcher(str);
		return m.matches();
	}

	/**
	 * 判断一个字符是否是中文
 	 */
	public static boolean isChinese(char c) {
		return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
	}

	/**
	 * 判断一个字符串是否含有中文
 	 */
	public static boolean isChinese(String str) {
		if (str == null)
			return false;
		for (char c : str.toCharArray()) {
			if (isChinese(c))
				return true;// 有一个中文字符就返回
		}
		return false;
	}
}
