package com.solesurvivor.simplerender2;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.input.InputEvent;
import com.solesurvivor.simplerender2.input.InputEventBus;
import com.solesurvivor.simplerender2.input.InputEventEnum;
import com.solesurvivor.simplerender2.input.TouchFeedback;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.util.logging.SSLog;

public class SimpleRender2Activity extends Activity {

	private static final String TAG = SimpleRender2Activity.class.getSimpleName();	
	private static final int GL_VERSION = 0x20000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		GameGlobal.init(getApplicationContext(), getWindowManager());
		TouchFeedback.init();

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		final boolean es2 = activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= GL_VERSION;
		if (es2) {
			RendererManager.init();			
			setContentView(RendererManager.inst().getSurfaceView());			
		}
		
		SSLog.d(TAG, "Activity created.");
	}

	@Override
	public void onBackPressed() {
		InputEventBus.inst().add(new InputEvent(InputEventEnum.BACK_BUTTON, new PointF(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY)));
	}

}
