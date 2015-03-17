package com.solesurvivor.pthirtyeight;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.solesurvivor.pthirtyeight.game.GameGlobal;
import com.solesurvivor.pthirtyeight.input.InputEvent;
import com.solesurvivor.pthirtyeight.input.InputEventBus;
import com.solesurvivor.pthirtyeight.input.InputEventEnum;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.util.logging.SSLog;

public class CopyOfScrollerActivity extends Activity {

	private static final String TAG = CopyOfScrollerActivity.class.getSimpleName();	
	private static final int GL_VERSION = 0x20000;
	
	private static boolean initialized = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		final boolean es2 = activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= GL_VERSION;
		if (es2) {
			Context c = getApplicationContext();
			
			if(!initialized) {
				GameGlobal.init(c);
				initialized = true;
			}
			
			RendererManager.init(c, getWindowManager(), getAssets());
			setContentView(RendererManager.getSurfaceView());					
		}

		SSLog.d(TAG, "Activity created.");
	}
	
	@Override
	public void onBackPressed() {
		InputEventBus.inst().add(new InputEvent(InputEventEnum.BACK_BUTTON, new PointF(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY)));
	}
}
