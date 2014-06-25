package com.solesurvivor.simplerender;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class SimpleRenderSurfaceView extends GLSurfaceView {

	private static final String TAG = SimpleRenderSurfaceView.class.getSimpleName();

	@SuppressWarnings("unused")
	private SimpleGLRenderer mRenderer;

	public SimpleRenderSurfaceView(Context context) {		
		super(context);
		Log.d(TAG, "GLSurfaceView created.");
	}

	@Override
	public void setRenderer(Renderer renderer) {

		if(renderer != null && renderer instanceof SimpleGLRenderer) {
			this.mRenderer = (SimpleGLRenderer)renderer;
		}

		super.setRenderer(renderer);
	}
	
//	@Override
//	public void onBackPressed() {
//		setContentView(R.layout.activity_simple_render);
//	}

}
