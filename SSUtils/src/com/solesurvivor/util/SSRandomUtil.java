package com.solesurvivor.util;

import java.util.Random;

public class SSRandomUtil {

	@SuppressWarnings("unused")
	private static final String TAG = SSRandomUtil.class.getSimpleName();
	
	public static float clampRandomF(Random randy, float low, float high) {
		return ((high - low) * randy.nextFloat()) + low;
	}
}
