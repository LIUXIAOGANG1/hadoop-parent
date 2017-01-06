package com.github.lxg.hadoop.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
* @author : liuxiaogang
* @version ����ʱ�䣺2016��7��15�� ����2:02:18 
* ��˵�� 
*/
public class CommonUtils {
	private static final String SP = "|";
	private static final String SEPARATOR = "\\|";
	
	private static final String UTF_8 = "UTF-8";
	private static final String GB2312 = "gb2312";
	
	/**
	 * �Ѵ��зָ��� ��|�� ���ַ�����λ���
	 * @param addend	����
	 * @param augend	������
	 * @return	�ͣ��ַ���������|�����Ӻ�ģ�
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
	 * ͨ�����ӷ�����������������ַ���
	 * @param src	�������������
	 * @param split	���ӷ�
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
	 * ͨ�����ӷ� '|' ����������������ַ���
	 * @param src	�������������Ĭ�����ӷ�Ϊ ��|��
	 * @return	
	 */
	public static String join(Object[] src) {
		return join(src, SP);
	}
	
	/**
	 * ��������ʽ�ж��ַ����Ƿ�Ϊ���֣���������
	 * @param str	��У���ַ���
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
	 * �������С����� scale λ�����������룩
	 * @param divisor	����
	 * @param dividend	������
	 * @return	��
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
	 * ȥ�� target �������� str ֮��Ĳ��֣����û�г��֣��򷵻�ԭ�� target
	 * @param target	����ȡ�ַ���
	 * @param str	���ݴ��ַ������н�ȡ
	 * @return	��ȡ����ַ���
	 */
	public static String removeTail(String target, String str){
		int index = target.lastIndexOf(str);
		
		if(index < 0){
			return target;
		}
		
		return target.substring(0, index);
	}
	
	/**
	 * ��url�ַ�������
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
 