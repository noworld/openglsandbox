package com.solesurvivor.simplescroller;

import android.view.ScaleGestureDetector;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.util.logging.SSLog;

public class ScrollerScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

	private static final String TAG = ScrollerScaleListener.class.getSimpleName();
	
	@Override
    public boolean onScale(ScaleGestureDetector detector) {
		float zoom = detector.getScaleFactor();
		GameWorld.inst().getCurrentState().setZoom(zoom);
		SSLog.d(TAG, String.format("Zoom factor: %.3f", zoom));
        return true;
    }
}
