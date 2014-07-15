package com.pimphand.simplerender2.scene;

import com.pimphand.simplerender2.rendering.RendererManager;

import android.graphics.Point;
import android.opengl.Matrix;


public class GameWorld extends GameObject {

	private static GameWorld sInstance = new GameWorld();

	private Point mViewport = new Point(0,0);
	private float[] mProjectionMatrix = new float[16];
	private float[] mUIMatrix = new float[16];

	private GameWorld() {

	}

	public static GameWorld instance() {
		return sInstance;
	}
	
	@Override
	public boolean update() {
		return super.update();
	}

	@Override
	public boolean render() {		
		return super.render();
	}

	public boolean resizeViewport(Point newViewport) {
		
		RendererManager.instance().getRenderer().resizeViewport(newViewport);
		
		this.mViewport = newViewport;
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) mViewport.x / mViewport.y;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 200.0f;

		final float left_ortho = -(mViewport.x/2);
		final float right_ortho = (mViewport.x/2);
		final float bottom_ortho = -(mViewport.y/2);
		final float top_ortho = (mViewport.y/2);
		final float near_ortho = 1.0f;
		final float far_ortho = 200.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		Matrix.orthoM(mUIMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near_ortho, far_ortho);
		
		return true;
	}

}
