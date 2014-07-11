package com.solesurvivor.model.parsing;

public class VboIboPackedGeometry {
	
	public String mName;

	/*For drawing with OpenGL*/
	public float[] mData;
	public short[] mIndexes;
	
	public int mStride; 
	public int mTotalStride; //stride multiplied by data size
	
	public int mPosSize;
	public int mNrmSize;
	public int mTxcSize;
	
	public int mPosOffset;
	public int mNrmOffset;
	public int mTxcOffset;
	
	/*For parsing from Collada*/
	public int mNumElements;
	public int mNumPolys;
	
	/*For other drawing purposes*/
	public float mXMin = Float.MAX_VALUE;
	public float mYMin = Float.MAX_VALUE;
	public float mZMin = Float.MAX_VALUE;
	
	public float mXMax = Float.MIN_VALUE;
	public float mYMax = Float.MIN_VALUE;
	public float mZMax = Float.MIN_VALUE;
	
}
