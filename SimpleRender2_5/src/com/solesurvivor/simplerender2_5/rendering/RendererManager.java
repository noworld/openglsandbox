package com.solesurvivor.simplerender2_5.rendering;

import com.solesurvivor.simplerender2_5.SimpleRender25SurfaceView;
import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.util.logging.SSLog;

public class RendererManager {
	
	private static final String TAG = RendererManager.class.getSimpleName();
	
	private static SimpleRender25SurfaceView mGLSurfaceView;
	private static BaseRenderer mRenderer;

	
	public static void init() {
		mGLSurfaceView = new SimpleRender25SurfaceView(GameGlobal.inst().getContext());
		mGLSurfaceView.setEGLContextClientVersion(2);
		mRenderer = new BaseRenderer();
		mGLSurfaceView.setRenderer(mRenderer);
		SSLog.d(TAG, "RendererManager created.");	
	}
	
	public static SimpleRender25SurfaceView getSurfaceView() {
		return mGLSurfaceView;
	}
	
	public static BaseRenderer getRenderer() {
		return mRenderer;
	}
	
}
