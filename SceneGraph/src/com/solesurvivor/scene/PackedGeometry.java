package com.solesurvivor.scene;

import android.opengl.GLES20;

import com.solesurvivor.drawing.ShaderProgram;

public class PackedGeometry extends Point {
	
	public int mPolyType = GLES20.GL_TRIANGLES;
	
	public int mPositionsSize;
	public int mColorsSize;
	public int mNormalsSize;
	public int mTexcoordsSize;
	
	public int mNumElements;
	public int mNumPolys;
	
	public int mDataHandle = -1;
	public int mIndexesHandle = -1;
	public int mPositionsOffset = 0;
	public int mNormalsOffset = -1;
	public int mTexcoordsOffset = -1;
	public int mColorsOffset = -1;
	
	public int mTextureHandle = -1;
	
	public ShaderProgram mShaderProgram;

	@Override
	public void update() {
		super.update();
	}
	
}
