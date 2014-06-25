package com.solesurvivor.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

public class SSArrayUtil {
	
	public static final int BYTES_PER_FLOAT = 4;
	public static final int BYTES_PER_SHORT = 2;
	
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
	
	public static byte[] shortToByteArray(short[] data) {
		byte[] bytes = new byte[data.length * BYTES_PER_SHORT];
		
		for(int i = 0, j = 0; i < data.length; i++) {
			byte[] onefloat = ByteBuffer.allocate(BYTES_PER_SHORT).putShort(data[i]).array();
			bytes[j++] = onefloat[0];
			bytes[j++] = onefloat[1];
		}

		return bytes;
	}
	
	public static short[] byteToShortArray(byte[] data) {
		short[] shorts = new short[data.length / BYTES_PER_SHORT];
		ByteBuffer.wrap(data).order(ByteOrder.nativeOrder()).asShortBuffer().get(shorts);
		return shorts;
	}

	public static byte[] floatToByteArray(float[] data) {
		byte[] bytes = new byte[data.length * BYTES_PER_FLOAT];

		for(int i = 0, j = 0; i < data.length; i++) {
			byte[] onefloat = ByteBuffer.allocate(BYTES_PER_FLOAT).putFloat(data[i]).array();
			bytes[j++] = onefloat[0];
			bytes[j++] = onefloat[1];
			bytes[j++] = onefloat[2];
			bytes[j++] = onefloat[3];
		}

		return bytes;
	}
	
	public static float[] byteToFloatArray(byte[] data) {
		float[] floats = new float[data.length / BYTES_PER_FLOAT];
		ByteBuffer.wrap(data).order(ByteOrder.nativeOrder()).asFloatBuffer().get(floats);
		return floats;
	}
	
	public static FloatBuffer arrayToFloatBuffer(final int bytesPerFloat, float[] inputArray) {
		FloatBuffer fBuf = ByteBuffer.allocateDirect(inputArray.length * bytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		fBuf.put(inputArray).position(0);
		return fBuf;
	}
	
	public static ShortBuffer arrayToShortBuffer(final int bytesPerShort, short[] inputArray) {
		ShortBuffer sBuf = ByteBuffer.allocateDirect(inputArray.length * bytesPerShort).order(ByteOrder.nativeOrder()).asShortBuffer();
		sBuf.put(inputArray).position(0);
		return sBuf;
	}
	
	public static IntBuffer arrayToIntBuffer(final int bytesPerInt, int[] inputArray) {
		IntBuffer iBuf = ByteBuffer.allocateDirect(inputArray.length * bytesPerInt).order(ByteOrder.nativeOrder()).asIntBuffer();
		iBuf.put(inputArray).position(0);
		return iBuf;
	}

}
