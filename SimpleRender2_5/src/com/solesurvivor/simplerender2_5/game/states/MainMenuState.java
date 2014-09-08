package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.HeightmapTexture2D;
import com.solesurvivor.simplerender2_5.scene.Plane;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class MainMenuState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public MainMenuState() {
		Point viewport = GameWorld.inst().getViewport();
		mCamera = new Camera();
		mCamera.resizeViewport(viewport);
		RendererManager.getRenderer().setCurrentCamera(mCamera);
		
		Skybox skybox = new Skybox("skybox_shader", "tenerife_cube");
		mScene.addChild(skybox);
		//Creating procedural texture adds it to the manager
//		ProceduralTexture2D texture = new ProceduralTexture2D("hm_shader", "procedural1", mCamera.getViewport());
		HeightmapTexture2D texture = new HeightmapTexture2D("hm_shader", "procedural1", new Point(800,600));
		//Plane will pull the procedural handle
		Plane plane = new Plane("plane_shader", "procedural1", new Point(800,600));
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
