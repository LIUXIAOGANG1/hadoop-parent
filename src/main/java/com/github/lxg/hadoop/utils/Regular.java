package com.github.lxg.hadoop.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuxiaogang
 * @version 创建时间：2016年5月20日 上午10:21:10 类说明
 */
public class Regular {

	public static void main(String[] args) {
		String regex = ".+";
		String target = "==.";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);

		if (matcher.matches()) {
			System.out.println("ok");
		} else {
			System.out.println("not ok");
		}

		target = "http://item.jd.com/1369408834.html";
		regex = "http://item.jumei.com/(\\d+)\\.html";

		System.out.println(getMatcher(regex, target));
	}

	public static String getMatcher(String regex, String source) {
		String result = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);

		while (matcher.find()) {
			result = matcher.group(1);// 只取第一组
		}
		return result;
	}
}
