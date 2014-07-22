package com.pimphand.simplerender2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.pimphand.simplerender2.input.InputEvent;
import com.pimphand.simplerender2.input.InputEventBus;
import com.pimphand.simplerender2.input.InputEventEnum;
import com.pimphand.simplerender2.util.UiUtil;

public class SimpleRender2SurfaceView extends GLSurfaceView {

	private static final String TAG = SimpleRender2SurfaceView.class.getSimpleName();
	
	private List<PointF> mPointers = Collections.synchronizedList(new LinkedList<PointF>());

	public SimpleRender2SurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		Log.d(TAG, "GLSurfaceView created.");
	}

	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		int action = MotionEventCompat.getActionMasked(event);

		switch(action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			handlePointerEvent(event, InputEventEnum.DOWN);
		case MotionEvent.ACTION_MOVE:			
			handlePointerEvent(event, InputEventEnum.MOVE);
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			handlePointerEvent(event, InputEventEnum.UP);
			break;
		}

		return true;
	}

	public void handlePointerEvent(MotionEvent event, InputEventEnum eventType) {

		int index = MotionEventCompat.getActionIndex(event);

		float xPos = MotionEventCompat.getX(event, index);
		float yPos = MotionEventCompat.getY(event, index);		
		PointF p = new PointF(xPos, yPos);

		if(eventType.equals(InputEventEnum.DOWN)) {

			if(mPointers.size() < index) {
				//If the pointer is already down
				//Then just track it in the list
				mPointers.set(index, p);
			} else {					
				//If this is a new down event
				//Send it through the bus
				mPointers.add(index, p);	
				InputEventBus.inst().add(new InputEvent(InputEventEnum.DOWN, UiUtil.screenToViewCoords(p)));
			}

		} else if(eventType.equals(InputEventEnum.MOVE)) {								
			int count = MotionEventCompat.getPointerCount(event);

			for(int i = 0; i < count; i++) {
				PointF pMoved = new PointF(MotionEventCompat.getX(event, i), MotionEventCompat.getY(event, i));						
				PointF pOld = mPointers.get(i);				
				mPointers.set(i, pMoved);
				
				//If the pointers are moving, then release and repress
				if(pOld.x != pMoved.x && pOld.y != pMoved.y) {
					InputEventBus.inst().add(new InputEvent(InputEventEnum.MOVE_OFF, UiUtil.screenToViewCoords(pOld)));
					InputEventBus.inst().add(new InputEvent(InputEventEnum.MOVE_ON, UiUtil.screenToViewCoords(pMoved)));
				}
			}

		} else if(eventType.equals(InputEventEnum.UP)) {
			mPointers.remove(index);
			//Release
			InputEventBus.inst().add(new InputEvent(InputEventEnum.UP, UiUtil.screenToViewCoords(p)));
		}

	}

}
