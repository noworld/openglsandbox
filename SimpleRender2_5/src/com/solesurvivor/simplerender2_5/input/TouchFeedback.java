package com.solesurvivor.simplerender2_5.input;

import android.content.Context;
import android.os.Vibrator;

import com.solesurvivor.simplerender2_5.game.GameGlobal;

public class TouchFeedback {

	private static final long[] TOUCH_VIB_PATTERN = {10, 50};
		
	private static Vibrator mVibrator;
	
	public static void init() {
		Context context = GameGlobal.inst().getContext();
		mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public static void vibPattern(long[] pattern) {
		if(mVibrator.hasVibrator()) {
//			mVibrator.cancel();
			mVibrator.vibrate(pattern,-1);
		}
	}
	
	public static void touch() {
		vibPattern(TOUCH_VIB_PATTERN);
	}
	
}
