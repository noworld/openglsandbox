package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.util.math.Vec3;

import android.opengl.Matrix;
import android.test.MoreAsserts;

public class Geometry implements Drawable {

	@SuppressWarnings("unused")
	private static final String TAG = Geometry.class.getSimpleName();
	
	protected String mName;
	
	protected int mShaderHandle;
	protected int mDataBufHandle;
	protected int mIdxBufHandle;
	
	protected int mPosSize;
	protected int mNrmSize;
	protected int mTxcSize;
	
	protected int mPosOffset;
	protected int mNrmOffset;
	protected int mTxcOffset;
	
	protected int mNumElements;
	protected int mElementStride;
	
	protected float[] mWorldMatrix;
	protected int mTextureHandle;
	protected List<Light> mLights;
	
	protected float[] mRotMatrix;
	protected float[] mTransMatrix;
	protected float[] mScaleMatrix;
	protected Vec3 mRotAxes;
	protected float mRotAngle = 0.0f;
	
	protected boolean mDirty = true;
	
	protected Geometry() {
		super();
		mWorldMatrix = new float[16];
		mRotMatrix = new float[16];
		mTransMatrix = new float[16];
		mScaleMatrix = new float[16];
		resetTransforms();
		mLights = new ArrayList<Light>();
	}

	public Geometry(String name, int shaderHandle, int dataBufHandle, int idxBufHandle,
			int posSize, int nrmSize, int txcSize, int posOffset,
			int nrmOffset, int txcOffset, int numElements, int elementStride,
			int textureHandle) {
		this();
		this.mName = name;
		this.mShaderHandle = shaderHandle;
		this.mDataBufHandle = dataBufHandle;
		this.mIdxBufHandle = idxBufHandle;
		this.mPosSize = posSize;
		this.mNrmSize = nrmSize;
		this.mTxcSize = txcSize;
		this.mPosOffset = posOffset;
		this.mNrmOffset = nrmOffset;
		this.mTxcOffset = txcOffset;
		this.mNumElements = numElements;
		this.mElementStride = elementStride;
		this.mTextureHandle = textureHandle;
	}

	@Override
	public int getShaderHandle() {
		return mShaderHandle;
	}

	@Override
	public int getTextureHandle() {
		return mTextureHandle;
	}

	@Override
	public int getDatBufHandle() {
		return mDataBufHandle;
	}

	@Override
	public int getIdxBufHandle() {
		return mIdxBufHandle;
	}

	@Override
	public int getPosSize() {
		return mPosSize;
	}

	@Override
	public int getNrmSize() {
		return mNrmSize;
	}

	@Override
	public int getTxcSize() {
		return mTxcSize;
	}

	@Override
	public int getNumElements() {
		return mNumElements;
	}

	@Override
	public int getElementStride() {
		return mElementStride;
	}

	@Override
	public int getPosOffset() {
		return mPosOffset;
	}

	@Override
	public int getNrmOffset() {
		return mNrmOffset;
	}

	@Override
	public int getTxcOffset() {
		return mTxcOffset;
	}

	@Override
	public float[] getWorldMatrix() {
		if(mDirty) {
			applyTransforms();
		}
		return mWorldMatrix;
	}

	@Override
	public List<Light> getLights() {
		return mLights;
	}
	
	public void rotate(float angle, Vec3 dir) {
		Matrix.rotateM(mRotMatrix, 0, angle, dir.getX(), dir.getY(), dir.getZ());
		mRotAxes = dir.normalize();
		mRotAngle += angle;
		mDirty = true;
	}
	
	public void translate(Vec3 dir) {
		Matrix.translateM(mTransMatrix, 0, dir.getX(), dir.getY(), dir.getZ());
		mDirty = true;
	}
	
	public void scale(Vec3 scale) {
		Matrix.scaleM(mScaleMatrix, 0, scale.getX(), scale.getY(), scale.getZ());
		mDirty = true;
	}
	
	public void resetTransforms() {
		Matrix.setIdentityM(mWorldMatrix, 0);
		Matrix.setIdentityM(mRotMatrix, 0);
		Matrix.setIdentityM(mTransMatrix, 0);
		Matrix.setIdentityM(mScaleMatrix, 0);
		mDirty = false;
	}
	
	private void applyTransforms() {
		float[] temp = new float[16];
		Matrix.setIdentityM(temp, 0);
		Matrix.multiplyMM(mWorldMatrix, 0, mTransMatrix, 0, temp, 0);
		Matrix.rotateM(mWorldMatrix, 0, mRotAngle, mRotAxes.getX(), mRotAxes.getY(), mRotAxes.getZ());
		
//		mWorldMatrix = temp;
//		Matrix.multiplyMM(temp2, 0, mRotMatrix, 0, mWorldMatrix, 0);
//		mWorldMatrix = temp2;

		mDirty = false;
	}
}
