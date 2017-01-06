package com.github.lxg.hadoop.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxiaogang
 * @version 创建时间：2016年6月3日 下午12:25:59 类说明
 */
public class DealLocalFile {
	private static final String UTF_8 = "UTF-8";
	private static final String GB2312 = "gb2312";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		String readPath = "D:\\localFile\\read";
		String writePath = "D:\\localFile\\write\\write.txt";

		File file = new File(readPath);
		if (!file.exists()) {
			file.mkdir();
		}

		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(writePath), "UTF-8");
		BufferedWriter out = new BufferedWriter(write);
		
		String[] fileList = file.list();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for (String fl : fileList) {
			String newPath = readPath + "\\" + fl;
			FileInputStream in = new FileInputStream(newPath);
			InputStreamReader is = new InputStreamReader(in, "UTF-8");
			BufferedReader br = new BufferedReader(is);
			
			String line = "";
			String result = "";
			String[] splite;
			try {
				while ((line = br.readLine()) != null) {
					splite = line.split("->");
					
					for(String val : splite){
						if(map.containsKey(val)){
							int count = map.get(val);
							count++;
							map.put(val, count);
						}else{
							map.put(val, 1);
						}
					}
					
//					String keyword = getString(getString(splite[6].trim().split("\\.")[5]));
//					
//					result = line + "|" + keyword;
//					
//					out.write(result);
//					out.newLine();
//					out.flush(); // 把缓存区内容压入文件
				}

				out.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for(Map.Entry<String, Integer> entry : map.entrySet()){
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
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
