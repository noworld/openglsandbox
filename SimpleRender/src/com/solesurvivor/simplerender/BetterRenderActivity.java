package com.solesurvivor.simplerender;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.solesurvivor.simplerender.renderer.RendererManager;
import com.solesurvivor.simplerender.ui.TouchFeedback;
import com.solesurvivor.simplerender.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class BetterRenderActivity extends Activity {
	
	private static final String TAG = BetterRenderActivity.class.getSimpleName();
	
	private static final int GL_VERSION = 0x20000;
	
	private Context mContext;
	private boolean mStarted = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mStarted = false;
		
		Log.d(TAG, "BetterRenderActivity created.");

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mContext = getApplicationContext();
		
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		final boolean es2 = activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= GL_VERSION;
    	if (es2 && !mStarted) {
			// Request an OpenGL ES 2.0 compatible context.
			RendererManager.init(mContext);
			setContentView(RendererManager.getInstance().getSurfaceView());
			TouchFeedback.init(mContext);
			mStarted = true;
    	}
	}
}
