package com.solesurvivor.simplerender2.rendering;

import android.opengl.Matrix;


public class Geometry {
	
	public String mName;
	public String mAssetXml;

	public int mDatBufIndex;
	public int mIdxBufIndex; 
	
	public int mPosSize;
	public int mNrmSize;
	public int mTxcSize;
	
	public int mPosOffset;
	public int mNrmOffset;
	public int mTxcOffset;
	
	public int mNumElements;
	public int mElementStride;
	
	public float[] mModelMatrix = new float[16];
	public float[] mCombinedMatrix = new float[16];
	public boolean mDirty = true;
	
	public int mTextureHandle;
	public int mShaderHandle;
	public float mAccumScale = 1.0f;
	public float mAccumRot = 0.0f;
	
	public Geometry() {
		Matrix.setIdentityM(this.mModelMatrix, 0);
	}
	
}
