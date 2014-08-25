package com.solesurvivor.simplerender2_5.game;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.game.states.GameState;
import com.solesurvivor.simplerender2_5.game.states.GameStateEnum;
import com.solesurvivor.simplerender2_5.game.states.GameStateManager;


public class GameWorld {

	private static GameWorld sInstance;

	private GameState mCurrentState;
	private GameState mPreviousState;
	private Point mViewport;

	private GameWorld() {

	}

	public static GameWorld inst() {
		return sInstance;
	}

	public static void init() {
		sInstance = new GameWorld();
		sInstance.changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}
	
	public GameState getCurrentState() {
		return mCurrentState;
	}
	
	public void update() {	
		this.mCurrentState.execute();
	}

	public void render() {
		this.mCurrentState.render();
	}

	public boolean changeState(GameState state) {

		if(mCurrentState != null) {
			mCurrentState.exit();
			this.mPreviousState = mCurrentState;
		}
		
		mCurrentState = state;

		mCurrentState.enter();

		return true;
	}
	
	public boolean revertState() {
		return changeState(mPreviousState);
	}
	
	public Point getViewport() {
		return mViewport;
	}
	
	public void resizeViewport(Point viewport) {
		this.mViewport = viewport;
	}
	
}
