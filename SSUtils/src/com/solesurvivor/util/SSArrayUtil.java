package com.solesurvivor.util;

import java.lang.reflect.Array;
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
	public static final int BYTES_PER_INT = 4;
	
	public static FloatBuffer bytesToFloatBufBigEndian(byte[] bytes) {
		float[] floats = new float[bytes.length / BYTES_PER_FLOAT];
		ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asFloatBuffer().get(floats);		
		FloatBuffer fBuf = SSArrayUtil.arrayToFloatBuffer(floats);
		fBuf.rewind();
		return fBuf;
		
	}
	
	public static ShortBuffer bytesToShortBufBigEndian(byte[] bytes) {
		short[] ibo = new short[bytes.length / BYTES_PER_SHORT];
		ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(ibo);		
		ShortBuffer sBuf = SSArrayUtil.arrayToShortBuffer(ibo);
		sBuf.rewind();
		return sBuf;
		
	}
	
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

	public static float[] parseFloatArray(String s, String delimiter) {
		String[] strings = s.split(delimiter);
		float[] floats = new float[strings.length];

		for(int i = 0; i < strings.length; i++) {
			floats[i] = Float.parseFloat(strings[i]);
		}

		return floats;
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
	
	public static ShortBuffer arrayToShortBuffer(byte[] inputArray) {
		ByteBuffer bBuf = ByteBuffer.allocateDirect(inputArray.length).order(ByteOrder.nativeOrder());		
		bBuf.put(inputArray).rewind();
		return bBuf.asShortBuffer();
	}
	
	public static FloatBuffer arrayToFloatBuffer(byte[] inputArray) {
		ByteBuffer bBuf = ByteBuffer.allocateDirect(inputArray.length).order(ByteOrder.nativeOrder());		
		bBuf.put(inputArray).rewind();		
		return bBuf.asFloatBuffer();
	}
	
	public static FloatBuffer arrayToFloatBuffer(float[] inputArray) {
		FloatBuffer fBuf = ByteBuffer.allocateDirect(inputArray.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		fBuf.put(inputArray).position(0);
		return fBuf;
	}
	
	public static ShortBuffer arrayToShortBuffer(short[] inputArray) {
		ShortBuffer sBuf = ByteBuffer.allocateDirect(inputArray.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
		sBuf.put(inputArray).position(0);
		return sBuf;
	}
	
	public static IntBuffer arrayToIntBuffer(int[] inputArray) {
		IntBuffer iBuf = ByteBuffer.allocateDirect(inputArray.length * BYTES_PER_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
		iBuf.put(inputArray).position(0);
		return iBuf;
	}
	
	public static <T> T[] remove(T[] array, int index) {
        return remove(array, index, 1);
    }
	
	@SuppressWarnings("unchecked")
	public static <T> T[] remove(T[] array, int index, int stride) {
        int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        Object result = Array.newInstance(array.getClass().getComponentType(), length - stride);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + stride, result, index, length - index - stride);
        }

        return (T[])result;
    }
	
	public static int getLength(Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

}
