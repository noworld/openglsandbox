package com.solesurvivor.simplescroller.rendering;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.view.WindowManager;

import com.solesurvivor.simplescroller.ScrollerSurfaceView;
import com.solesurvivor.util.exceptions.NotInitializedException;
import com.solesurvivor.util.logging.SSLog;

public class RendererManager {
	
	private static final String TAG = RendererManager.class.getSimpleName();
	
	
	protected static ScrollerSurfaceView glSurfaceView;
	protected static ScrollerRenderer renderer;
	protected static WindowManager windowManager;	
	protected static Context context;
	protected static AssetManager assets;
	protected static Point viewport;
	
	private RendererManager() {
		
	}

	public static void init(Context ctx, WindowManager wm, AssetManager assetManager) {
		glSurfaceView = new ScrollerSurfaceView(ctx);
		glSurfaceView.setEGLContextClientVersion(2);
		renderer = new ScrollerRenderer();
		glSurfaceView.setRenderer(renderer);
		windowManager = wm;
		context = ctx;
		viewport = new Point(0,0);
		windowManager.getDefaultDisplay().getSize(viewport);
		assets = assetManager;
		SSLog.d(TAG, "RendererManager initialized.");	
	}

	public static ScrollerSurfaceView getSurfaceView() {
		if(glSurfaceView == null) {
			throw new NotInitializedException();
		}
		
		return glSurfaceView;
	}
	
	public static ScrollerRenderer getRenderer() {
		if(renderer == null) {
			throw new NotInitializedException();
		}
		
		return renderer;
	}
	
	public static WindowManager getWindowManager() {
		if(windowManager == null) {
			throw new NotInitializedException();
		}
		
		return windowManager;
	}
	
	public static Context getContext() {
		if(context == null) {
			throw new NotInitializedException();
		}
		
		return context;
	}
	
	public static Point getViewport() {
		if(viewport== null) {
			throw new NotInitializedException();
		}
		
		return viewport;
	}

	public static AssetManager getAssets() {
		return assets;
	}
	
}
