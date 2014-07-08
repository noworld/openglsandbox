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
		setEGLContextClientVersion(2);
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

		int action = MotionEventCompat.getActionMasked(event);

		switch(action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			handlePointerEvent(event, false);
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			handlePointerEvent(event, true);
			break;
		}

		return true;
	}
	
	public void handlePointerEvent(MotionEvent event, boolean up) {
		int index = MotionEventCompat.getActionIndex(event);
		InputHandler.reset();
		if(event.getPointerCount() > 0) {
			for(int i = 0; i < event.getPointerCount(); i++) {
				if(up && i == index) continue;
				int xPos = (int)MotionEventCompat.getX(event, i);
				int yPos = (int)MotionEventCompat.getY(event, i);				
				InputHandler.touch(xPos, yPos);
			}
		}
	}

}
