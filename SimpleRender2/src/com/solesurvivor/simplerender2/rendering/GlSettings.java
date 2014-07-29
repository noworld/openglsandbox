package com.solesurvivor.simplerender2.rendering;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;

public class GlSettings {

	private float[] mGlClearColor = {0.2f, 0.4f, 0.6f, 1.0f};
	private GlBlendFunc mBlendFunc;
	private List<GlOption> mOptions;
	
	public GlSettings() {
		this.mOptions = new ArrayList<GlOption>();
		
		GlOption glCullFace = new GlOption(GLES20.GL_CULL_FACE);
		GlOption glDepthTest = new GlOption(GLES20.GL_DEPTH_TEST);
		GlOption glBlend = new GlOption(GLES20.GL_BLEND);
		
		// Use culling to remove back faces.
		this.mOptions.add(glCullFace);
		// Enable Z-buffer
		this.mOptions.add(glDepthTest);
		//Enable alpha channels
		this.mOptions.add(glBlend);
		
		this.mBlendFunc = new GlBlendFunc();
	}

	public float[] getGlClearColor() {
		return mGlClearColor;
	}

	public void setGlClearColor(float[] mGlClearColor) {
		this.mGlClearColor = mGlClearColor;
	}

	public GlBlendFunc getBlendFunc() {
		return mBlendFunc;
	}

	public void setBlendFunc(GlBlendFunc mBlendFunc) {
		this.mBlendFunc = mBlendFunc;
	}

	public List<GlOption> getOptions() {
		return mOptions;
	}

	public void setOptions(List<GlOption> options) {
		this.mOptions = options;
	}
}
