package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;

public class ProceduralTexture_16_9 extends Geometry_16_9 implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = ProceduralTexture_16_9.class.getSimpleName();
	
	//frame, depth, texture
	//or just texture
	protected int[] mBuffers;
	protected int mShaderHandle;
	protected boolean mProcedural = false;
	protected List<Node> mChildren;
	
	public ProceduralTexture_16_9(String shader) {
		mChildren = new ArrayList<Node>();
		this.mShaderHandle = ShaderManager.getShaderId(shader);
		BaseRenderer ren = RendererManager.getRenderer();
		mBuffers = ren.genTextureBuffer(DIMENSION);
	}
	
	public int getShaderHandle() {
		return mShaderHandle;
	}
	
	public int getTextureHandle() {
		return mBuffers[2];
	}
	
	public int[] getBuffers() {
		return mBuffers;
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

}
