package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.util.math.Vec3;

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
	protected int mElementOffset = 0;
	
	protected float[] mWorldMatrix;
	protected int mTextureHandle;
	protected List<Light> mLights;
	
	protected Vec3 mTransDir;
	protected Vec3 mScaleFac;
	protected Vec3 mRotAxes;
	protected float mRotAngle;	
	
	protected boolean mDirty = true;
	protected NodeType intendedType = NodeType.ENTITY;
	
	protected Geometry() {
		super();
		mWorldMatrix = new float[16];		
		mLights = new ArrayList<Light>();
		resetTransforms();
	}
	
	public Geometry(Geometry geo) {
		this();
		this.mName = geo.mName;
		this.mShaderHandle = geo.mShaderHandle;
		this.mDataBufHandle = geo.mDataBufHandle;
		this.mIdxBufHandle = geo.mIdxBufHandle;
		this.mPosSize = geo.mPosSize;
		this.mNrmSize = geo.mNrmSize;
		this.mTxcSize = geo.mTxcSize;
		this.mPosOffset = geo.mPosOffset;
		this.mNrmOffset = geo.mNrmOffset;
		this.mTxcOffset = geo.mTxcOffset;
		this.mNumElements = geo.mNumElements;
		this.mElementStride = geo.mElementStride;
		this.mTextureHandle = geo.mTextureHandle;
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
	
	public String getName() {
		return mName;
	}
	
	public void setShaderHandle(int shaderHandle) {
		this.mShaderHandle = shaderHandle;
	}

	@Override
	public int getShaderHandle() {
		return mShaderHandle;
	}
	
	public void setTextureHandle(int texHandle) {
		this.mTextureHandle = texHandle;
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
	public int getElementOffset() {
		return mElementOffset;
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
	
	public void addLight(Light l) {
		mLights.add(l);
	}

	@Override
	public List<Light> getLights() {
		return mLights;
	}
	
	public void setPosOffset(int offset) {
		this.mPosOffset = offset;
	}
	
	public void setNrmOffset(int offset) {
		this.mNrmOffset = offset;
	}
	
	public void setTxcOffset(int offset) {
		this.mTxcOffset = offset;
	}
	
	public void setElementOffset(int offset) {
		this.mElementOffset = offset;
	}
	
	public void setNumElements(int numElements) {
		this.mNumElements = numElements;
	}
	
	public void setIntendedType(NodeType t) {
		this.intendedType = t;
	}
	
	public NodeType getIntendedType() {
		return intendedType;
	}
	
	public void rotate(float angle, Vec3 dir) {
		if(mRotAxes == null) {
			mRotAxes = dir.normalizeClone();
		} else {
			mRotAxes.add(dir);
			mRotAxes.normalize();
		}
		
		mRotAngle += angle;
		mDirty = true;
	}
	
	public void translate(Vec3 dir) {
		mTransDir.add(dir);
		mDirty = true;
	}
	
	public void scale(Vec3 fac) {
		mScaleFac.componentScale(fac);
		mDirty = true;
	}
	
	public void resetTransforms() {
		Matrix.setIdentityM(mWorldMatrix, 0);
		mTransDir = new Vec3(0.0f,0.0f,0.0f);
		mScaleFac = new Vec3(1.0f,1.0f,1.0f);
		mRotAxes = null;
		mRotAngle = 0.0f;	
		mDirty = false;
	}
	
	protected void applyTransforms() {
		Matrix.setIdentityM(mWorldMatrix, 0);		
		Matrix.translateM(mWorldMatrix, 0, mTransDir.getX(), mTransDir.getY(), mTransDir.getZ());
		if(mRotAxes != null) {
			Matrix.rotateM(mWorldMatrix, 0, mRotAngle, mRotAxes.getX(), mRotAxes.getY(), mRotAxes.getZ());
		}
		Matrix.scaleM(mWorldMatrix, 0, mScaleFac.getX(), mScaleFac.getY(), mScaleFac.getZ());

		mDirty = false;
	}
	
	public Geometry clone() {
		return new Geometry(this);
	}

}
