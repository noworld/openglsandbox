package com.solesurvivor.simplescroller.rendering;

import android.content.Context;

import com.solesurvivor.simplescroller.ScrollerSurfaceView;
import com.solesurvivor.util.logging.SSLog;

public class RendererManager {
	
	private static final String TAG = RendererManager.class.getSimpleName();
	
	protected static ScrollerSurfaceView glSurfaceView;
	protected static ScrollerRenderer renderer;
	
	private RendererManager() {
		
	}

	public static void init(Context context) {
		glSurfaceView = new ScrollerSurfaceView(context);
		glSurfaceView.setEGLContextClientVersion(2);
		renderer = new ScrollerRenderer();
		glSurfaceView.setRenderer(renderer);
		SSLog.d(TAG, "RendererManager initialized.");	
	}

	public static ScrollerSurfaceView getSurfaceView() {
		return glSurfaceView;
	}
	
	public static BaseRenderer getRenderer() {
		return renderer;
	}
	
}
