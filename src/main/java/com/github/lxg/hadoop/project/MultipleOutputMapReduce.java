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
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MultipleOutputMapReduce {
	private static final String SP = "|";

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(); // 获得配置文件对象
		conf.set(TextOutputFormat.SEPERATOR, SP);

		String[] paras = new GenericOptionsParser(conf, args).getRemainingArgs();

		if (paras.length != 2) {
			System.out.println("input format <in> <out>");
			System.exit(2);
		}

		int result = multipleJob(conf, paras[0], paras[1], 48);
		System.exit(result == 0 ? 0 : 1);
	}
	
	public static int multipleJob(Configuration conf, String input, String output, int numTask)
			throws ClassNotFoundException, IOException, InterruptedException {
		Job job = Job.getInstance(conf, "MultipleOutputMapReduce_Job");

		job.setJarByClass(MultipleOutputMapReduce.class);
		// 合并小文件
		job.setInputFormatClass(TextInputFormat.class);

		FileInputFormat.addInputPaths(job, input);
		FileOutputFormat.setOutputPath(job, new Path(output)); // 输出目录应该是一个先前不存在的地址,不然会报错..

		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

		job.setMapperClass(MultipleMapper.class);
		job.setReducerClass(MultipleReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setNumReduceTasks(numTask);

		job.waitForCompletion(true);
		return job.isSuccessful() ? 0 : 1;
	}
	
	public static class MultipleMapper extends Mapper<Object, Text, Text, Text> {
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
		}
	}

	public static class MultipleReducer extends Reducer<Text, Text, Text, Text> {
		private MultipleOutputs<Text, Text> multipleOutputs;

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			multipleOutputs = new MultipleOutputs<Text, Text>(context);
		}

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			if ("target".equals("multiple")) {
				multipleOutputs.write(new Text(""), new Text(""), "nobuyer");
			} else {
				context.write(new Text(""), new Text(""));
			}
		}

		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			// 需关闭，否则数据会出错
			multipleOutputs.close();
		}
	}
}
