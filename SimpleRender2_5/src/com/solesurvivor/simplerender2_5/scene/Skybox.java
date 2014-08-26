package com.solesurvivor.simplerender2_5.scene;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.util.SSArrayUtil;

public class Skybox implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = Skybox.class.getSimpleName();
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected int mPosHandle;
	protected int mIdxHandle;
//	protected float[] mVertices = {-1.0f, -1.0f,  1.0f,
//						1.0f, -1.0f,  1.0f,
//						-1.0f,  1.0f,  1.0f,
//						1.0f,  1.0f,  1.0f,
//						-1.0f, -1.0f, -1.0f,
//						1.0f, -1.0f, -1.0f,
//						-1.0f,  1.0f, -1.0f,
//						1.0f,  1.0f, -1.0f};
//	protected short[] mIndexes = {0, 1, 2,
//			1,2,3, 
//			2,3,7, 
//			3,7,1, 
//			7,1,5, 
//			1,5,4, 
//			5,4,7, 
//			4,7,6, 
//			7,6,2, 
//			6,2,4, 
//			2,4,0, 
//			4,0,1};
	protected float[] mVertices = {-10.0f, -10.0f,  10.0f,
			10.0f, -10.0f,  10.0f,
			-10.0f,  10.0f,  10.0f,
			10.0f,  10.0f,  10.0f,
			-10.0f, -10.0f, -10.0f,
			10.0f, -10.0f, -10.0f,
			-10.0f,  10.0f, -10.0f,
			10.0f,  10.0f, -10.0f};
	protected short[] mIndexes = {4,7,6,
			7,4,5,			
			0,6,2,
			6,0,4,
			5,3,7,
			3,5,1,
			1,2,3,
			2,1,0,
			4,1,5,
			4,0,1,
			7,3,2,
			2,6,7};
	protected int mPosSize = 3;
//	protected int mNumElements = 12;
	protected int mNumElements = 36;
	protected int mElementStride = 12;
	protected int mPosOffset = 0;
	protected float[] mModelMatrix = new float[16];
	
	public Skybox(int shader, int texture) {
		this.mShaderHandle = shader;
		this.mTextureHandle = texture;
		
		BaseRenderer ren = RendererManager.getRenderer();
		mPosHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		
		Matrix.setIdentityM(mModelMatrix, 0);
//		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
	}

	public int getShader() {
		return mShaderHandle;
	}

	public void setShaderHandle(int shader) {
		this.mShaderHandle = shader;
	}

	public int getTexture() {
		return mTextureHandle;
	}

	public void setTextureHandle(int texture) {
		this.mTextureHandle = texture;
	}

	public int getPosHandle() {
		return mPosHandle;
	}

	public void setPosHandle(int posHandle) {
		this.mPosHandle = posHandle;
	}

	public int getIdxHandle() {
		return mIdxHandle;
	}

	public void setIdxHandle(int idxHandle) {
		this.mIdxHandle = idxHandle;
	}

	public int getPosSize() {
		return mPosSize;
	}

	public void setPosSize(int posSize) {
		this.mPosSize = posSize;
	}

	public int getNumElements() {
		return mNumElements;
	}

	public void setNumElements(int numElements) {
		this.mNumElements = numElements;
	}

	public int getElementStride() {
		return mElementStride;
	}

	public void setElementStride(int elementStride) {
		this.mElementStride = elementStride;
	}

	public int getPosOffset() {
		return mPosOffset;
	}

	public void setPosOffset(int posOffset) {
		this.mPosOffset = posOffset;
	}

	public float[] getModelMatrix() {
		return mModelMatrix;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		BaseRenderer ren = RendererManager.getRenderer();
		ren.drawSkybox(this);
	}

	@Override
	public void addChild(Node n) {

	}
}
