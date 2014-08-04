package com.solesurvivor.scene.builder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.solesurvivor.scene.Scene;

public abstract class SceneBuilder {
	
	public abstract Scene buildScene();
	
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
	
	public static float[] stringArrayToFloatArray(String[] strings) {
		float[] floats = new float[strings.length];
		
		for(int i = 0; i < floats.length; i++) {
			floats[i] = Float.parseFloat(strings[i]);
		}
		
		return floats;
	}
}
