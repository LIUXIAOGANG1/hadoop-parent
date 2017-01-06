package com.github.lxg.hadoop.project;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.github.lxg.hadoop.common.SegmentUtils.OutputKey;
import com.github.lxg.hadoop.common.SegmentUtils.OutputKeyComparator;
import com.github.lxg.hadoop.common.SegmentUtils.OutputKeyGroupingComparator;
import com.github.lxg.hadoop.common.SegmentUtils.OutputkeyHashCodePartitioner;

/** 
* @author : liuxiaogang
* @version ����ʱ�䣺2016��10��12�� ����12:26:56 
* ��˵�� 
*/
public class SecondSortMapReduce {
	private static final String SP = "|";
	private static final String SEPARATOR = "\\|";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(); // ��������ļ�����
		conf.set(TextOutputFormat.SEPERATOR, SP);

		String[] paras = new GenericOptionsParser(conf, args).getRemainingArgs();

		if (paras.length != 2) {
			System.out.println("input format <in> <out>");
			System.exit(2);
		}
		
		String inputPath = paras[0];
		String outputPath = paras[1];
		
		int result = backfillRefJob(conf, inputPath, outputPath, 24);
		System.exit(result == 0 ? 0 : 1);
	}
	
	public static int backfillRefJob(Configuration conf, String input, String output, int numTask)
			throws ClassNotFoundException, IOException, InterruptedException {
		Job job = Job.getInstance(conf, "SecondSortMapReduce");

		job.setJarByClass(SecondSortMapReduce.class);
		// �ϲ�С�ļ�
		FileInputFormat.addInputPath(job, new Path(input)); // ����map�����ļ�·��
		FileOutputFormat.setOutputPath(job, new Path(output)); // ����reduce����ļ�·��

		job.setPartitionerClass(OutputkeyHashCodePartitioner.class); // �����Զ����������
		job.setSortComparatorClass(OutputKeyComparator.class); // �����Զ�������������
		job.setGroupingComparatorClass(OutputKeyGroupingComparator.class); // �����Զ���������

		job.setInputFormatClass(TextInputFormat.class); // �����ļ������ʽ
		
		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		
		job.setMapperClass(SortMapper.class);
		job.setReducerClass(SortReducer.class);
		
		// ����map�����key��value����
		job.setMapOutputKeyClass(OutputKey.class);
		job.setMapOutputValueClass(Text.class);
		// ����reduce�����key��value����
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setNumReduceTasks(numTask);

		job.waitForCompletion(true);
		return job.isSuccessful() ? 0 : 1;
	}
	
	public static class SortMapper extends Mapper<Object, Text, OutputKey, Text> {
		/**
		 * ��������Ҫ˵��һ�£�ΪʲôҪ����Щ����д��map������ߡ� ���ڷֲ�ʽ�ĳ�������һ��Ҫע�⵽�ڴ��ʹ�������
		 * ����mapreduce��ܣ�ÿһ�е�ԭʼ��¼�Ĵ���Ҫ����һ��map���������裬�˸�mapҪ����1���������¼��
		 * �������Щ������������map���������ᵼ����4�������Ķ�������̷ǳ��ࣨ��������½�����4*1�ڸ������
		 * ��ȻjavaҲ�����Զ���gc���Ƶģ�һ������ﵽ��ô�࣬���ǻ��˷Ѻܶ�ʱ��ȥGC��������ջ�ڴ汻�˷ѵ���
		 * ���ǽ���д��map������ߣ� �����ֻ��4����������
		 */
		OutputKey combinationKey = new OutputKey();

		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			System.out.println("---------enter map function flag---------");
			String[] splited = value.toString().split(SEPARATOR);
			
			if(splited.length != 2){
				return;
			}
			
			combinationKey.setKey(splited[0]);
			combinationKey.setOrder(Long.parseLong(splited[1]));
			// map���
			context.write(combinationKey, new Text(splited[1]));
			System.out.println("---------out map function flag---------");
		}
	}

	public static class SortReducer extends Reducer<OutputKey, Text, Text, Text> {
		@Override
		protected void reduce(OutputKey key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			System.out.println("---------enter reduce function flag---------");
			StringBuffer sb = new StringBuffer();
			
			for(Text value : values){
				/**
				 * �˴�ע�����key�е� order ��value�ı���˳����仯�����֤�����ݽ��� reduce ����Ȼ�ǳ� [key,value] �Զ����֣�
				 * ��������һ�� key ��Ӧһ�� value��ֻ����ͬʱ���� reduce �е����� key ��ͬ��
				 */
				System.out.println("reduce Input data:{[" + key.getKey() + "," + key.getOrder() + "], [" + value.toString() + "]}");
				
				sb.append(value.toString() + ",");
			}
			
			// ȥ�����һ������
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			
			context.write(new Text(key.getKey()), new Text(sb.toString()));
			System.out.println("---------out reduce function flag---------");
		}
	}
}
 