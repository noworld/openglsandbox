package com.solesurvivor.simplerender;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import com.solesurvivor.util.SSArrayUtil;

import android.opengl.Matrix;


public class Font {
		

	public String mName = "praeFont";
	public float[] mModelMatrix = new float[16];
	
	
	public float[] mPosNrm = new float[36];	
//	public float[] mTxc = new float[12];	
	public short[] mIdx = new short[6];
	
	public int mShaderHandle = 0;
	public int mTextureHandle = 0;
	public int mPosNrmBufIndex = 0;
//	public int mTxcBufIndex = 0;
	public int mIdxBufIndex = 0;
	public int mElementStride = 24;
	public int mPosOffset = 0;
	public int mNrmOffset = 12;
//	public FloatBuffer mTxcBuffer = null;
	public int mNumElements = 6; 
	
	public int mPosSize = 3;
	public int mNrmSize = 3;
	public int mTxcSize = 2;
	
	
	/* New - Draw fonts based on supplied measurements */
	
	//Characters supported on the atlas
//	public String mChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()<>?/{}[]\\;':\"`~_+-=";
	public String mChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!";
	
	public float mAtlasSzX = 1024.0f; //Font atlas size
	public float mAtlasSzY = 1024.0f;
	
//	public float mBBTop = 767.0f;    //Top Y of first glyph bounding box
//	public float mBBBottom = 702.0f; //Bottom Y of first glyph bounding box
//	public float mBBLeft = 2.0f;   //Left X of first glyph bounding box
//	public float mBBRight = 43.0f;  //Right X of first glyph bounding box
	
	public float mBBTop = 15.0f;    //Top Y of first glyph bounding box
	public float mBBBottom = 42.0f; //Bottom Y of first glyph bounding box
	public float mBBLeft = 9.0f;   //Left X of first glyph bounding box
	public float mBBRight = 23.0f;  //Right X of first glyph bounding box
	
	public float mStride = 19.0f; //X Distance between characters
	
	public Map<Character,FloatBuffer> mGlyphs = new HashMap<Character,FloatBuffer>(mChars.length());
	
//	private float mTop = 1 - ((mAtlasSzY - mBBTop) / mAtlasSzY);  //Use top left coordinates, then Flip Y
//	private float mBottom = 1 - ((mAtlasSzY - mBBBottom) / mAtlasSzY); //Use top left coordinates, then Flip Y
//	private float mLeft = mBBLeft / mAtlasSzX;
//	private float mRight = mBBRight / mAtlasSzX;
			
	public Font() {
		Matrix.setIdentityM(mModelMatrix, 0);
		
		for(int i = 0; i < mChars.length(); i++) {
			char c = mChars.charAt(i);
			mGlyphs.put(c, loadTxc(i));
		}
		
		loadFont();
	}
	
	public FloatBuffer getCoords(char c) {
		return mGlyphs.get(c);
	}
	
	private void loadFont() {
		
		for(int i = 0; i < mIdx.length; i++) {
			mIdx[i] = (short)i;
		}
		
//		Bottom Left Vertex
		//Pos
		mPosNrm[0] = -2.5f;
		mPosNrm[1] = -4.5f;
		mPosNrm[2] = 0.0f;
		
		//Nrm
		mPosNrm[3] = 0.0f;
		mPosNrm[4] = 0.0f;
		mPosNrm[5] = 1.0f;
		
//		Bottom Right
		mPosNrm[6] = 2.5f;
		mPosNrm[7] = -4.5f;
		mPosNrm[8] = 0.0f;

		mPosNrm[9] = 0.0f;
		mPosNrm[10] = 0.0f;
		mPosNrm[11] = 1.0f;
	
//		Top Right
		mPosNrm[12] = 2.5f;
		mPosNrm[13] = 4.5f;
		mPosNrm[14] = 0.0f;

		mPosNrm[15] = 0.0f;
		mPosNrm[16] = 0.0f;
		mPosNrm[17] = 1.0f;
		
//		Top Left
		mPosNrm[18] = -2.5f;
		mPosNrm[19] = 4.5f;
		mPosNrm[20] = 0.0f;

		mPosNrm[21] = 0.0f;
		mPosNrm[22] = 0.0f;
		mPosNrm[23] = 1.0f;
		
//		Bottom Left
		mPosNrm[24] = -2.5f;
		mPosNrm[25] = -4.5f;
		mPosNrm[26] = 0.0f;

		mPosNrm[27] = 0.0f;
		mPosNrm[28] = 0.0f;
		mPosNrm[29] = 1.0f;
		
//		Top Right
		mPosNrm[30] = 2.5f;
		mPosNrm[31] = 4.5f;
		mPosNrm[32] = 0.0f;

		mPosNrm[33] = 0.0f;
		mPosNrm[34] = 0.0f;
		mPosNrm[35] = 1.0f;
		
	}
	
	private FloatBuffer loadTxc(int index) {
		
		float totalStride = mStride * index;
		float top = 1 - ((mAtlasSzY - (mBBTop)) / mAtlasSzY);  //Use top left coordinates, then Flip Y
		float bottom = 1 - ((mAtlasSzY - (mBBBottom)) / mAtlasSzY); //Use top left coordinates, then Flip Y
		float left = (mBBLeft + totalStride) / mAtlasSzX;
		float right = (mBBRight + totalStride) / mAtlasSzX;
		
		float[] txc = new float[12];
		
//		Bottom Left Coordinate
		txc[0] = left;
		txc[1] = bottom;
		
//		Bottom Right
		txc[2] = right;
		txc[3] = bottom;
		
//		Top Right
		txc[4] = right;
		txc[5] = top;
		
//		Top Left
		txc[6] = left;
		txc[7] = top;
		
//		Bottom Left
		txc[8] = left;
		txc[9] = bottom;
		
//		Top Right
		txc[10] = right;
		txc[11] = top;
		
		return SSArrayUtil.arrayToFloatBuffer(txc);
		
	}
	
}
