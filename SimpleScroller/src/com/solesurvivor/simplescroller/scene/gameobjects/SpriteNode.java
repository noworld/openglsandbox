package com.solesurvivor.simplescroller.scene.gameobjects;

import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.simplescroller.rendering.ScrollerRenderer;
import com.solesurvivor.simplescroller.rendering.ShaderManager;
import com.solesurvivor.simplescroller.scene.Drawable;
import com.solesurvivor.simplescroller.scene.Light;
import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;
import com.solesurvivor.util.math.Vec3;

public class SpriteNode extends StatefulNodeImpl implements Drawable {
	
	private static final String DEFAULT_SHADER = "twodee_shader";
	
	protected Sprite sprite;
	protected Vec3 animTrans;
	protected int shaderHandle;
	protected ScrollerRenderer ren;
	protected float speed = 0.0f;
	
	public SpriteNode(String spriteName, float scale) {
		this(spriteName, DEFAULT_SHADER, scale);
	}

	public SpriteNode(String spriteName, String shaderName, float scale) {
		super();
		this.sprite = SpriteManager.getSprite(spriteName);
		this.animTrans = Vec3.createZeroVec3();
		this.shaderHandle = ShaderManager.getShaderId(shaderName);
		this.ren = RendererManager.getRenderer();
		if(scale != 1.0) {
			Matrix.scaleM(this.scaleMatrix, 0, scale, scale, 1.0f);
		}
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
		
	public Vec3 getAnimTrans() {
		return animTrans;
	}
	
	public Vec3 getAnimTransScale() {
		Vec3 scaledTrans = animTrans.clone();
		scaledTrans.componentScale(Vec3.fromFloatArray(new float[]{scaleMatrix[0],scaleMatrix[5],scaleMatrix[10]}));
		return scaledTrans;
	}
	
	public void translateAnimation(Vec3 trans) {
		animTrans.add(trans);
		super.translate(trans);
	}
	
	public void translateAnimationScale(Vec3 trans) {
		Vec3 scaledTrans = trans.clone();
		scaledTrans.componentScale(Vec3.fromFloatArray(new float[]{scaleMatrix[0],scaleMatrix[5],scaleMatrix[10]}));
		super.translate(scaledTrans);
	}
	
	public void translateAnimationTest(Vec3 trans) {
		Vec3 scaledTrans = trans.clone();
		scaledTrans.componentScale(Vec3.fromFloatArray(new float[]{3.85f*2,3.85f*2,1.0f}));
		super.translate(scaledTrans);
	}
	
	@Override
	public void render() {
		ren.drawSprite(this);
		renderChildren();
	}

	@Override
	public int getShaderHandle() {
		return this.shaderHandle;
	}

	@Override
	public int getTextureHandle() {
		return sprite.getTexHandle();
	}

	@Override
	public int getDatBufHandle() {
		return sprite.getDatHandle();
	}

	@Override
	public int getIdxBufHandle() {
		return sprite.getIdxHandle();
	}

	@Override
	public int getPosSize() {
		return sprite.getPosSize();
	}

	@Override
	public int getNrmSize() {
		return sprite.getNrmSize();
	}

	@Override
	public int getTxcSize() {
		return sprite.getTxcSize();
	}

	@Override
	public int getNumElements() {
		return sprite.getNumElements();
	}

	@Override
	public int getElementStride() {
		return sprite.getElementStride();
	}

	@Override
	public int getElementOffset() {
		return 0;
	}

	@Override
	public int getPosOffset() {
		return sprite.getPosOffset();
	}

	@Override
	public int getNrmOffset() {
		return sprite.getNrmOffset();
	}

	@Override
	public int getTxcOffset() {
		return sprite.getTxcOffset();
	}

	@Override
	public List<Light> getLights() {
		return null;
	}

}
