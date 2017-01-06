package com.github.lxg.hadoop.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;

/**
 * @author liuxiaogang
 * @version ����ʱ�䣺2016��6��3�� ����12:26:18 ��˵��
 */
public class DealHdfsFile {
	public static final String BACK_CART = "cart.add";
	
	public static void main(String[] args) throws Exception {
		// ��ȡhadoop�ļ�ϵͳ������
		Configuration conf = new Configuration();
		
		conf.set("url", "hdfs://hadoop1:9000/user/jiuqian/lxg/url/source/ad-brand.txt");

		for(String value : getHDFSFile(conf, "url")){
			System.out.println(value);
		}
	}
	
	
	/**
	 * ��ȡHDFS ��ָ��Ŀ¼�����ļ����ݣ����ļ����������ݼ��ص�list��
	 * 
	 * @param conf
	 * @param fileName
	 * @return
	 */
	public static List<String> getHDFSFile(Configuration conf, String fileName) {
		List<String> contentList = new ArrayList<String>();

		FSDataInputStream in = null;
		BufferedReader br = null;
		try {
			String uri = conf.get(fileName);
//			FileSystem fs = FileSystem.get(conf);
			FileSystem fs = FileSystem.get(new URI(uri), conf);

			Path rootPath = new Path(uri);
			if (!fs.exists(rootPath)) {
				return contentList;
			}

			CompressionCodecFactory factory = new CompressionCodecFactory(conf);
			CompressionCodec codec = factory.getCodec(rootPath);

			FileStatus fstatus = fs.getFileStatus(rootPath);
			if (fstatus.isFile()) {
				in = fs.open(rootPath);
				br = getBufferedReader(codec, in);

				String line = null;
				while ((line = br.readLine()) != null) {
					contentList.add(line);
				}
			} else {
				FileStatus[] stats = fs.listStatus(rootPath);

				for (int i = 0; i < stats.length; i++) {
					Path path = new Path(stats[i].getPath().toString());
					codec = factory.getCodec(path);

					in = fs.open(path);
					br = getBufferedReader(codec, in);

					String line = null;
					while ((line = br.readLine()) != null) {
						contentList.add(line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			IOUtils.closeStream(in);
		}

		return contentList;
	}
	
	/**
	 * �ж��ļ��Ƿ�ѹ��������ʽ���� BufferedReader
	 * @param codec
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader getBufferedReader(CompressionCodec codec, InputStream in) throws IOException{
		BufferedReader br = null;
		if (codec == null) {
			InputStreamReader is = new InputStreamReader(in, "utf-8");
			br = new BufferedReader(is);
		} else {
			CompressionInputStream comInputStream = codec.createInputStream(in);
			InputStreamReader is = new InputStreamReader(comInputStream, "utf-8");
			br = new BufferedReader(is);
		}
		return br;
	}
}
