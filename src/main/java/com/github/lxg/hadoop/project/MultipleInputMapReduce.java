package com.github.lxg.hadoop.project;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MultipleInputMapReduce {
	private static final String SP = "|";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(); // 获得配置文件对象
		conf.set(TextOutputFormat.SEPERATOR, SP);

		String[] paras = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (paras.length != 3) {
			System.out.println("input format <conifg> <in> <out>");
			System.exit(2);
		}

		String config_in = paras[0];
		String storecode_in = paras[1];
		String outputPath = paras[2];

		int result = multipleJob(conf, config_in, storecode_in, outputPath + File.separator + "mid", 24);
		System.exit(result == 0 ? 0 : 1);
	}
	
	public static int multipleJob(Configuration conf, String config_in, String storecode_in, String output, int numTask)
			throws ClassNotFoundException, IOException, InterruptedException {
		Job job1 = Job.getInstance(conf, "MultipleMapReduce_multipleJob");

		job1.setJarByClass(MultipleInputMapReduce.class);

		FileOutputFormat.setCompressOutput(job1, true);
		FileOutputFormat.setOutputCompressorClass(job1, GzipCodec.class);

		MultipleInputs.addInputPath(job1, new Path(config_in), TextInputFormat.class, ConfigMapper.class);
		MultipleInputs.addInputPath(job1, new Path(storecode_in), TextInputFormat.class, StorecodeMapper.class);
		job1.setReducerClass(MultipleReducer.class);

		FileOutputFormat.setOutputPath(job1, new Path(output)); // 输出目录应该是一个先前不存在的地址,不然会报错..
		
		job1.setMapOutputKeyClass(Text.class);
		job1.setMapOutputValueClass(Text.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(Text.class);

		job1.setNumReduceTasks(numTask);

		job1.waitForCompletion(true);
		return job1.isSuccessful() ? 0 : 1;
	}
	
	/**
	 * 
	 */
	public static class ConfigMapper extends Mapper<Object, Text, Text, Text>{
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
		}
	}
	
	/**
	 * 
	 */
	public static class StorecodeMapper extends Mapper<Object, Text, Text, Text>{
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
		}
	}
	
	/**
	 * 
	 */
	public static class MultipleReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			
		}
	}
}
