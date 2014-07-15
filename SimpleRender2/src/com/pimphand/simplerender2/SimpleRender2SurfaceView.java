package com.pimphand.simplerender2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class SimpleRender2SurfaceView extends GLSurfaceView {
	
	private static final String TAG = SimpleRender2SurfaceView.class.getSimpleName();

	public SimpleRender2SurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		Log.d(TAG, "GLSurfaceView created.");
	}
	
	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
	}

}
