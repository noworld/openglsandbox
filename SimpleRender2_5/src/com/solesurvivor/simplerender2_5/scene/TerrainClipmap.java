package com.solesurvivor.simplerender2_5.scene;

import org.apache.commons.lang.ArrayUtils;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;


public class TerrainClipmap implements Node {
	
	protected static final int MATRIX_SZ = 16;
	protected static final int NUM_BLOCKS = 1; //12;
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
	
	
	public TerrainClipmap(Geometry clipmap, ClipmapData data) {
		
		mRenderer = RendererManager.getRenderer();
		
		mBlock = new Geometry(clipmap);
		mRingFill = new Geometry(clipmap);
		mInteriorTrim = new Geometry(clipmap);
		
		mRingFill.setPosOffset(data.mRingFillIndex);
		mRingFill.setTxcOffset(mRingFill.getTxcOffset() + data.mRingFillIndex);
		mRingFill.setNumElements(data.mRingFillNumElements);
		
		mInteriorTrim.setPosOffset(data.mInteriorTrimIndex);
		mInteriorTrim.setTxcOffset(mInteriorTrim.getTxcOffset() + data.mInteriorTrimIndex);
		mInteriorTrim.setNumElements(data.mInteriorTrimNumElements);
		
		mSideLength = data.mSideLength;
		mResolution = data.mResolution;
		
		mBlockMat = new float[NUM_BLOCKS * MATRIX_SZ];
		mRingMat = new float[NUM_RING_FILL * MATRIX_SZ];
		mInteriorMat = new float[NUM_INTERIOR_TRIM * MATRIX_SZ];
		
		float n_half_sz = -(mSideLength/2.0f);
		
		for(int i = 0; i < mBlockMat.length; i += MATRIX_SZ) {
			Matrix.setIdentityM(mBlockMat, i);
			Matrix.translateM(mBlockMat, i, n_half_sz, 0.0f, n_half_sz);
		}
		
		for(int i = 0; i < mRingMat.length; i += MATRIX_SZ) {
			Matrix.setIdentityM(mRingMat, i);
		}
		
		for(int i = 0; i < mInteriorMat.length; i += MATRIX_SZ) {
			Matrix.setIdentityM(mInteriorMat, i);
		}
	}
	

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		for(int i = 0; i < mBlockMat.length; i += MATRIX_SZ) {
			mRenderer.drawGeometryTristrips(mBlock, ArrayUtils.subarray(mBlockMat, i, i + MATRIX_SZ));
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
