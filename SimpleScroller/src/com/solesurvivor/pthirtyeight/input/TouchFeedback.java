package com.solesurvivor.pthirtyeight.input;

import android.content.Context;
import android.os.Vibrator;

import com.solesurvivor.pthirtyeight.rendering.RendererManager;

public class TouchFeedback {

	private static final long[] TOUCH_VIB_PATTERN = {10, 50};
		
	private static Vibrator vibrator;
	
	public static void init() {
		Context context = RendererManager.getContext();
		vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public static void vibPattern(long[] pattern) {
		if(vibrator.hasVibrator()) {
//			mVibrator.cancel();
			vibrator.vibrate(pattern,-1);
		}
	}
	
	public static void touch() {
		vibPattern(TOUCH_VIB_PATTERN);
	}
	
}
