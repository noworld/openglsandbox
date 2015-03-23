package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;

public class GeometryNode extends StatefulNodeImpl {

	@SuppressWarnings("unused")
	private static final String TAG = GeometryNode.class.getSimpleName();
	
	protected Geometry mGeometry = null;
	protected GridMapRenderer mRen = null;
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected List<Light> mLights;
	
	protected GeometryNode() {
		mWorldMatrix = new float[16];		
		mLights = new ArrayList<Light>();
	}
	
	public GeometryNode(Geometry geometry, int shaderHandle, int textureHandle) {
		this();
		this.mShaderHandle = shaderHandle;
		this.mTextureHandle = textureHandle;
		this.mGeometry = geometry;
		this.mRen = (GridMapRenderer)RendererManager.getRenderer();
	}
	
	public GeometryNode(Geometry geometry) {
		this(geometry, geometry.getShaderHandle(), geometry.getTextureHandle());
	}

	@Override
	public void render() {
		mRen.drawGeometry(mGeometry, this.getWorldMatrix());
		renderChildren();
	}
	
}
