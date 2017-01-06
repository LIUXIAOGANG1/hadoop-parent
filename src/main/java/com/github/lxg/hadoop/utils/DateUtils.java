package com.github.lxg.hadoop.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @author liuxiaogang
* @version 创建时间：2016年6月3日 下午12:26:38
* 类说明
*/
public class DateUtils {
	private static final String DEFAULTFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/*
	 * 从yyyy-MM-dd HH:mm:ss 转换为 Date
	 * 
	 * */
	public static Date getDateByString(String time, String format){
		DateFormat fmt = new SimpleDateFormat(format);
		try {
			return fmt.parse(time);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date getDateByString(String time){
		return getDateByString(time, DEFAULTFORMAT);
	}
	
	/*
	 * 从yyyy-MM-dd HH:mm:ss 获得Long(毫秒数)
	 * 
	 * */
	public static Long getMilliSeconds(String time){
		Date date = getDateByString(time);
		return date.getTime();
	}
	
	/*
	 * 从Long(毫秒数) 转为yyyy-MM-dd HH:mm:ss
	 * 
	 * */
	public static String getStringDate(Long seconds, String formate){
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Date date = new Date(seconds);
		return sdf.format(date);
	}
	
	public static String getStringDate(Long seconds){
		return getStringDate(seconds, DEFAULTFORMAT);
	}
}
