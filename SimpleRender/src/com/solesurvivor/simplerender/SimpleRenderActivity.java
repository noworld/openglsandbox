package com.solesurvivor.simplerender;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.solesurvivor.simplerender.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SimpleRenderActivity extends Activity {
	
	private static final String TAG = SimpleRenderActivity.class.getSimpleName();
	
	private Context mContext;
	private GLSurfaceView mGLSurfaceView;
	private GLSurfaceView.Renderer mRenderer;
	boolean mStarted = false;
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mSavedInstanceState = savedInstanceState;
		this.mStarted = false;

		setContentView(R.layout.activity_simple_render);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@SuppressWarnings("unused")
					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.startButton).setOnTouchListener(
				mDelayHideTouchListener);
		
		mContext = getApplicationContext();
		mGLSurfaceView = new SimpleRenderSurfaceView(mContext);
		
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		final Spinner spinner = (Spinner)findViewById(R.id.rendererSpinner);
		List<String> spinnerValues = new ArrayList<String>();
		spinnerValues.add(RendererType.BETTER_UI.toString());
		spinnerValues.add(RendererType.TWO_D_UI.toString());
		spinnerValues.add(RendererType.ALPHA_PNG.toString());
		spinnerValues.add(RendererType.MULTI_MODEL.toString());
		spinnerValues.add(RendererType.PACKED_ARRAY_ZIP.toString());
		spinnerValues.add(RendererType.PACKED_ARRAY.toString());
		spinnerValues.add(RendererType.SIMPLE.toString());
		spinnerValues.add(RendererType.TEXTURE.toString());
		spinnerValues.add(RendererType.VBO.toString());
		spinnerValues.add(RendererType.IBO.toString());		

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerValues);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
		
		final Spinner models = (Spinner)findViewById(R.id.modelSpinner);
		List<String> modelvals = new ArrayList<String>();
		modelvals.add("monkey");
		modelvals.add("ncfb");
		modelvals.add("sphere");
		modelvals.add("toruscone");

		ArrayAdapter<String> modela = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, modelvals);
		modela.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		models.setAdapter(modela);

		final Button startButton = (Button)findViewById(R.id.startButton);
		RendererFactory.setContext(mContext);

		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
            	boolean es2 = activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
            	if (es2 && !mStarted) {
        			// Request an OpenGL ES 2.0 compatible context.
        			mGLSurfaceView.setEGLContextClientVersion(2);
        			// Set the renderer to our demo renderer, defined below.
        			RendererType rt = RendererType.valueOf(spinner.getSelectedItem().toString());
        			String model = models.getSelectedItem().toString();
        			mRenderer = RendererFactory.getRenderer(rt, model);
        			mGLSurfaceView.setRenderer(mRenderer);
        			setContentView(mGLSurfaceView);
        			mStarted = true;
        		} else {
        			Log.w(TAG, String.format("OpenGL ES 2.0 not supported (%s) or renderer already started (%s).", es2, mStarted));
        		}
            }
        });		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}


	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	@Override
	public void onBackPressed() {
		setContentView(R.layout.activity_simple_render);
		this.onCreate(mSavedInstanceState);
	}
}
