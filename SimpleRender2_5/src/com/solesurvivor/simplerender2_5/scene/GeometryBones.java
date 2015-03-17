package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.simplerender2_5.scene.animation.Armature;
import com.solesurvivor.simplerender2_5.scene.animation.Pose;

public class GeometryBones extends Geometry implements DrawableBones {
	
	
	protected boolean bones = false;
	protected int boneCountSize;
	protected int boneCountOffset;
	protected int boneIndexSize;
	protected int boneIndexOffset;
	protected int boneWeightSize;
	protected int boneWeightOffset;
	protected Armature armature;
	protected Pose pose;
	protected Pose restPose;
	protected Pose restPoseInv;
	
	public GeometryBones(Geometry geo) {
		super();
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
		
		if(geo instanceof GeometryBones) {			
			this.bones = ((GeometryBones) geo).bones;
			this.boneCountOffset = ((GeometryBones) geo).boneCountOffset;
			this.boneCountSize = ((GeometryBones) geo).boneCountSize;
			this.boneIndexOffset = ((GeometryBones) geo).boneIndexOffset;
			this.boneIndexSize = ((GeometryBones) geo).boneIndexSize;
			this.boneWeightOffset = ((GeometryBones) geo).boneWeightOffset;
			this.boneWeightSize = ((GeometryBones) geo).boneWeightSize;
		}
		
	}

	public GeometryBones(String name, int shaderHandle, int dataBufHandle, int idxBufHandle,
			int posSize, int nrmSize, int txcSize, int posOffset,
			int nrmOffset, int txcOffset, int numElements, int elementStride,
			int textureHandle, boolean bones, int boneCountSize, int boneCountOffset,
			int boneIndexSize, int boneIndexOffset, int boneWeightSize, int boneWeightOffset) {
		super();
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
		this.bones = bones;
		this.boneCountOffset = boneCountOffset;
		this.boneCountSize = boneCountSize;
		this.boneIndexSize = boneIndexSize;
		this.boneIndexOffset = boneIndexOffset;
		this.boneWeightSize = boneWeightSize;
		this.boneWeightOffset = boneWeightOffset;
				
	}

	@Override
	public int getBoneCountSize() {
		return boneCountSize;
	}

	@Override
	public int getBoneCountOffset() {
		return boneCountOffset;
	}

	@Override
	public boolean getBones() {
		return bones;
	}

	@Override
	public int getBoneIndexSize() {
		return boneIndexSize;
	}

	@Override
	public int getBoneIndexOffset() {
		return boneIndexOffset;
	}

	@Override
	public int getBoneWeightSize() {
		return boneWeightSize;
	}

	@Override
	public int getBoneWeightOffset() {
		return boneWeightOffset;
	}

	@Override
	public Armature getArmature() {
		return armature;
	}
	
	public void setArmature(Armature arm) {
		this.armature = arm;
	}

	@Override
	public Pose getPose() {
		return pose;
	}
	
	public void setPose(Pose p) {
		this.pose = p;
	}

	@Override
	public Pose getRestPose() {
		return restPose;
	}
	
	public void setRestPose(Pose p) {
		this.restPose = p;
	}

	@Override
	public Pose getRestPoseInv() {
		return restPoseInv;
	}
	
	public void setRestPoseInv(Pose p) {
		this.restPoseInv = p;
	}

}
