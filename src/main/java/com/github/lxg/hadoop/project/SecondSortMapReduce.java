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
* @version 创建时间：2016年10月12日 下午12:26:56 
* 类说明 
*/
public class SecondSortMapReduce {
	private static final String SP = "|";
	private static final String SEPARATOR = "\\|";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration(); // 获得配置文件对象
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
		// 合并小文件
		FileInputFormat.addInputPath(job, new Path(input)); // 设置map输入文件路径
		FileOutputFormat.setOutputPath(job, new Path(output)); // 设置reduce输出文件路径

		job.setPartitionerClass(OutputkeyHashCodePartitioner.class); // 设置自定义分区策略
		job.setSortComparatorClass(OutputKeyComparator.class); // 设置自定义二次排序策略
		job.setGroupingComparatorClass(OutputKeyGroupingComparator.class); // 设置自定义分组策略

		job.setInputFormatClass(TextInputFormat.class); // 设置文件输入格式
		
		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
		
		job.setMapperClass(SortMapper.class);
		job.setReducerClass(SortReducer.class);
		
		// 设置map的输出key和value类型
		job.setMapOutputKeyClass(OutputKey.class);
		job.setMapOutputValueClass(Text.class);
		// 设置reduce的输出key和value类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setNumReduceTasks(numTask);

		job.waitForCompletion(true);
		return job.isSuccessful() ? 0 : 1;
	}
	
	public static class SortMapper extends Mapper<Object, Text, OutputKey, Text> {
		/**
		 * 这里特殊要说明一下，为什么要将这些变量写在map函数外边。 对于分布式的程序，我们一定要注意到内存的使用情况，
		 * 对于mapreduce框架，每一行的原始记录的处理都要调用一次map函数，假设，此个map要处理1亿条输入记录，
		 * 如果将这些变量都定义在map函数里边则会导致这4个变量的对象句柄编程非常多（极端情况下将产生4*1亿个句柄，
		 * 当然java也是有自动的gc机制的，一定不会达到这么多，但是会浪费很多时间去GC），导致栈内存被浪费掉。
		 * 我们将其写在map函数外边， 顶多就只有4个对象句柄。
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
			// map输出
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
				 * 此处注意组合key中的 order 随value的遍历顺序而变化。简介证明数据进入 reduce 后依然是成 [key,value] 对儿出现，
				 * 而并非由一条 key 对应一组 value，只不过同时进入 reduce 中的所有 key 相同。
				 */
				System.out.println("reduce Input data:{[" + key.getKey() + "," + key.getOrder() + "], [" + value.toString() + "]}");
				
				sb.append(value.toString() + ",");
			}
			
			// 去除最后一个逗号
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			
			context.write(new Text(key.getKey()), new Text(sb.toString()));
			System.out.println("---------out reduce function flag---------");
		}
	}
}
 