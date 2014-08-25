package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Skybox;

public class MainMenuState extends BaseState {

	@SuppressWarnings("unused")
	private static final String TAG = MainMenuState.class.getSimpleName();
	
	protected Camera mCamera;
	
	public MainMenuState() {
		mCamera = new Camera();
		Skybox skybox = new Skybox();
		mScene.addChild(skybox);
	}

	@Override
	public void enter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resizeViewport(Point p) {
		mCamera.resizeViewport(p);
	}
	
}
