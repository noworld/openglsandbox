package com.solesurvivor.simplerender.ui;


public abstract class UiElement {

	public String mName;
	
	public int mDatBufIndex;
	public int mIdxBufIndex; 
	
	public int mPosSize;
	public int mNrmSize;
	public int mTxcSize;
	
	public int mPosOffset;
	public int mNrmOffset;
	public int mTxcOffset;
	
	public float[] mModelMatrix = new float[16];
	public int mTextureHandle;
	public int mShaderHandle;
	
	public float[] mPosition = new float[]{0.0f, 0.0f,-1.0f};
	public float[] mScale = new float[]{1.0f, 1.0f, 1.0f};
	
}
