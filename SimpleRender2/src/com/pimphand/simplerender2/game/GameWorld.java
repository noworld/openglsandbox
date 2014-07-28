package com.pimphand.simplerender2.game;

import android.graphics.Point;

import com.pimphand.simplerender2.fsm.GameState;
import com.pimphand.simplerender2.input.InputEventBus;
import com.pimphand.simplerender2.scene.Camera;

public class GameWorld {

	private static GameWorld sInstance;

	private Camera mCamera;
	private GameState<GameWorld> mCurrentState;
	private GameState<GameWorld> mPreviousState;

	private GameWorld() {
		this.mCamera = new Camera();
	}

	public static GameWorld inst() {
		return sInstance;
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	
	public static void init() {
		sInstance = new GameWorld();
		sInstance.changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}
	
	public void update() {

		InputEventBus.inst().executeCommands(mCurrentState.getLibrary().mInputHandlers);		
		this.mCurrentState.execute(this);
	}

	public void render() {
		this.mCurrentState.render(this);
	}

	public boolean changeState(GameState<GameWorld> state) {

		if(mCurrentState != null) {
			mCurrentState.exit(this);
			this.mPreviousState = mCurrentState;
		}
		
		mCurrentState = state;
		mCurrentState.enter(this);

		return true;

	}
	
	public boolean revertState() {
		return changeState(mPreviousState);
	}
	
	public void resizeViewport(Point point) {
		this.mCamera.resizeViewport(point);
	}
	
}
