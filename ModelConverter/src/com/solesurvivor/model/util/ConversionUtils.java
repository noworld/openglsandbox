package com.solesurvivor.model.util;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ConversionUtils {
	
	private static final char POUND = '#';

	public static int[] parseIntArray(String s, String delimiter) {
		String[] strings = s.split(delimiter);
		int[] ints = new int[strings.length];

		for(int i = 0; i < strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		}

		return ints;
	}
	
	public static int[] parseIntArray(List<BigInteger> bigInts) {
		int[] ints = new int[bigInts.size()];

		for(int i = 0; i < bigInts.size(); i++) {
			ints[i] = bigInts.get(i).intValue();
		}

		return ints;
	}

	public static int[] parseFloatArray(String s, String delimiter) {
		String[] strings = s.split(delimiter);
		int[] ints = new int[strings.length];

		for(int i = 0; i < strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		}

		return ints;
	}

	public static float[] parseFloatArray(List<Double> doubles) {
		float[] floats = new float[doubles.size()];
		
		for(int i = 0; i < doubles.size(); i++) {
			floats[i] = doubles.get(i).floatValue();
		}
		
		return floats;
	}
	
	public static String chopChar(String s, char c) {
		if(StringUtils.isBlank(s)) return null;
		
		String strVal = s;
		
		if(s.charAt(0) == c) {
			strVal = s.substring(1);
		}
		
		return strVal;
		
	}
	
	public static String chopPound(String s) {
		return chopChar(s, POUND);	
	}

	
}
