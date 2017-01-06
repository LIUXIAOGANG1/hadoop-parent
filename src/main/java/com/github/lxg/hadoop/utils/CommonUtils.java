package com.github.lxg.hadoop.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
* @author : liuxiaogang
* @version 创建时间：2016年7月15日 下午2:02:18 
* 类说明 
*/
public class CommonUtils {
	private static final String SP = "|";
	private static final String SEPARATOR = "\\|";
	
	private static final String UTF_8 = "UTF-8";
	private static final String GB2312 = "gb2312";
	
	/**
	 * 把带有分隔符 ‘|’ 的字符串对位相加
	 * @param addend	加数
	 * @param augend	被加数
	 * @return	和（字符串，被‘|’连接后的）
	 */
	public static String summation(String addend, String augend) {
		String[] addends = addend.split(SEPARATOR);
		String[] augends = augend.split(SEPARATOR);
		
		if(addends.length != augends.length){
			return addend;
		}

		for (int i = 0; i < addends.length; i++) {
			addends[i] = String.valueOf(Integer.parseInt(augends[i]) + Integer.parseInt(addends[i]));
		}

		return join(addends);
	}
	
	/**
	 * 通过连接符连接数组对象，生成字符串
	 * @param src	被连接数组对象
	 * @param split	连接符
	 * @return
	 */
	public static String join(Object[] src, String split) {
		if (src == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length; i++) {
			sb.append(src[i].toString());
			if (i < src.length - 1) {
				sb.append(split);
			}
		}
		return sb.toString();
	}

	/**
	 * 通过连接符 '|' 连接数组对象，生成字符串
	 * @param src	被连接数组对象，默认连接符为 ‘|’
	 * @return	
	 */
	public static String join(Object[] src) {
		return join(src, SP);
	}
	
	/**
	 * 用正则表达式判断字符串是否为数字（含负数）
	 * @param str	被校验字符串
	 * @return	boolean
	 */
	public static boolean isNumeric(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		String regEx = "^-?[0-9]+\\.?[0-9]+$";
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(str);
		if (mat.find()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 相除保留小数点后 scale 位，（四舍五入）
	 * @param divisor	除数
	 * @param dividend	被除数
	 * @return	商
	 */
	public static String divide(Object divisor, Object dividend, int scale){
		BigDecimal former = new BigDecimal(divisor.toString());
		BigDecimal latter = new BigDecimal(dividend.toString());
		
		int compare = latter.compareTo(BigDecimal.ZERO);
		if(compare == 0){
			return "0";
		}
		return former.divide(latter, scale, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	/**
	 * 去掉 target 中最后出现 str 之后的部分，如果没有出现，则返回原串 target
	 * @param target	待截取字符串
	 * @param str	依据此字符串进行截取
	 * @return	截取完的字符串
	 */
	public static String removeTail(String target, String str){
		int index = target.lastIndexOf(str);
		
		if(index < 0){
			return target;
		}
		
		return target.substring(0, index);
	}
	
	/**
	 * 对url字符串解码
	 * @param str
	 * @return
	 */
	public static String getString(String str) {
		String strGB2312 = "";
		String strUTF8 = "";

		try {
			strUTF8 = java.net.URLDecoder.decode(str, UTF_8);
			strGB2312 = java.net.URLDecoder.decode(str, GB2312);
			return strUTF8.length() < strGB2312.length() ? strUTF8 : strGB2312;
		} catch (Exception e) {
			return str;
		}
	}
}
 