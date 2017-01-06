package com.github.lxg.hadoop.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * @author : liuxiaogang
 * @version ����ʱ�䣺2016��10��12�� ����11:07:33 ��˵��
 */
public class SegmentUtils {
	/**
	 * �Զ������key
	 * 
	 * @author jiuqian
	 *
	 */
	public static class OutputKey implements WritableComparable<OutputKey> {
		private String key = "";
		private Long order = 0l;

		public OutputKey() {
		}

		public OutputKey(String key) {
			this.key = key;
			this.order = 0l;
		}

		public OutputKey(String key, Long order) {
			this.key = key;
			this.order = order;
		}

		public String getKey() {
			return this.key;
		}

		public Long getOrder() {
			return this.order;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setOrder(Long order) {
			this.order = order;
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			this.key = in.readUTF();
			this.order = in.readLong();
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeUTF(key);
			out.writeLong(this.order);
		}

		@Override
		public int compareTo(OutputKey other) {
			return this.key.compareTo(other.getKey());
		}
	}

	/**
	 * �Զ����������
	 * 
	 * @author jiuqian
	 *
	 */
	public static class OutputkeyHashCodePartitioner extends Partitioner<OutputKey, Text> {
		@Override
		public int getPartition(OutputKey key, Text arg1, int numPartitions) {
			int partitionNum = 0;
			partitionNum = (int) (((key.getKey().trim().hashCode()) & Integer.MAX_VALUE) % numPartitions);
			return partitionNum;
		}
	}

	/**
	 * �Զ������������
	 * @author jiuqian
	 *
	 */
	public static class OutputKeyComparator extends WritableComparator {
		protected OutputKeyComparator() {
			super(OutputKey.class, true);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable w1, WritableComparable w2) {
			OutputKey p1 = (OutputKey) w1;
			OutputKey p2 = (OutputKey) w2;
			int cmp = p1.getKey().compareTo(p2.getKey());
			if (cmp != 0) {
				return cmp;
			}
			return p1.getOrder() == p2.getOrder() ? 0 : (p1.getOrder() < p2.getOrder() ? -1 : 1);
		}
	}

	/**
	 * 
	 * �Զ�����麯����reduce �׶Σ�
	 * 
	 * @author jiuqian
	 *
	 */
	public static class OutputKeyGroupingComparator extends WritableComparator {
		protected OutputKeyGroupingComparator() {
			super(OutputKey.class, true);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable o1, WritableComparable o2) {
			OutputKey p1 = (OutputKey) o1;
			OutputKey p2 = (OutputKey) o2;
			return p1.getKey().compareTo(p2.getKey());
		}
	}
}
