package com.solesurvivor.simplerender;

import java.nio.FloatBuffer;

import android.graphics.Point;
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
	
	
	/* New - Draw fonts based on supplied measurements */
	
	public float mAtlasSzX = 1024.0f; //Font atlas size
	public float mAtlasSzY = 1024.0f;
	
//	public float mBBTop = 767.0f;    //Top Y of first glyph bounding box
//	public float mBBBottom = 702.0f; //Bottom Y of first glyph bounding box
//	public float mBBLeft = 2.0f;   //Left X of first glyph bounding box
//	public float mBBRight = 43.0f;  //Right X of first glyph bounding box
	
	public float mBBTop = 263.0f;    //Top Y of first glyph bounding box
	public float mBBBottom = 317.0f; //Bottom Y of first glyph bounding box
	public float mBBLeft = 11.0f;   //Left X of first glyph bounding box
	public float mBBRight = 38.0f;  //Right X of first glyph bounding box
	
	public Font() {
		Matrix.setIdentityM(mModelMatrix, 0);
		loadFont();
	}
	
	public void draw() {
		
	}
	
	private void loadFont() {
		
		float mTop = 1 - ((mAtlasSzY - mBBTop) / mAtlasSzY);  //Use top left coordinates, then Flip Y
		float mBottom = 1 - ((mAtlasSzY - mBBBottom) / mAtlasSzY); //Use top left coordinates, then Flip Y
		float mLeft = mBBLeft / mAtlasSzX;
		float mRight = mBBRight / mAtlasSzX;
		
		mIdx[0] = 0;
		
		mPosNrm[0] = -2.5f;
		mPosNrm[1] = -4.5f;
		mPosNrm[2] = 0.0f;
		
		mPosNrm[3] = 0.0f;
		mPosNrm[4] = 0.0f;
		mPosNrm[5] = 1.0f;

//		Bottom Left Coordinate
//		mTxc[0] = 0.01171875f;
//		mTxc[1] = 0.31482393f;
		mTxc[0] = mLeft;
		mTxc[1] = mBottom;
		
		mIdx[1] = 1;
		
		mPosNrm[6] = 2.5f;
		mPosNrm[7] = -4.5f;
		mPosNrm[8] = 0.0f;

		mPosNrm[9] = 0.0f;
		mPosNrm[10] = 0.0f;
		mPosNrm[11] = 1.0f;

//		Bottom Right
//		mTxc[2] = 0.04236412f;
//		mTxc[3] = 0.3148241f;
		mTxc[2] = mRight;
		mTxc[3] = mBottom;
		
		mIdx[2] = 2;
		
		mPosNrm[12] = 2.5f;
		mPosNrm[13] = 4.5f;
		mPosNrm[14] = 0.0f;

		mPosNrm[15] = 0.0f;
		mPosNrm[16] = 0.0f;
		mPosNrm[17] = 1.0f;

//		Top Right
//		mTxc[4] = 0.04236418f;
//		mTxc[5] = 0.2507475f;
		mTxc[4] = mRight;
		mTxc[5] = mTop;

		mIdx[3] = 3;
		
		mPosNrm[18] = -2.5f;
		mPosNrm[19] = 4.5f;
		mPosNrm[20] = 0.0f;

		mPosNrm[21] = 0.0f;
		mPosNrm[22] = 0.0f;
		mPosNrm[23] = 1.0f;

//		Top Left
//		mTxc[6] = 0.001887559f;
//		mTxc[7] = 0.25074738f;
		mTxc[6] = mLeft;
		mTxc[7] = mTop;

		mIdx[4] = 4;
		
		mPosNrm[24] = -2.5f;
		mPosNrm[25] = -4.5f;
		mPosNrm[26] = 0.0f;

		mPosNrm[27] = 0.0f;
		mPosNrm[28] = 0.0f;
		mPosNrm[29] = 1.0f;

//		Bottom Left
//		mTxc[8] = 0.00188744f;
//		mTxc[9] = 0.31482393f;
		mTxc[8] = mLeft;
		mTxc[9] = mBottom;

		mIdx[5] = 5;
		
		mPosNrm[30] = 2.5f;
		mPosNrm[31] = 4.5f;
		mPosNrm[32] = 0.0f;

		mPosNrm[33] = 0.0f;
		mPosNrm[34] = 0.0f;
		mPosNrm[35] = 1.0f;

//		Top Right
//		mTxc[10] = 0.04236418f;
//		mTxc[11] = 0.2507475f;
		mTxc[10] = mRight;
		mTxc[11] = mTop;
		
	}
	
}
