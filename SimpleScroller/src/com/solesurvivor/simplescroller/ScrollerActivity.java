package com.solesurvivor.simplescroller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.solesurvivor.simplescroller.game.GameGlobal;
import com.solesurvivor.simplescroller.input.InputEvent;
import com.solesurvivor.simplescroller.input.InputEventBus;
import com.solesurvivor.simplescroller.input.InputEventEnum;
import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.util.logging.SSLog;

public class ScrollerActivity extends Activity {

	private static final String TAG = ScrollerActivity.class.getSimpleName();	
	private static final int GL_VERSION = 0x20000;
	
	private static boolean initialized = false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(!initialized) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

			final boolean es2 = activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= GL_VERSION;
			if (es2) {
				Context c = getApplicationContext();
				GameGlobal.init(c);
				RendererManager.init(c, getWindowManager(), getAssets());
				setContentView(RendererManager.getSurfaceView());					
			}
			
			initialized = true;
		}
		
		SSLog.d(TAG, "Activity created.");
	}
	
	@Override
	public void onBackPressed() {
		InputEventBus.inst().add(new InputEvent(InputEventEnum.BACK_BUTTON, new PointF(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY)));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
