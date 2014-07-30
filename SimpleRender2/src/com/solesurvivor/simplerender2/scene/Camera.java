package com.solesurvivor.simplerender2.scene;

import android.graphics.Point;
import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.simplerender2.rendering.RendererManager;

public class Camera {
	
	private static final String TAG = Camera.class.getSimpleName();

	protected float[] mViewMatrix = new float[16];	
	protected float[] mProjectionMatrix  = new float[16];
	protected float[] mUiMatrix = new float[16];
	protected float[] mEyePos = {0.0f, 0.0f, -0.5f};
	protected float[] mLookVector = {0.0f, 0.0f, -1.0f};
	protected float[] mUpVector = {0.0f, 1.0f, 0.0f};	
	protected Point mViewport = new Point(0,0);
	protected float mNear = 1.0f;
	protected float mFar = 200.0f;
	protected float mAspectAdj = 1.0f;
	
	public Camera() {		
		orient();
	}
	
	public float[] getViewMatrix() {
		return mViewMatrix;
	}

	public void setViewMatrix(float[] viewMatrix) {
		this.mViewMatrix = viewMatrix;
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
	
	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}
	
	public float[] getUiMatrix() {
		return mUiMatrix;
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
	
	public void adjustAspect(float adj) {
		this.mAspectAdj += adj;
		if(mAspectAdj <= 0.001f) mAspectAdj = 0.001f;
		float ratio = (float) mViewport.x / mViewport.y;
		Log.d(TAG, String.format("WIDTH IS: %s", (ratio + mAspectAdj)));
		resizeViewport(mViewport);
	}
	
	public float getAspect() {
		return mAspectAdj;
	}
	
	public void resizeViewport(Point newViewport) {
		
		RendererManager.inst().getRenderer().resizeViewport(newViewport);
		
		this.mViewport = newViewport;
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) mViewport.x / mViewport.y;
		
		//Rig for a 90 deg FOV
		final float left = -(ratio * mAspectAdj);
		final float right = ratio * mAspectAdj;
		final float bottom = -mAspectAdj;
		final float top = mAspectAdj;
		final float near = mNear;
		final float far = mFar;

		final float left_ortho = -(mViewport.x/2);
		final float right_ortho = (mViewport.x/2);
		final float bottom_ortho = -(mViewport.y/2);
		final float top_ortho = (mViewport.y/2);
		final float near_ortho = 1.0f;
		final float far_ortho = 200.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		Matrix.orthoM(mUiMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near_ortho, far_ortho);
	}
}
