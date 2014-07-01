package com.solesurvivor.simplerender;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.solesurvivor.simplerender.renderer.SimpleGLRenderer;

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

	public boolean onTouchEvent(MotionEvent event) {

		InputHandler.reset();
		if(event.getPointerCount() > 0) {
			for(int i = 0; i < event.getPointerCount(); i++) {
				int xPos = (int)MotionEventCompat.getX(event, i);
				int yPos = (int)MotionEventCompat.getY(event, i);
				
				InputHandler.touch(xPos, yPos);
			}
		}

		return true;
	}


}
