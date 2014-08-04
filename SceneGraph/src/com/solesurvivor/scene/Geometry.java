package com.solesurvivor.scene;

import com.solesurvivor.drawing.ShaderProgram;

public class Geometry extends Point {
	
	public int  mPositionsStride;
	public int mColorsStride;
	public int mNormalsStride;
	public int mTexcoordsStride;
	
	public int mNumPolys;
	
	public int mPositionsHandle = -1;
	public int mNormalsHandle = -1;
	public int mTexcoordsHandle = -1;
	public int mColorsHandle = -1;
	public int mTextureHandle = -1;
	
	public ShaderProgram mShaderProgram;

	@Override
	public void update() {
		super.update();
	}
	
}
