package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Plane_16_9;
import com.solesurvivor.simplerender2_5.scene.ProceduralTexture_16_9;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class MainMenuState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public MainMenuState() {
		mCamera = new Camera();
		
		Skybox skybox = new Skybox("skybox_shader", "tenerife_cube");
		mScene.addChild(skybox);
		
		//Creating procedural texture adds it to the manager
		ProceduralTexture_16_9 texture = new ProceduralTexture_16_9("tex_shader", "procedural1");
		//Plane will pull the procedural handle
		Plane_16_9 plane = new Plane_16_9("plane_shader", "procedural1");
		//Add plane as a child of the texture so tex renders first
		texture.addChild(plane);
		//add texture to the scene
		mScene.addChild(texture);
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
