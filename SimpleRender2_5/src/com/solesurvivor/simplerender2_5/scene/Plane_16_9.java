package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;

public class Plane_16_9 extends Geometry_16_9 implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = Plane_16_9.class.getSimpleName();
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected List<Node> mChildren;
	
	public Plane_16_9(String shaderName, String textureName) {
		super();
		mChildren = new ArrayList<Node>();
		this.mShaderHandle = ShaderManager.getShaderId(shaderName);
		this.mTextureHandle = TextureManager.getTextureId(textureName);	
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -2.0f);
	}

	@Override
	public void update() {
		for(Node n : mChildren) {
			n.update();
		}
	}

	@Override
	public void render() {		
		BaseRenderer ren = RendererManager.getRenderer();
		ren.drawGeometry(this);
		for(Node n : mChildren) {
			n.render();
		}
	}

	@Override
	public void addChild(Node n) {
		mChildren.add(n);
	}

	@Override
	public int getShaderHandle() {
		return this.mShaderHandle;
	}

	@Override
	public int getTextureHandle() {
		return this.mTextureHandle;
	}

}
