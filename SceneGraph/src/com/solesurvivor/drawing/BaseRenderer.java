package com.solesurvivor.drawing;

import java.util.Map;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.scene.Camera;
import com.solesurvivor.scene.Scene;

public abstract class BaseRenderer {
	
	protected Context mContext;
	protected Scene mScene;
	protected Map<String,ShaderProgram> mShaders;
	
	protected Camera mCurrentCamera;
	protected float[] mProjectionMatrix = new float[16];
	protected float[] mMVPMatrix = new float[16];

	protected void initOpenGL() {
		Log.d("initOpenGL", "START");

		// Set the background clear color
		GLES20.glClearColor(0.25f, 0.5f, 0.25f, 0.0f);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable Z-buffer?
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}
	
	protected void resizeViewport(int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 200.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}
	
	protected void clearOpenGL() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	protected abstract void loadScene();
	
	protected abstract void drawScene();

}
