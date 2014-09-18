package com.solesurvivor.simplerender2_5.scene;

import org.apache.commons.lang.ArrayUtils;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;


public class TerrainClipmap implements Node {
	
	protected static final int MATRIX_SZ = 16;
	protected static final int NUM_BLOCKS = 12;
	protected static final int NUM_RING_FILL = 1; //4;
	protected static final int NUM_INTERIOR_TRIM = 1;
	
	@SuppressWarnings("unused")
	private static final String TAG = TerrainClipmap.class.getSimpleName();
	
	protected Geometry mBlock;
	protected Geometry mRingFill;
	protected Geometry mInteriorTrim;
	
	protected float[] mBlockMat;
	protected float[] mRingMat;
	protected float[] mInteriorMat;
	
	protected int mSideLength;
	protected int mResolution;
	
	protected BaseRenderer mRenderer;
	
	protected float angle = 0.0f;
	
	
	public TerrainClipmap(Geometry clipmap, ClipmapData data) {
		
		mRenderer = RendererManager.getRenderer();
		
		mBlock = new Geometry(clipmap);
		mRingFill = new Geometry(clipmap);
		mInteriorTrim = new Geometry(clipmap);
		
		mRingFill.setElementOffset(data.mRingFillIndex);
		mRingFill.setNumElements(data.mRingFillNumElements);
		
		mInteriorTrim.setElementOffset(data.mInteriorTrimIndex);
		mInteriorTrim.setNumElements(data.mInteriorTrimNumElements);
		
		
		mSideLength = data.mSideLength;
		mResolution = data.mResolution;
		
		mBlockMat = new float[NUM_BLOCKS * MATRIX_SZ];
		mRingMat = new float[NUM_RING_FILL * MATRIX_SZ];
		mInteriorMat = new float[NUM_INTERIOR_TRIM * MATRIX_SZ];
		
		float nHalfSz = -(mSideLength/2.0f);
		float quadWidth = ((float)mSideLength) / (((float)mResolution) - 1.0f);
		float blockSz = (((float)mResolution) + 1.0f) / 4.0f;
		float blockDisp = quadWidth * (blockSz - 1.0f);
		
		//12 blocks arranged like so
		//http://http.developer.nvidia.com/GPUGems2/elementLinks/02_clipmaps_05.jpg
		int idx = 0;
		int xIdx = 0;
		int zIdx = 0;
		
		//1
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx++;
		
		//2
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx++;
		
		//3
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx++;
		
		//4
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx = 0;
		zIdx++;

		//5
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx += 3;

		//6
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx = 0;
		zIdx++;

		//7
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx += 3;

		//8
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx = 0;
		zIdx++;

		//9
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx++;

		//10
	
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx++;

		//11	
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		xIdx++;

		//12
		System.arraycopy(getBlockMatrix(nHalfSz, xIdx, zIdx, blockDisp, quadWidth), 0, mBlockMat, idx++ * MATRIX_SZ, MATRIX_SZ);
		
		//Four ring fills
		//1
		idx = 0;
		Matrix.setIdentityM(mRingMat, idx * MATRIX_SZ);
//		Matrix.translateM(mRingMat, idx++ * MATRIX_SZ, nHalfSz + (blockDisp * 2.0f), 0.0f, nHalfSz);
		
		//2
//		Matrix.setIdentityM(mRingMat, idx * MATRIX_SZ);		
//		Matrix.translateM(mRingMat, idx * MATRIX_SZ, 0.0f, 0.0f, 0.0f);
//		Matrix.rotateM(mRingMat, idx++ * MATRIX_SZ, 90, 0.0f, 1.0f, 0.0f);
		
		
		//3
//		Matrix.setIdentityM(mRingMat, idx * MATRIX_SZ);		
//		Matrix.translateM(mRingMat, idx * MATRIX_SZ, nHalfSz + (blockDisp * 2.0f), 0.0f, nHalfSz);
//		Matrix.rotateM(mRingMat, idx++ * MATRIX_SZ, 180, 0.0f, 1.0f, 0.0f);
		
		//4
//		Matrix.setIdentityM(mRingMat, idx * MATRIX_SZ);		
//		Matrix.translateM(mRingMat, idx * MATRIX_SZ, nHalfSz + (blockDisp * 2.0f), 0.0f, nHalfSz);
//		Matrix.rotateM(mRingMat, idx * MATRIX_SZ, 270, 0.0f, 1.0f, 0.0f);
		
		
		for(int i = 0; i < mInteriorMat.length; i += MATRIX_SZ) {
			Matrix.setIdentityM(mInteriorMat, i);
		}
	}
	
	private float[] getBlockMatrix(float nHalfSz, float xIdx, float zIdx, float blockDisp, float quadWidth) {
		float centerDispX = xIdx > 1 ? 2.0f : 0.0f;
		float centerDispZ = zIdx > 1 ? 2.0f : 0.0f;
		float[] mat = new float[16];
		Matrix.setIdentityM(mat, 0);
		Matrix.translateM(mat, 0, nHalfSz + (xIdx * blockDisp) + (quadWidth * centerDispX), 0.0f, nHalfSz + (zIdx * blockDisp) + (quadWidth * centerDispZ));
		return mat;
	}
	

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		for(int i = 0; i < mBlockMat.length; i += MATRIX_SZ) {
			mRenderer.drawGeometryTristrips(mBlock, ArrayUtils.subarray(mBlockMat, i, i + MATRIX_SZ));
		}
		
		for(int i = 0; i < mRingMat.length; i += MATRIX_SZ) {
			mRenderer.drawGeometryTristrips(mRingFill, ArrayUtils.subarray(mRingMat, i, i + MATRIX_SZ));
		}

	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}
	
	public static class ClipmapData {
		public int mBlockIndex;
		public int mRingFillIndex;
		public int mRingFillNumElements;
		public int mInteriorTrimIndex;
		public int mInteriorTrimNumElements;
		public int mSideLength;
		public int mResolution;
	}
	
}
