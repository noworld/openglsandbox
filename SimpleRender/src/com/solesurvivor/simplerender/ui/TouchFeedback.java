package com.solesurvivor.simplerender.ui;

import android.content.Context;
import android.os.Vibrator;

public class TouchFeedback {
	
	private static final long[] SHORT_VIB = {10, 100};

	private static TouchFeedback sInstance = null;
	
	private Context mContext;
	private Vibrator mVibrator;
	
	private TouchFeedback(Context context) {
		this.mContext = context;
		this.mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public static TouchFeedback instance() {
		return sInstance;
	}
	
	public static void init(Context context) {
		sInstance = new TouchFeedback(context);
	}
	
	public void shortVib() {
		if(mVibrator.hasVibrator()) {
			mVibrator.cancel();
			mVibrator.vibrate(SHORT_VIB,-1);
		}
	}
	
	
	
}
