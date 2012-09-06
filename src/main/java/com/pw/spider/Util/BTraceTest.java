package com.pw.spider.Util;

import java.util.Arrays;

public class BTraceTest {
   
	public int[] mergeArray(int one[],int two[]) {
		int[] result=new int[one.length+two.length];
		System.arraycopy(one, 0, result, 0, one.length);
		System.arraycopy(two, 0, result, one.length, two.length);
		return result;
	}
	
	public static void main(String args[]) {
		try {
			Thread.sleep(60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 System.out.println(Arrays.toString(new BTraceTest().mergeArray(new int[]{1,2,3}, new int[]{4,5,6})));
	}
	
}
