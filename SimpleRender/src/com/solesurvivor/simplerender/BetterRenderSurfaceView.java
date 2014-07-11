package com.solesurvivor.simplerender;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;

public class BetterRenderSurfaceView extends GLSurfaceView {

	private static final String TAG = BetterRenderSurfaceView.class.getSimpleName();

	private BetterUiGLTextureRenderer mRenderer;

	public BetterRenderSurfaceView(Context context) {		
		super(context);
		setEGLContextClientVersion(2);
		Log.d(TAG, "GLSurfaceView created.");
	}

	@Override
	public void setRenderer(Renderer renderer) {

		if(renderer != null && renderer instanceof BetterUiGLTextureRenderer) {
			this.mRenderer = (BetterUiGLTextureRenderer)renderer;
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
		if(event.getPointerCount() > 0) {
			for(int i = 0; i < event.getPointerCount(); i++) {
				int xPos = (int)MotionEventCompat.getX(event, i);
				int yPos = (int)MotionEventCompat.getY(event, i);		
				Point p = new Point(xPos, yPos);
				
				/*DEBUG*/
				if(up) {
					Log.d(TAG, String.format("Liftoff at %s,%s", p.x, p.y));
				} else {
					Log.d(TAG, String.format("Touchdown at %s,%s", p.x, p.y));
				}
				
				if(up && mRenderer.mPointers.containsKey(i)) {
					mRenderer.mPointers.remove(i);
				} else if(!up && ! mRenderer.mPointers.containsKey(i)) {
					mRenderer.mPointers.put(i,p);
				}
				
			}
		}
	}

}
