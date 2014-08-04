package com.solesurvivor.scene.builder;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

import com.solesurvivor.drawing.DrawingConstants;
import com.solesurvivor.scene.PackedGeometry;

public class PackedGeometryBuilder implements DrawingConstants {
	
	private static final String TAG = PackedGeometryBuilder.class.getSimpleName();

	public static PackedGeometry build(GeometryData data) {
		PackedGeometry pg = new PackedGeometry();
		
		float[] packed = new float[data.mPosData.length + data.mNrmData.length + data.mTxcData.length];
		
		System.arraycopy(data.mPosData, 0, packed, 0, data.mPosData.length);
		System.arraycopy(data.mNrmData, 0, packed, data.mPosData.length, data.mNrmData.length);
		System.arraycopy(data.mTxcData, 0, packed, data.mPosData.length + data.mNrmData.length, data.mTxcData.length);
				
		FloatBuffer dataBuffer = SceneBuilder.arrayToFloatBuffer(BYTES_PER_FLOAT, packed);
		IntBuffer indexBuffer = SceneBuilder.arrayToIntBuffer(BYTES_PER_INT, data.mPrimData);
		
		final int buffers[] = new int[2];
		GLES20.glGenBuffers(2, buffers, 0);	
		
		if(buffers[0] == 0 || buffers[1] == 0) {
			Log.w(TAG, String.format("Could not generate buffers for geometry %s.", data.mName));
		}
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, dataBuffer.capacity() * BYTES_PER_FLOAT, dataBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * BYTES_PER_INT, indexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		pg.mPositionsSize = POSITION_DATA_SIZE;
		pg.mNormalsSize = NORMAL_DATA_SIZE;
		pg.mTexcoordsSize = TEXCOORD_DATA_SIZE;
		
		pg.mNumElements = data.mVCount.length * TRIANGLE_VERTICES;
		
		pg.mDataHandle = buffers[0];
		pg.mIndexesHandle = buffers[1];
		pg.mNormalsOffset = data.mPosData.length;
		pg.mTexcoordsOffset = data.mPosData.length + data.mNrmData.length;
		
		return pg;
	}
	
	public static class GeometryData {
		public String mName;
		public float[] mPosData;
		public float[] mNrmData;
		public float[] mTxcData;
		public int[] mPrimData; //p
		public int[] mVCount;
		public int mNumPolys;
		
	}

}