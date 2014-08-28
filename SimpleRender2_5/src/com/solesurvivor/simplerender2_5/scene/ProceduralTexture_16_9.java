package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;

public class ProceduralTexture_16_9 extends Geometry_16_9 implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = ProceduralTexture_16_9.class.getSimpleName();
	
	//frame, depth, texture
	//or just texture
	protected int[] mBuffers;
	protected int mShaderHandle;
	protected List<Node> mChildren;
	protected String mTextureName;
	
	public ProceduralTexture_16_9(String shaderName, String textureName) {
		mChildren = new ArrayList<Node>();
		this.mShaderHandle = ShaderManager.getShaderId(shaderName);
		BaseRenderer ren = RendererManager.getRenderer();
		mBuffers = ren.genTextureBuffer(DIMENSION);
		this.mTextureName = textureName;
		TextureManager.registerTexture(textureName, mBuffers[2]);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -2.0f);
	}

	public int[] getBuffers() {
		return mBuffers;
	}
	
	public String getTextureName() {
		return mTextureName;
	}

	@Override
	public void update() {
		for(Node n : mChildren) {
			n.update();
		}
	}

	@Override
	public void render() {
		RendererManager.getRenderer().renderTexture(this);
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
		return mShaderHandle;
	}	
	
	@Override
	public int getTextureHandle() {
		return mBuffers[2];
	}
}
