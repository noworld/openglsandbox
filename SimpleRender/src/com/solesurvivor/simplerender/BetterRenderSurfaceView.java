package com.solesurvivor.simplerender;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
import com.solesurvivor.simplerender.ui.InputEventTypeEnum;

public class BetterRenderSurfaceView extends GLSurfaceView {

	private static final int MOVE_EVENT_BLUR_SQ = 16;
	
	//XXX DEBUG - hold an array of recent events
	SparseArray<String> sa = new SparseArray<String>();
	int max = 10000;
	int ctr = 0;
	private void record(String s) {
		sa.append(ctr++, s);
		if(ctr > max) ctr = 0;
	}
	
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
		record(String.format("ACTIONEVENT: %s->%s at (%s,%s)", index, action, xPos, yPos));
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
		
		//TODO: Why am I casting this to int???
		int xPos = (int)MotionEventCompat.getX(event, index);
		int yPos = (int)MotionEventCompat.getY(event, index);		
		Point p = new Point(xPos, yPos);

		/*DEBUG*/
		if(eventType.equals(InputEventTypeEnum.RELEASE)) {
			Log.d(TAG, String.format("Liftoff at %s,%s", p.x, p.y));
			Log.d(TAG, String.format("Record size: %s", sa.size()));
		} else if(eventType.equals(InputEventTypeEnum.PRESS)) {
			Log.d(TAG, String.format("Touchdown at %s,%s", p.x, p.y));
		} 
//		else if(eventType.equals(InputEventTypeEnum.MOVE)) {
//			Log.d(TAG, String.format("PRESSURE at %s,%s: %s", p.x, p.y, event.getPressure(index)));
//		}
		
		
		//XXX HACK: Only handle move events if it moves a certain distance
//		if(eventType.equals(InputEventTypeEnum.MOVE)) {
//			Point oldP = mRenderer.mPointers.get(index);
//			if(oldP != null) {
//				int a = oldP.x - xPos;
//				int b = oldP.y - yPos;
//				if( ((a*a) + (b*b)) < MOVE_EVENT_BLUR_SQ ) return;
//			}
//		}

		synchronized(mRenderer.mPointers) {
			
			//XXX HACK: Sometimes a button can get stuck...
			if(MotionEventCompat.getPointerCount(event) < mRenderer.mPointers.size()) {
				Log.e(TAG, String.format("More pointers than events: %s:%s", 
						MotionEventCompat.getPointerCount(event),
						mRenderer.mPointers.size()));
				mRenderer.mPointers.clear();
				for(int i = 0; i < sa.size(); i++) {
					String s = sa.get(i);
					Log.d(TAG, String.format("Record: %s", s));
				}
			}
			
			if(eventType.equals(InputEventTypeEnum.RELEASE) && mRenderer.mPointers.containsKey(index)) {
				mRenderer.mPointers.remove(index);
			} else {
				mRenderer.mPointers.put(index,p);
			}
		}

	}

}
