package com.github.lxg.hadoop.utils;

/**
 * @author : liuxiaogang
 * @version 创建时间：2016年7月14日 上午11:06:01 类说明
 */
public class ArrayUtils {

	/**
	 * 初始化长度为 length 的整形数组，分别赋值为0；
	 * @param length
	 * @return
	 */
	public static Integer[] initIntArray(int length) {
		Integer[] array = new Integer[length];

		for (int i = 0; i < length; i++) {
			array[i] = 0;
		}

		return array;
	}

	/**
	 * 初始化长度为 length 的长整形数组，分别赋值为0L；
	 * @param length
	 * @return
	 */
	public static Long[] initLongArray(int length) {
		Long[] array = new Long[length];

		for (int i = 0; i < length; i++) {
			array[i] = 0L;
		}

		return array;
	}
	
	/**
	 * 两个数组对应位 相加
	 * @param addend	加数（数组）
	 * @param augend	被加数（数组）
	 * @return
	 */
	public static Long[] addArray(Long[] addend, Long[] augend){
		if(addend.length != augend.length){
			return addend;
		}
		
		for(int i = 0; i < addend.length; i++){
			addend[i] = addend[i] + augend[i];
		}
		
		return addend;
	}
	
	/**
	 * 把两个整形数组 对应位 或操作
	 * @param source	原数组
	 * @param target	目标数组
	 * @return
	 */
	public static Integer[] bitOperation(Integer[] source, Integer[] target){
		if(source.length != target.length){
			return target;
		}
		
		for(int i = 0; i < source.length; i++){
			target[i] = target[i]|source[i];
		}
		
		return target;
	}
}
