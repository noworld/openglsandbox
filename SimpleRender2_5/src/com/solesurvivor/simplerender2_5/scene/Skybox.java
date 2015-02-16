package com.solesurvivor.simplerender2_5.scene;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.math.Vec3;

public class Skybox implements Node, Drawable {

	@SuppressWarnings("unused")
	private static final String TAG = Skybox.class.getSimpleName();
	
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected int mPosHandle;
	protected int mIdxHandle;

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
	protected int mNumElements = 36;
	protected int mElementStride = 12;
	protected int mPosOffset = 0;
	protected float[] mModelMatrix = new float[16];
	
	public Skybox(String shaderName, String textureName) {
		this.mShaderHandle = ShaderManager.getShaderId(shaderName);
		this.mTextureHandle = TextureManager.getTextureId(textureName);
		BaseRenderer ren = RendererManager.getRenderer();
		mPosHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		Matrix.setIdentityM(mModelMatrix, 0);
	}

	@Override
	public int getShaderHandle() {
		return mShaderHandle;
	}

	public void setShaderHandle(int shader) {
		this.mShaderHandle = shader;
	}

	@Override
	public int getTextureHandle() {
		return mTextureHandle;
	}

	public void setTextureHandle(int texture) {
		this.mTextureHandle = texture;
	}

	@Override
	public int getDatBufHandle() {
		return mPosHandle;
	}

	public void setDatBufHandle(int posHandle) {
		this.mPosHandle = posHandle;
	}

	@Override
	public int getIdxBufHandle() {
		return mIdxHandle;
	}

	public void setIdxBufHandle(int idxHandle) {
		this.mIdxHandle = idxHandle;
	}

	@Override
	public int getPosSize() {
		return mPosSize;
	}
	
	public void setPosSize(int posSize) {
		this.mPosSize = posSize;
	}

	@Override
	public int getNumElements() {
		return mNumElements;
	}

	public void setNumElements(int numElements) {
		this.mNumElements = numElements;
	}

	@Override
	public int getElementStride() {
		return mElementStride;
	}

	public void setElementStride(int elementStride) {
		this.mElementStride = elementStride;
	}

	@Override
	public int getPosOffset() {
		return mPosOffset;
	}

	public void setPosOffset(int posOffset) {
		this.mPosOffset = posOffset;
	}

	@Override
	public float[] getWorldMatrix() {
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
		throw new NotImplementedException();
	}

	@Override
	public int getNrmSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTxcSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNrmOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTxcOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Light> getLights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getElementOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void scale(Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(Vec3 trans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getTransMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

}
