package com.solesurvivor.simplerender2.rendering;

import com.solesurvivor.simplerender2.SimpleRender2SurfaceView;
import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.util.logging.SSLog;

public class RendererManager {
	
	private static final String TAG = RendererManager.class.getSimpleName();

	private static RendererManager sInstance = null;
	
	private SimpleRender2SurfaceView mGLSurfaceView;
	private BaseRenderer mRenderer;
	
	private RendererManager() {
		mGLSurfaceView = new SimpleRender2SurfaceView(GameGlobal.inst().getContext());
		mGLSurfaceView.setEGLContextClientVersion(2);
		mRenderer = new BaseRenderer();
		mGLSurfaceView.setRenderer(mRenderer);
		SSLog.d(TAG, "RendererManager created.");
	}
	
	public static void init() {
		sInstance = new RendererManager();		
	}
	
	public SimpleRender2SurfaceView getSurfaceView() {
		return mGLSurfaceView;
	}
	
	public BaseRenderer getRenderer() {
		return mRenderer;
	}
	
	public static RendererManager inst() {
		return sInstance;
	}
	
}
