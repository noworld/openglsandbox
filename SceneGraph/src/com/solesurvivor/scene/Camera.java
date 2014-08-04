package com.solesurvivor.scene;

import android.opengl.Matrix;

import com.solesurvivor.drawing.CameraTypeEnum;
import com.solesurvivor.drawing.PanoramaTypeEnum;

public class Camera extends Point {
	
	/*eye initialization*/
	// Position the eye in front of the origin.
	protected final float eyeX = 0.0f;
	protected final float eyeY = 0.0f;
	protected final float eyeZ = -0.5f;

	// We are looking toward the distance
	protected final float lookX = 0.0f;
	protected final float lookY = 0.0f;
	protected final float lookZ = -5.0f;

	// Set our up vector. This is where our head would be pointing were we holding the camera.
	protected final float upX = 0.0f;
	protected final float upY = 1.0f;
	protected final float upZ = 0.0f;
	
	public float[] mViewMatrix = new float[16];

	/*lens*/
	public float mFocalLength;

	/* motion blur */
	public float mShuttertime;

	/* depth of field */
	public float mFocalDistance;
	public float mApertureSize;
	public int mBlades;
	public float mBladesRotation;

	/* type */
	CameraTypeEnum mCameraType;
	public float mFov;

	/* panorama */
	PanoramaTypeEnum mPanoramaType;
	public float mFisheyeFov;
	public float mFisheyeLens;

	/* sensor */
	public float mSensorWidth;
	public float mSsensorHeight;

	/* clipping */
	public float mNearClip;
	public float mFarClip;

	/* screen */
	public int mWidth;
	public int mHeight;
	public int mResolution;
	//	BoundBox2D viewplane;

	/* border */
	//	BoundBox2D border;

	/* transformation */
	//	Transform matrix;

	/* motion */
	//	MotionTransform motion;
	//	bool use_motion;

	/* computed camera parameters */
	//	Transform screentoworld;
	//	Transform rastertoworld;
	//	Transform ndctoworld;
	//	Transform cameratoworld;
	//
	//	Transform worldtoraster;
	//	Transform worldtoscreen;
	//	Transform worldtondc;
	//	Transform worldtocamera;
	//
	//	Transform rastertocamera;
	//	Transform cameratoraster;

	//	float3 dx;
	//	float3 dy;

	/* update */
	//	bool need_update;
	//	bool need_device_update;
	//	int previous_need_motion;

	@Override
	public void init() {
		

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);	
	}
	
}
