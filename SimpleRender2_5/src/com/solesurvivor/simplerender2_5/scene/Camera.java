package com.solesurvivor.simplerender2_5.scene;

import android.graphics.Point;
import android.opengl.Matrix;
import android.util.Log;

public class Camera {
	
	private static final String TAG = Camera.class.getSimpleName();

	protected float[] mViewMatrix = new float[16];	
	protected float[] mProjectionMatrix  = new float[16];
	protected float[] mOrthoMatrix = new float[16];
	protected float[] mEyePos = {0.0f, 0.0f, -0.5f};
	protected float[] mLookVector = {0.0f, 0.0f, -1.0f};
	protected float[] mUpVector = {0.0f, 1.0f, 0.0f};	
	protected Point mViewport = new Point(0,0);
	protected float mNear = 0.5f;
	protected float mFar = 200.0f;
	protected float[] mTranslation = {0.0f, 0.0f, 0.0f};
	protected float[] mRotation = {0.0f, 0.0f, 0.0f, 1.0f};
	protected float[] mVelocity = {0.0f, 0.0f, 0.0f};
	
	public Camera() {		
		orient();
	}

	public float[] getEyePos() {
		return mEyePos;
	}

	public void setEyePos(float[] eyePos) {
		this.mEyePos = eyePos;
		orient();
	}

	public float[] getLookVector() {
		return mLookVector;
	}

	public void setLookVector(float[] lookVector) {
		this.mLookVector = lookVector;
		orient();
	}

	public float[] getUpVector() {
		return mUpVector;
	}

	public void setUpVector(float[] upVector) {
		this.mUpVector = upVector;
		orient();
	}
	
	public Point getViewport() {
		return mViewport;
	}

	public void setViewport(Point viewport) {
		this.mViewport = viewport;
	}
	
	public float[] getViewMatrix() {
		return mViewMatrix;
	}

	public void setViewMatrix(float[] viewMatrix) {
		this.mViewMatrix = viewMatrix;
		orient();
	}
	
	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}
	
	public float[] getOrthoMatrix() {
		return mOrthoMatrix;
	}

	protected void orient() {
		Matrix.setLookAtM(mViewMatrix, 0, mEyePos[0], mEyePos[1], mEyePos[2], 
				mLookVector[0], mLookVector[1], mLookVector[2],
				mUpVector[0], mUpVector[1], mUpVector[2]);
	}
	
	public void adjustFar(float adj) {
		this.mFar += adj;
		if(mFar <= mNear) mFar = mNear + 0.1f;
		Log.d(TAG, String.format("FAR IS: %s", mFar));
		resizeViewport(mViewport);
	}
	
	public float getFar() {
		return this.mFar;
	}
	
	public void adjustNear(float adj) {
		this.mNear += adj;
		if(mNear <= 0.0f) mNear = 0.001f;
		if(mFar <= mNear) mFar = mNear + 0.1f;
		Log.d(TAG, String.format("NEAR IS: %s", mNear));
		resizeViewport(mViewport);
	}
	
	public float getNear() {
		return this.mNear;
	}
	
	public void resizeViewport(Point newViewport) {

		this.mViewport = newViewport;

		final float ratio = (float) mViewport.x / mViewport.y;
		
		final float near = mNear;
		final float far = mFar;

		final float left_ortho = -(mViewport.x/2);
		final float right_ortho = (mViewport.x/2);
		final float bottom_ortho = -(mViewport.y/2);
		final float top_ortho = (mViewport.y/2);
		
		//Rig for a 90 deg FOV
		Matrix.perspectiveM(mProjectionMatrix, 0, 90.0f, ratio, near, far);
		Matrix.orthoM(mOrthoMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near, near);
	}

	public float[] getTranslation() {
		return mTranslation;
	}

	public void setTranslation(float[] mCameraTranslation) {
		this.mTranslation = mCameraTranslation;
	}

	public float[] getRotation() {
		return mRotation;
	}

	public void setRotation(float[] mCameraRotation) {
		this.mRotation = mCameraRotation;
	}

	public float[] getVelocity() {
		return mVelocity;
	}

	public void setVelocity(float[] mCameraVelocity) {
		this.mVelocity = mCameraVelocity;
	}

}
