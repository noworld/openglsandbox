package com.solesurvivor.scene;

import java.nio.FloatBuffer;

public class BufferedGeometry extends PackedGeometry {
	
	public FloatBuffer mPositions;
	public FloatBuffer mNormals;
	public FloatBuffer mColors;
	public FloatBuffer mTexCoords;
	
	public float[] mPosArray;
	public float[] mNrmArray;
	public float[] mColArray;
	public float[] mTxcArray;
	
	public int mPosStride;
	public int mNrmStride;
	public int mColStride;
	public int mTxcStride;
	
	public int mNumElements;
	
	public String mShaderProgramName;
	public String mTextureName;

	@Override
	public void update() {
		super.update();
	}
	
}
