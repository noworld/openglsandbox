package com.solesurvivor.simplerender;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
import com.solesurvivor.simplerender.ui.InputEventTypeEnum;
import com.solesurvivor.simplerender.util.EventRecorder;

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

		//TODO: Ship these events off to a queue to
		//avoid the concurrent modification exceptions...
		int action = MotionEventCompat.getActionMasked(event);
		
		/*DEBUG*/
		int index = MotionEventCompat.getActionIndex(event);
		int xPos = (int)MotionEventCompat.getX(event, index);
		int yPos = (int)MotionEventCompat.getY(event, index);	
		EventRecorder.getInstance().record(String.format("ACTIONEVENT: %s->%s at (%s,%s)", index, action, xPos, yPos));
		/*DEBUG*/

		switch(action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			handlePointerEvent(event, InputEventTypeEnum.PRESS);
		case MotionEvent.ACTION_MOVE:			
			handlePointerEvent(event, InputEventTypeEnum.MOVE);
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			handlePointerEvent(event, InputEventTypeEnum.RELEASE);
			break;
		}

		return true;
	}
	
	public void handlePointerEvent(MotionEvent event, InputEventTypeEnum eventType) {

		int index = MotionEventCompat.getActionIndex(event);
		
		float xPos = MotionEventCompat.getX(event, index);
		float yPos = MotionEventCompat.getY(event, index);		
		PointF p = new PointF(xPos, yPos);

		/*DEBUG*/
		if(eventType.equals(InputEventTypeEnum.RELEASE)) {
			Log.d(TAG, String.format("Index %s Liftoff at %s,%s", index, p.x, p.y));			
		} else if(eventType.equals(InputEventTypeEnum.PRESS)) {
			Log.d(TAG, String.format("Index %s Touchdown at %s,%s", index, p.x, p.y));
		} 
//		else if(eventType.equals(InputEventTypeEnum.MOVE)) {
//			Log.d(TAG, String.format("PRESSURE at %s,%s: %s", p.x, p.y, event.getPressure(index)));
//		}
		

		synchronized(mRenderer.mPointers) {
			
			if(eventType.equals(InputEventTypeEnum.PRESS)) {
				
				if(mRenderer.mPointers.size() < index) {
					mRenderer.mPointers.set(index, p);
				} else {					
					mRenderer.mPointers.add(index, p);
				}
				
			} else if(eventType.equals(InputEventTypeEnum.MOVE)) {								
				 int count = MotionEventCompat.getPointerCount(event);
				 
				 for(int i = 0; i < count; i++) {
					PointF pMoved = new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i));
					mRenderer.mPointers.set(i, pMoved);
				 }
				
			} else if(eventType.equals(InputEventTypeEnum.RELEASE)) {
				
//				Log.d(TAG, "Pointers contents before release:");
//				for(int i = 0; i < mRenderer.mPointers.size(); i++) {
//					Point target = mRenderer.mPointers.get(i);
//					int realIndex = mRenderer.mPointers.indexOf(target);
//					Log.d(TAG, String.format("Pointer event index %s, Real Index %s, List Position %s", index, realIndex, i));
//				}
				
				mRenderer.mPointers.remove(index);				
				
//				Log.d(TAG, "Pointers contents after release:");
//				for(int i = 0; i < mRenderer.mPointers.size(); i++) {
//					Point target = mRenderer.mPointers.get(i);
//					int realIndex = mRenderer.mPointers.indexOf(target);
//					Log.d(TAG, String.format("Pointer event index %s, Real Index %s, List Position %s", index, realIndex, i));
//				}
				
			}		
		}

	}

}
