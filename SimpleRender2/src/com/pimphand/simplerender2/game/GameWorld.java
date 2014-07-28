package com.pimphand.simplerender2.game;

import android.graphics.Point;

import com.pimphand.simplerender2.fsm.GameState;
import com.pimphand.simplerender2.input.InputEventBus;
import com.pimphand.simplerender2.scene.Camera;

public class GameWorld {

	private static GameWorld sInstance;

	private GameState<GameWorld> mCurrentState;
	private GameState<GameWorld> mPreviousState;
	private Point mViewport = null;

	private GameWorld() {
		
	}

	public static GameWorld inst() {
		return sInstance;
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
		if(mViewport != null) {
			mCurrentState.resizeViewport(mViewport);
		}
		mCurrentState.enter(this);

		return true;

	}
	
	public boolean revertState() {
		return changeState(mPreviousState);
	}
	
	public Camera getCamera() {
		return this.mCurrentState.getCamera();
	}
	
	public void resizeViewport(Point point) {
		this.mViewport = point;
		this.mCurrentState.resizeViewport(point);
	}
	
}
