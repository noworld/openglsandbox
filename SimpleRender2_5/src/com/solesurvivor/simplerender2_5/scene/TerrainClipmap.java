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
	
	protected ClipBlock[] mRingBlocks;
	protected ClipBlock[] mFillBlocks;
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
		
		//Mips must be powers of 2 that give at least a 2x2
		mMipMults = new float[]{2.0f, 4.0f, 8.0f, 16.0f, 32.0f};
		mMipRng = new int[]{0,5};
		
		mRingBlocks = new ClipBlock[NUM_BLOCKS];
		mFillBlocks = new ClipBlock[NUM_FILL_BLOCKS];
		
		mBlockMat = new float[NUM_BLOCKS * MATRIX_SZ];
		mFillMat =  new float[NUM_FILL_BLOCKS * MATRIX_SZ];
		
		
		float nHalfSz = -(mSideLength/2.0f);
		float quadWidth = ((float)mSideLength) / (((float)mResolution) - 1.0f);
		float blockSz = (((float)mResolution) + 1.0f) / 4.0f;
		float blockDisp = quadWidth * (blockSz - 1.0f);
		
		//12 blocks arranged like so
		//http://http.developer.nvidia.com/GPUGems2/elementLinks/02_clipmaps_05.jpg
		
		//0
		int idx = 0;
		mRingBlocks[idx] = new ClipBlock(0, 0, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);

		//1
		idx++;
		mRingBlocks[idx] = new ClipBlock(1, 0, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
		
		//2
		idx++;
		mRingBlocks[idx] = new ClipBlock(2, 0, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);

		//3
		idx++;
		mRingBlocks[idx] = new ClipBlock(3, 0, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0,
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
		
		//4
		idx++;
		mRingBlocks[idx] = new ClipBlock(0, 1, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);

		//5
		idx++;
		mRingBlocks[idx] = new ClipBlock(3, 1, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
		
		//6
		idx++;
		mRingBlocks[idx] = new ClipBlock(0, 2, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
		
		//7
		idx++;
		mRingBlocks[idx] = new ClipBlock(3, 2, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
	
		//8
		idx++;
		mRingBlocks[idx] = new ClipBlock(0, 3, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
	
		//9
		idx++;
		mRingBlocks[idx] = new ClipBlock(1, 3, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
		
		//10
		idx++;
		mRingBlocks[idx] = new ClipBlock(2, 3, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
	
		//11	
		idx++;
		mRingBlocks[idx] = new ClipBlock(3, 3, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mRingBlocks[idx].mXOffset, mRingBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mBlockMat, mRingBlocks[idx].mMatrixOffset, MATRIX_SZ);
		
		//4 blocks to fill up the inside		
		//1
		idx = 0;
		mFillBlocks[idx] = new ClipBlock(1, 1, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mFillBlocks[idx].mXOffset, mFillBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mFillMat, mFillBlocks[idx].mMatrixOffset, MATRIX_SZ);

		//2
		idx++;
		mFillBlocks[idx] = new ClipBlock(2, 1, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mFillBlocks[idx].mXOffset, mFillBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mFillMat, mFillBlocks[idx].mMatrixOffset, MATRIX_SZ);

		//3
		idx++;
		mFillBlocks[idx] = new ClipBlock(1, 2, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mFillBlocks[idx].mXOffset, mFillBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mFillMat, mFillBlocks[idx].mMatrixOffset, MATRIX_SZ);

		//4
		idx++;
		mFillBlocks[idx] = new ClipBlock(2, 2, idx * MATRIX_SZ);
		System.arraycopy(getBlockMatrix(nHalfSz, mFillBlocks[idx].mXOffset, mFillBlocks[idx].mZOffset, blockDisp, quadWidth), 0, 
				mFillMat, mFillBlocks[idx].mMatrixOffset, MATRIX_SZ);

		
	}
	
	private float[] getBlockMatrix(float nHalfSz, float xIdx, float zIdx, float blockDisp, float quadWidth) {
		float[] mat = new float[16];
		Matrix.setIdentityM(mat, 0);
		Matrix.translateM(mat, 0, nHalfSz + (xIdx * blockDisp) + quadWidth, 0.0f, nHalfSz + (zIdx * blockDisp) + quadWidth);
		return mat;
	}
	
//	private float[] getBlockFillMatrix(float nHalfSz, float xIdx, float zIdx, float blockDisp, float quadWidth) {
//		float[] mat = new float[16];
//		float x = (xIdx == 1 ? -blockDisp : 0.0f);
//		float z = (zIdx == 1 ? -blockDisp : 0.0f);
//		Matrix.setIdentityM(mat, 0);
//		Matrix.translateM(mat, 0, x, 0.0f, z);
//		return mat;
//	}
	

	@Override
	public void update() {
		
	}

	@Override
	public void render() {
		for(int h = mMipRng[0]; h < mMipRng[1]; h++) {
			float mipScale = mMipMults[h];
			for(int i = 0; i < mRingBlocks.length; i ++) {
				float[] mat = ArrayUtils.subarray(mBlockMat, mRingBlocks[i].mMatrixOffset, mRingBlocks[i].mMatrixEnd);
				mRenderer.drawClipMap(mBlock, mat, mipScale, mRingBlocks[i].mXOffset, mRingBlocks[i].mZOffset);
			}

			if(h == mMipRng[0]) {
				for(int i = 0; i < mFillBlocks.length; i++) {
					float[] mat = ArrayUtils.subarray(mFillMat, mFillBlocks[i].mMatrixOffset, mFillBlocks[i].mMatrixEnd);
					mRenderer.drawClipMap(mBlock, mat, mipScale, mFillBlocks[i].mXOffset, mFillBlocks[i].mZOffset);
				}
			}
		}

	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}
	
	private static class ClipBlock {
		
		public ClipBlock(int mXOffset, int mZOffset, int mMatrixOffset) {
			this.mXOffset = mXOffset;
			this.mZOffset = mZOffset;
			this.mMatrixOffset = mMatrixOffset;
			this.mMatrixEnd = mMatrixOffset + MATRIX_SZ;
		}
		
		public int mXOffset;
		public int mZOffset;
		public int mMatrixOffset;
		public int mMatrixEnd;
	}
	
	public static class ClipmapData {
		public int mSideLength;
		public int mResolution;
	}
	
}
