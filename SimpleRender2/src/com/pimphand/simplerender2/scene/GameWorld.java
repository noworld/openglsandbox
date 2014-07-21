package com.pimphand.simplerender2.scene;

import android.graphics.Point;

import com.pimphand.simplerender2.fsm.MainMenuState;
import com.pimphand.simplerender2.fsm.State;
import com.pimphand.simplerender2.rendering.BaseRenderer;
import com.pimphand.simplerender2.rendering.GlSettings;
import com.pimphand.simplerender2.rendering.RendererManager;
import com.pimphand.simplerender2.ui.InputUiElement;
import com.pimphand.simplerender2.ui.UiElement;

public class GameWorld {

	private static GameWorld sInstance;

	private Camera mCamera;
	private GlSettings mGlSettings;
	private State<GameWorld> mCurrentState;
	private State<GameWorld> mPreviousState;
	private boolean mDrawInputAreas = false;

	private GameWorld() {
		this.mCamera = new Camera();
		this.mGlSettings = new GlSettings();
		
		RendererManager.instance().getRenderer().initOpenGL(mGlSettings);
		
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
	
	public static void init() {
		sInstance = new GameWorld();		
	}
	
	public void update() {
		this.mCurrentState.execute(this);
	}

	public void render() {		
		renderUi();
	}

	public boolean changeState(State<GameWorld> state) {

		mCurrentState.exit(this);

		this.mPreviousState = mCurrentState;
		mCurrentState = state;

		mCurrentState.enter(this);

		return true;

	}
	
	public boolean revertState() {
		return changeState(mPreviousState);
	}

	private void renderUi() {
		
		BaseRenderer ren = RendererManager.instance().getRenderer();
		
		for(UiElement ui : mCurrentState.getLibrary().mDisplayElements) {
			ren.drawUI(ui.getGeometry());
		}
		
		if(mDrawInputAreas) {
			for(InputUiElement iui : mCurrentState.getLibrary().mInputElements) {
				ren.drawUI(iui.getGeometry());
			}
		}
	}

	public void resizeViewport(Point point) {
		this.mCamera.resizeViewport(point);
		
		for(UiElement ui : this.mCurrentState.getLibrary().mDisplayElements) {
			ui.reposition();
		}
		
		for(InputUiElement iui : this.mCurrentState.getLibrary().mInputElements) {
			iui.reposition();
		}
	}
	
}
