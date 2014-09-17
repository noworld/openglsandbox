package com.solesurvivor.simplerender2_5.game;

import android.graphics.Point;
import android.os.SystemClock;

import com.solesurvivor.simplerender2_5.game.states.GameState;
import com.solesurvivor.simplerender2_5.input.InputEventBus;


public class GameWorld {

	private static GameWorld sInstance;

	private GameState mCurrentState;
	private GameState mPreviousState;
	private Point mViewport;
	
	private long mDeltaT = 0L;
	private long mLastT = SystemClock.uptimeMillis(); 

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
		long tempT = SystemClock.uptimeMillis();
		mDeltaT = tempT - mLastT;
		mLastT = tempT;
		InputEventBus.inst().executeCommands(mCurrentState.getInputs());
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
	
	public long getDeltaT() {
		return mDeltaT;
	}
	
}
