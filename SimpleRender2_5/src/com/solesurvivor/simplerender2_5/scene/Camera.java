package com.solesurvivor.simplerender2_5.scene;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.util.logging.SSLog;

public class Camera {
	
	private static final String TAG = Camera.class.getSimpleName();

	protected float[] mViewMatrix = new float[16];	
	protected boolean mViewDirty = false;
	protected float[] mProjectionMatrix  = new float[16];
	protected float[] mOrthoMatrix = new float[16];
	protected float[] mEyePos = {0.0f, 2.0f, 0.0f};
	protected float[] mLookVector = {0.0f, 2.0f, -1.0f};
	protected float[] mUpVector = {0.0f, 1.0f, 0.0f};	
	protected Point mViewport = new Point(0,0);
	protected float mNear = 0.5f;
	protected float mFar = 200.0f;
	protected float mFov = 62.0f;
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
	
	public float[] getViewMatrix() {
		if(mViewDirty) {
			Matrix.setIdentityM(mViewMatrix, 0);
			orient();
			Matrix.rotateM(mViewMatrix, 0, mRotation[0], mRotation[1], mRotation[2], mRotation[3]);		
			Matrix.translateM(mViewMatrix, 0, mTranslation[0], mTranslation[1], mTranslation[2]);
			mViewDirty = false;
		}
		return mViewMatrix;
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
		mViewDirty = true;
	}
	
	public float getFar() {
		return this.mFar;
	}
	

	public float getNear() {
		return this.mNear;
	}
	
	public void resizeViewport(Point newViewport) {
		if(newViewport == null) {
			SSLog.d(TAG, "Null viewport sent to camera.");
			return;
		} else {
			SSLog.d(TAG, "Resizing camera viewport: %s,%s", newViewport.x, newViewport.y);
		}

		this.mViewport = newViewport;

		final float ratio = (float) mViewport.x / mViewport.y;
		
		final float near = mNear;
		final float far = mFar;

		final float left_ortho = -(mViewport.x/2);
		final float right_ortho = (mViewport.x/2);
		final float bottom_ortho = -(mViewport.y/2);
		final float top_ortho = (mViewport.y/2);
		
		//Rig for a 90 deg FOV
		Matrix.perspectiveM(mProjectionMatrix, 0, mFov, ratio, near, far);
		Matrix.orthoM(mOrthoMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near, far);
	}

	public float[] getTranslation() {
		return mTranslation;
	}

	public void setTranslation(float[] mCameraTranslation) {
		this.mTranslation = mCameraTranslation;
		mViewDirty = true;
	}

	public float[] getRotation() {
		return mRotation;
	}

	public void setRotation(float[] mCameraRotation) {
		this.mRotation = mCameraRotation;
		mViewDirty = true;
	}

	public float[] getVelocity() {
		return mVelocity;
	}

	public void setVelocity(float[] mCameraVelocity) {
		this.mVelocity = mCameraVelocity;
		mViewDirty = true;
	}

}
