package com.solesurvivor.pthirtyeight;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.solesurvivor.util.logging.SSLog;

public class ScrollerGestureListener implements GestureDetector.OnGestureListener {
	
	private static final String TAG = ScrollerGestureListener.class.getSimpleName();

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
			float dy) {
		
		SSLog.d(TAG,String.format("Scrolling: %.2f, %.2f", dx, dy));
		//ScrollerWorld.inst().scrollCamera(-dx/100.0f, -dy/100.0f);
		
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
