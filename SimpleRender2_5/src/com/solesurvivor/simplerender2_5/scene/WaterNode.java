package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;

public class WaterNode extends StatefulNodeImpl {
	
	private static final Point DIM = new Point(1920,1920);

	@SuppressWarnings("unused")
	private static final String TAG = WaterNode.class.getSimpleName();
	
	protected Geometry mGeometry = null;
	protected GridMapRenderer mRen = null;
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected List<Light> mLights;
	protected Skybox skybox;
	protected ProceduralTexture2D reflectionTexture;
	protected ProceduralTexture2D refractionTexture;
	
	protected WaterNode() {
		mWorldMatrix = new float[16];		
		mLights = new ArrayList<Light>();
	}
	
	public WaterNode(Geometry geometry, int shaderHandle, int textureHandle) {
		this();
		this.mShaderHandle = shaderHandle;
		this.mGeometry = geometry;
		this.mRen = (GridMapRenderer)RendererManager.getRenderer();
		reflectionTexture = new ProceduralTexture2D("water_reflect_shader", "water_reflect_tex", DIM, CoordinateSystemEnum.CARTESIAN);
		refractionTexture = new ProceduralTexture2D("water_refract_shader", "water_refract_tex", DIM, CoordinateSystemEnum.CARTESIAN);
	}
	
	public WaterNode(Geometry geometry) {
		this(geometry, geometry.getShaderHandle(), geometry.getTextureHandle());
	}
	
	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}
	
	public Skybox getSkybox() {
		return this.skybox;
	}
	
	public Geometry getGeometry() {
		return this.mGeometry;
	}
	
	public  ProceduralTexture2D getReflectionTexture() {
		return this.reflectionTexture;
	}
	
	public  ProceduralTexture2D getReractionTexture() {
		return this.refractionTexture;
	}
	
	public Point getSize() {
		return DIM;
	}

	@Override
	public void render() {
		mRen.drawWaterPlane(this);
		renderChildren();
	}
	
}
