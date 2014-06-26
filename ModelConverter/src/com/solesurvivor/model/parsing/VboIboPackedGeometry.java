package com.solesurvivor.model.parsing;

public class VboIboPackedGeometry {
	
	public String mName;

	/*For drawing with OpenGL*/
	public float[] mData;
	public short[] mIndexes;
	
	public int mPosSize;
	public int mNrmSize;
	public int mTxcSize;
	
	public int mPosOffset;
	public int mNrmOffset;
	public int mTxcOffset;
	
	/*For parsing from Collada*/
	public int mNumElements;
	public int mNumPolys;
	
}
