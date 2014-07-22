package com.pimphand.simplerender2.rendering;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;

public class GlSettings {

	private float[] mGlClearColor = {0.2f, 0.4f, 0.6f, 1.0f};
	private GlBlendFunc mBlendFunc;
	private List<GlOption> mEnabledOptions;
	
	public GlSettings() {
		this.mEnabledOptions = new ArrayList<GlOption>();
		
		GlOption glCullFace = new GlOption(GLES20.GL_CULL_FACE);
		GlOption glDepthTest = new GlOption(GLES20.GL_DEPTH_TEST);
		GlOption glBlend = new GlOption(GLES20.GL_BLEND);
		
		// Use culling to remove back faces.
		this.mEnabledOptions.add(glCullFace);
		// Enable Z-buffer
		this.mEnabledOptions.add(glDepthTest);
		//Enable alpha channels
		this.mEnabledOptions.add(glBlend);
		
		this.mBlendFunc = new GlBlendFunc();
	}
	
	public void setClearColor(float[] color) {
		this.mGlClearColor = color;
	}
	
	public float[] getClearColor() {
		return mGlClearColor;
	}
	
	public List<GlOption> getOptions() {
		return mEnabledOptions;
	}
	
	public GlBlendFunc getBlendFunc() {
		return mBlendFunc;
	}
}
