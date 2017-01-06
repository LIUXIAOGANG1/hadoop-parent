package com.github.lxg.hadoop.utils;

/**
 * @author : liuxiaogang
 * @version ����ʱ�䣺2016��7��14�� ����11:06:01 ��˵��
 */
public class ArrayUtils {

	/**
	 * ��ʼ������Ϊ length ���������飬�ֱ�ֵΪ0��
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
	 * ��ʼ������Ϊ length �ĳ��������飬�ֱ�ֵΪ0L��
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
	 * ���������Ӧλ ���
	 * @param addend	���������飩
	 * @param augend	�����������飩
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
	 * �������������� ��Ӧλ �����
	 * @param source	ԭ����
	 * @param target	Ŀ������
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
