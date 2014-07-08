package com.solesurvivor.simplerender;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
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
	private GLSurfaceView mGLSurfaceView;
	private GLSurfaceView.Renderer mRenderer;
	private boolean mStarted = false;
	
	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mSavedInstanceState = savedInstanceState;
		this.mStarted = false;
		
		Log.d(TAG, "BetterRenderActivity created.");

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mContext = getApplicationContext();
		mGLSurfaceView = new SimpleRenderSurfaceView(mContext);
		
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		final boolean es2 = activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= GL_VERSION;
    	if (es2 && !mStarted) {
			// Request an OpenGL ES 2.0 compatible context.
			mGLSurfaceView.setEGLContextClientVersion(2);
			mRenderer = new BetterUiGLTextureRenderer(mContext);
			mGLSurfaceView.setRenderer(mRenderer);
			setContentView(mGLSurfaceView);
			mStarted = true;
    	}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	public void onBackPressed() {
		this.onCreate(mSavedInstanceState);
	}
}
