package com.solesurvivor.simplerender2_5.rendering;

import java.util.Stack;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.SimpleRender25SurfaceView;
import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.util.logging.SSLog;

public class RendererManager {
	
	private static final String TAG = RendererManager.class.getSimpleName();
	
	private static SimpleRender25SurfaceView mGLSurfaceView;
	private static BaseRenderer mRenderer;
	private static Stack<float[]> matrixStack;

	
	public static void init() {
		mGLSurfaceView = new SimpleRender25SurfaceView(GameGlobal.inst().getContext());
		mGLSurfaceView.setEGLContextClientVersion(2);
		mRenderer = new GridMapRenderer();
		mGLSurfaceView.setRenderer(mRenderer);
		SSLog.d(TAG, "RendererManager created.");
		
		matrixStack = new Stack<float[]>();
		float[] ident = new float[16];
		Matrix.setIdentityM(ident, 0);
		matrixStack.push(ident);
	}
	
	public static SimpleRender25SurfaceView getSurfaceView() {
		return mGLSurfaceView;
	}
	
	public static BaseRenderer getRenderer() {
		return mRenderer;
	}
	
	public static GridMapRenderer getGridMapRenderer() {
		return (GridMapRenderer)mRenderer;
	}
	
	public static void pushMatrix(float[] matrix) {
		matrixStack.push(matrix);
	}
	
	public static void pushMatrixMultiply(float[] matrix) {
		float[] mult = new float[16];
		Matrix.multiplyMM(mult, 0, matrixStack.peek(), 0, matrix, 0);
		matrixStack.push(mult);
	}
	
	public float[] peekMatrix() {
		return matrixStack.peek();
	}
	
	public float[] popMatrix() {
		return matrixStack.pop();
	}
	
}
