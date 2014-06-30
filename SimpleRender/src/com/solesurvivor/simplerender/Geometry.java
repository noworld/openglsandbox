package com.solesurvivor.simplerender;

import com.solesurvivor.simplerender.animui.HAlignType;
import com.solesurvivor.simplerender.animui.VAlignType;

public class Geometry {
	
	public String mName;
	public String mAssetXml;

	/*For drawing with OpenGL*/
	public int mDatBufIndex;
	public int mIdxBufIndex; 
	
	public int mPosSize;
	public int mNrmSize;
	public int mTxcSize;
	
	public int mPosOffset;
	public int mNrmOffset;
	public int mTxcOffset;
	
	/*For parsing from Collada*/
	public int mNumElements;
	public int mElementStride;
	
	/* New - for drawing multiple models*/
	public float[] mModelMatrix = new float[16];
	public int mTextureHandle;
	public int mShaderHandle;
	public float mAccumScale = 1.0f;
	public float mAccumRot = 0.0f;
	
	/* New - for drawing ui elements*/
	public float mXSize;
	public float mYSize;
	public float mZSize;
	
	public HAlignType mHAlign;
	public VAlignType mVAlign;
	
}
