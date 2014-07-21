package com.pimphand.simplerender2.scene;

import java.util.List;

import com.pimphand.simplerender2.fsm.MainMenuState;
import com.pimphand.simplerender2.fsm.State;
import com.pimphand.simplerender2.game.GameGlobal;
import com.pimphand.simplerender2.loading.GeometryLoader;
import com.pimphand.simplerender2.rendering.GlSettings;
import com.pimphand.simplerender2.rendering.RendererManager;

public class GameWorld {

	private static GameWorld sInstance = new GameWorld();

	private Camera mCamera;
	private GlSettings mGlSettings;
	private State<GameWorld> mCurrentState;
	private State<GameWorld> mPreviousState;

	private GameWorld() {
		this.mCamera = new Camera();
		this.mGlSettings = new GlSettings();		
		
		State<GameWorld> startingState = new MainMenuState();
		this.mCurrentState = startingState;
		this.mPreviousState = startingState;		
	}

	public static GameWorld instance() {
		return sInstance;
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	
	public void init() {
		RendererManager.instance().getRenderer().initOpenGL(mGlSettings);
	}
	
	public void update() {
		this.mCurrentState.execute(this);
	}

	public void render() {		
		
	}

	private void renderUi() {
		//TODO: mCurrentState.getui
	}
	
}
