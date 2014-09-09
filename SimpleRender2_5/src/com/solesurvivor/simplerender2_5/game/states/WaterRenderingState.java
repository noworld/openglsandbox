package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class WaterRenderingState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = WaterRenderingState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public WaterRenderingState() {
		Point viewport = GameWorld.inst().getViewport();
		mCamera = new Camera();
		mCamera.resizeViewport(viewport);
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		
		Skybox skybox = new Skybox("skybox_shader", "tenerife_cube");
		mScene.addChild(skybox);
	}

	@Override
	public void enter() {
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		RendererManager.getRenderer().initOpenGLDefault();
	}
	
	@Override
	public void execute() {

	}
	
	@Override
	public void render() {		
		super.render();
	}
	
	@Override
	public void resizeViewport(Point p) {
		mCamera.resizeViewport(p);
	}
	
}
