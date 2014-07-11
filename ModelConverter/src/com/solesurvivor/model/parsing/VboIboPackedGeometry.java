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
	public float mXMin = Float.POSITIVE_INFINITY;
	public float mYMin = Float.POSITIVE_INFINITY;
	public float mZMin = Float.POSITIVE_INFINITY;
	
	public float mXMax = Float.NEGATIVE_INFINITY;
	public float mYMax = Float.NEGATIVE_INFINITY;
	public float mZMax = Float.NEGATIVE_INFINITY;
	
}
