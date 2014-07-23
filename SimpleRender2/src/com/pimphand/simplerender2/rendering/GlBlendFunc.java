package com.pimphand.simplerender2.rendering;

import android.opengl.GLES20;

public class GlBlendFunc {

	private int mSource;
	private int mDest;
	
	public GlBlendFunc() {
		mSource = GLES20.GL_SRC_ALPHA;
		mDest = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	}
	
	public GlBlendFunc(int source, int dest) {
		this.mSource = source;
		this.mDest = dest;
	}

	public int getSource() {
		return mSource;
	}

	public void setSource(int source) {
		this.mSource = source;
	}

	public int getDest() {
		return mDest;
	}

	public void setDest(int dest) {
		this.mDest = dest;
	}
	
	
}
