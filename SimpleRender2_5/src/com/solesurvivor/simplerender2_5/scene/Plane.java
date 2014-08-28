package com.solesurvivor.simplerender2_5.scene;

import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;
import com.solesurvivor.util.SSArrayUtil;

public class Plane implements Node, Drawable {

	@SuppressWarnings("unused")
	private static final String TAG = Plane.class.getSimpleName();
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected int mPosHandle;
	protected int mIdxHandle;
//	protected float[] mVertices = {
//			/*vvv*/-1.0f, 1.0f,  0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,0.0f,
//			/*vvv*/ 1.0f, 1.0f,  0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,0.0f,
//			/*vvv*/-1.0f, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,1.0f,
//			/*vvv*/1.0f,  -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,1.0f,};
	protected float mH = 1.0f;
	protected float mW = 1.7778f; 
	protected float[] mVertices = {
			/*vvv*/-mW,  mH, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,0.0f,
			/*vvv*/ mW,  mH, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,0.0f,
			/*vvv*/-mW, -mH, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,1.0f,
			/*vvv*/ mW, -mH, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,1.0f};
	protected short[] mIndexes = {0,2,1,
			1,2,3};
	protected int mPosSize = 3;
	protected int mNrmSize = 3;
	protected int mTxcSize = 2;
	protected int mNumElements = 6;
	protected int mElementStride = 32;
	protected int mPosOffset = 0;
	protected int mNrmOffset = 12;
	protected int mTxcOffset = 24;
	protected float[] mModelMatrix = new float[16];
	
	public Plane(String shaderName, String textureName) {
		this.mShaderHandle = ShaderManager.getShaderId(shaderName);
		this.mTextureHandle = TextureManager.getTextureId(textureName);
		BaseRenderer ren = RendererManager.getRenderer();
		mPosHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -2.0f);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		BaseRenderer ren = RendererManager.getRenderer();
		ren.drawGeometry(this);
	}

	@Override
	public void addChild(Node n) {

	}

	@Override
	public int getShaderHandle() {
		return this.mShaderHandle;
	}

	@Override
	public int getTextureHandle() {
		return this.mTextureHandle;
	}

	@Override
	public int getDatBufHandle() {
		return this.mPosHandle;
	}

	@Override
	public int getIdxBufHandle() {
		return this.mIdxHandle;
	}

	@Override
	public int getNrmSize() {
		return this.mNrmSize;
	}

	@Override
	public int getTxcSize() {
		return this.mTxcSize;
	}

	@Override
	public int getNrmOffset() {
		return this.mNrmOffset;
	}

	@Override
	public int getTxcOffset() {
		return this.mTxcOffset;
	}

	@Override
	public List<Light> getLights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPosSize() {
		return this.mPosSize;
	}

	@Override
	public int getNumElements() {
		return this.mNumElements;
	}

	@Override
	public int getElementStride() {
		return this.mElementStride;
	}

	@Override
	public int getPosOffset() {
		return this.mPosOffset;
	}

	@Override
	public float[] getModelMatrix() {
		return this.mModelMatrix;
	}
}
