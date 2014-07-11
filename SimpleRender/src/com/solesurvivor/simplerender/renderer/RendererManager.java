package com.solesurvivor.simplerender.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.solesurvivor.simplerender.BetterRenderSurfaceView;

public class RendererManager {
	
	private static final String TAG = RendererManager.class.getSimpleName();

	private static RendererManager mInstance;
	
	private GLSurfaceView mGLSurfaceView;
	private GLSurfaceView.Renderer mRenderer;
	private Context mContext;	
	
	private RendererManager(Context context) {
		this.mContext = context;
		mGLSurfaceView = new BetterRenderSurfaceView(mContext);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mRenderer = new BetterUiGLTextureRenderer(mContext);
		mGLSurfaceView.setRenderer(mRenderer);
		Log.d(TAG, "RendererManager created.");
	}
	
	public GLSurfaceView getSurfaceView() {
		return mGLSurfaceView;
	}
	
	public GLSurfaceView.Renderer getRenderer() {
		return mRenderer;
	}
	
	public static void init(Context context) {
		mInstance = new RendererManager(context);
	}
	
	public static RendererManager getInstance() {
		return mInstance;
	}
	
}
