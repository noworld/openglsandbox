package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;

public class WaterNode extends StatefulNodeImpl {

	@SuppressWarnings("unused")
	private static final String TAG = WaterNode.class.getSimpleName();
	
	protected Geometry mGeometry = null;
	protected GridMapRenderer mRen = null;
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected List<Light> mLights;
	
	protected WaterNode() {
		mWorldMatrix = new float[16];		
		mLights = new ArrayList<Light>();
	}
	
	public WaterNode(Geometry geometry, int shaderHandle, int textureHandle) {
		this();
		this.mShaderHandle = shaderHandle;
		this.mTextureHandle = textureHandle;
		this.mGeometry = geometry;
		this.mRen = (GridMapRenderer)RendererManager.getRenderer();
	}
	
	public WaterNode(Geometry geometry) {
		this(geometry, geometry.getShaderHandle(), geometry.getTextureHandle());
	}

	@Override
	public void render() {
		mRen.drawGeometry(mGeometry, this.getWorldMatrix());
		renderChildren();
	}
	
}
