package com.weg.android.editor.util;


/**
 * Utilities for treating a {@code long} as a pair of {@code int}s
 */
public class IntPair {
	
	private IntPair() {
	}
	
	public static long of(int first, int second) {
		return (((long) first) << 32) | ((long) second & 0xffffffffL);
	}
	
	public static int first(long intPair) {
		return (int) (intPair >> 32);
	}
	
	public static int second(long intPair) {
		return (int) intPair;
	}
}
