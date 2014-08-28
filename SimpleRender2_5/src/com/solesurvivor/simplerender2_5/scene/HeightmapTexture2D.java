package com.solesurvivor.simplerender2_5.scene;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.rendering.RendererManager;

/**
 * Use with hm_shader 
 * @author nicholas.waun
 * (C)2014 Nicholas Waun. All rights reserved.
 */
public class HeightmapTexture2D extends ProceduralTexture2D implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = HeightmapTexture2D.class.getSimpleName();

	protected int mIterations = 20;
	protected float mMaxHeight = 1.0f;
	protected float mMinHeight = 0.0f;
	
	public HeightmapTexture2D(String shaderName, String textureName, Point dim) {
		super(shaderName, textureName, dim);
		RendererManager.getRenderer().renderHeightmap(this, mIterations, mMaxHeight, mMinHeight, 1);

	}
	
	//Only needs to render once
	@Override
	public void render() {		
		for(Node n : mChildren) {
			n.render();
		}
	}
}
