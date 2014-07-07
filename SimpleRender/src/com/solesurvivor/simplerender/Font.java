package com.solesurvivor.simplerender;

import java.nio.FloatBuffer;

import android.opengl.Matrix;


public class Font {
		

	public String mName = "praeFont";
	public float[] mModelMatrix = new float[16];
	
	
	public float[] mPosNrm = new float[36];	
	public float[] mTxc = new float[12];	
	public short[] mIdx = new short[6];
	
	public int mShaderHandle = 0;
	public int mTextureHandle = 0;
	public int mPosNrmBufIndex = 0;
	public int mTxcBufIndex = 0;
	public int mIdxBufIndex = 0;
	public int mElementStride = 24;
	public int mPosOffset = 0;
	public int mNrmOffset = 12;
	public FloatBuffer mTxcBuffer = null;
	public int mNumElements = 6; 
	
	public int mPosSize = 3;
	public int mNrmSize = 3;
	public int mTxcSize = 2;
	
	public Font() {
		Matrix.setIdentityM(mModelMatrix, 0);
		loadFont();
	}
	
	public void draw() {
		
	}
	
	private void loadFont() {
		
		mIdx[0] = 0;
		
		mPosNrm[0] = -2.5f;
		mPosNrm[1] = -4.5f;
		mPosNrm[2] = 0.0f;
		
		mPosNrm[3] = 0.0f;
		mPosNrm[4] = 0.0f;
		mPosNrm[5] = 1.0f;

		mTxc[0] = 0.00188744f;
		mTxc[1] = 0.31482393f;
		
		mIdx[1] = 1;
		
		mPosNrm[6] = 2.5f;
		mPosNrm[7] = -4.5f;
		mPosNrm[8] = 0.0f;

		mPosNrm[9] = 0.0f;
		mPosNrm[10] = 0.0f;
		mPosNrm[11] = 1.0f;

		mTxc[2] = 0.04236412f;
		mTxc[3] = 0.3148241f;

		mIdx[2] = 2;
		
		mPosNrm[12] = 2.5f;
		mPosNrm[13] = 4.5f;
		mPosNrm[14] = 0.0f;

		mPosNrm[15] = 0.0f;
		mPosNrm[16] = 0.0f;
		mPosNrm[17] = 1.0f;

		mTxc[4] = 0.04236418f;
		mTxc[5] = 0.2507475f;

		mIdx[3] = 3;
		
		mPosNrm[18] = -2.5f;
		mPosNrm[19] = 4.5f;
		mPosNrm[20] = 0.0f;

		mPosNrm[21] = 0.0f;
		mPosNrm[22] = 0.0f;
		mPosNrm[23] = 1.0f;

		
		mTxc[6] = 0.001887559f;
		mTxc[7] = 0.25074738f;

		mIdx[4] = 4;
		
		mPosNrm[24] = -2.5f;
		mPosNrm[25] = -4.5f;
		mPosNrm[26] = 0.0f;

		mPosNrm[27] = 0.0f;
		mPosNrm[28] = 0.0f;
		mPosNrm[29] = 1.0f;

		mTxc[8] = 0.00188744f;
		mTxc[9] = 0.31482393f;

		mIdx[5] = 5;
		
		mPosNrm[30] = 2.5f;
		mPosNrm[31] = 4.5f;
		mPosNrm[32] = 0.0f;

		mPosNrm[33] = 0.0f;
		mPosNrm[34] = 0.0f;
		mPosNrm[35] = 1.0f;

		mTxc[10] = 0.04236418f;
		mTxc[11] = 0.2507475f;
		
	}
	
}
