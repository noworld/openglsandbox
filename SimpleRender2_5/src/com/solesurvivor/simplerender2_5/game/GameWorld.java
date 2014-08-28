package com.solesurvivor.simplerender2_5.game;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.game.states.GameState;
import com.solesurvivor.simplerender2_5.game.states.GameStateEnum;
import com.solesurvivor.simplerender2_5.game.states.GameStateManager;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;


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
			mPreviousState = mCurrentState;
		}
		
		mCurrentState = state;

		mCurrentState.resizeViewport(mViewport);
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
		mViewport = viewport;
		if(mCurrentState != null) {
			mCurrentState.resizeViewport(viewport);
		}
	}
	
}
