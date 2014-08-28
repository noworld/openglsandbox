package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Plane;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class MainMenuState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public MainMenuState() {
		mCamera = new Camera();
		
		Skybox skybox = new Skybox("skybox_shader", "tenerife_cube");
		mScene.addChild(skybox);
		
		Plane plane = new Plane("plane_shader","dpad");
		mScene.addChild(plane);
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
