package com.solesurvivor.simplerender2_5.scene;

import org.apache.commons.lang.ArrayUtils;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;


public class TerrainClipmap implements Node {
	
	protected static final int MATRIX_SZ = 16;
	protected static final int NUM_BLOCKS = 12;
	protected static final int NUM_FILL_BLOCKS = 4;
	
	@SuppressWarnings("unused")
	private static final String TAG = TerrainClipmap.class.getSimpleName();
	
	protected Geometry mBlock;
	
	protected float[] mBlockMat;
	protected float[] mFillMat;	
	
	protected int mSideLength;
	protected int mResolution;
	protected float[] mMipMults;
	protected int[] mMipRng;
	
	protected BaseRenderer mRenderer;
	
	protected float angle = 0.0f;
	
	
	public TerrainClipmap(Geometry clipmap, ClipmapData data) {
		
		mRenderer = RendererManager.getRenderer();
		
		mBlock = new Geometry(clipmap);
		
		mSideLength = data.mSideLength;
		mResolution = data.mResolution;
		
		mMipMults = new float[]{1.0f, 2.0f, 4.0f, 8.0f, 16.0f, 32.0f};
		mMipRng = new int[]{0,5};
		
		mBlockMat = new float[NUM_BLOCKS * MATRIX_SZ];
		mFillMat =  new float[NUM_FILL_BLOCKS * MATRIX_SZ];
		
		
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
		
		//4 blocks to fill up the inside
		idx = 0;
		//1
		System.arraycopy(getBlockFillMatrix(nHalfSz, 0, 0, blockDisp, quadWidth), 0, mFillMat, idx++ * MATRIX_SZ, MATRIX_SZ);

		//2
		System.arraycopy(getBlockFillMatrix(nHalfSz, 1, 0, blockDisp, quadWidth), 0, mFillMat, idx++ * MATRIX_SZ, MATRIX_SZ);

		//3
		System.arraycopy(getBlockFillMatrix(nHalfSz, 0, 1, blockDisp, quadWidth), 0, mFillMat, idx++ * MATRIX_SZ, MATRIX_SZ);

		//4
		System.arraycopy(getBlockFillMatrix(nHalfSz, 1, 1, blockDisp, quadWidth), 0, mFillMat, idx++ * MATRIX_SZ, MATRIX_SZ);

		
	}
	
	private float[] getBlockMatrix(float nHalfSz, float xIdx, float zIdx, float blockDisp, float quadWidth) {
		float[] mat = new float[16];
		Matrix.setIdentityM(mat, 0);
		Matrix.translateM(mat, 0, nHalfSz + (xIdx * blockDisp) + quadWidth, 0.0f, nHalfSz + (zIdx * blockDisp) + quadWidth);
		return mat;
	}
	
	private float[] getBlockFillMatrix(float nHalfSz, float xIdx, float zIdx, float blockDisp, float quadWidth) {
		float[] mat = new float[16];
		float x = (xIdx == 0 ? -blockDisp : 0.0f);
		float z = (zIdx == 0 ? -blockDisp : 0.0f);
		Matrix.setIdentityM(mat, 0);
		Matrix.translateM(mat, 0, x, 0.0f, z);
		return mat;
	}
	

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		for(int h = mMipRng[0]; h < mMipRng[1]; h++) {
			float mipScale = mMipMults[h];
			for(int i = 0; i < mBlockMat.length; i += MATRIX_SZ) {
				mRenderer.drawClipMap(mBlock, ArrayUtils.subarray(mBlockMat, i, i + MATRIX_SZ), mipScale);
			}

			if(h == mMipRng[0]) {
				for(int i = 0; i < mFillMat.length; i += MATRIX_SZ) {
					mRenderer.drawClipMap(mBlock, ArrayUtils.subarray(mFillMat, i, i + MATRIX_SZ), mipScale);
				}
			}
		}
	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}
	
	public static class ClipmapData {
		public int mSideLength;
		public int mResolution;
	}
	
}
