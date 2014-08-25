package com.solesurvivor.simplerender2_5.scene;

import android.opengl.Matrix;

public class Geometry implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = Geometry.class.getSimpleName();
	
	public float[] mModelMatrix;
	public int mShaderHandle;
	public int mTextureHandle;
	
	public Geometry() {
		this.mModelMatrix = new float[16];
		Matrix.setIdentityM(this.mModelMatrix, 0);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}
}
