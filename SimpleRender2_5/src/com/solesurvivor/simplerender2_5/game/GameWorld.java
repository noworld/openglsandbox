package com.solesurvivor.simplerender2_5.game;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Point;
import android.os.SystemClock;

import com.solesurvivor.simplerender2_5.game.states.GameState;
import com.solesurvivor.simplerender2_5.input.InputEventBus;
import com.solesurvivor.util.logging.SSLog;


public class GameWorld {
	
	private static final String TAG = GameWorld.class.getSimpleName();
	private static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

	private static GameWorld sInstance;

	private GameState mCurrentState;
	private GameState mPreviousState;
	private Point mViewport;
	
	private long mDeltaT = 0L;
	private long mLastT = SystemClock.uptimeMillis(); 
	private long mGameLocalT;

	private GameWorld() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
		try {
			Date d = sdf.parse(GameGlobal.inst().getVal(GlobalKeysEnum.START_DATE_TIME));
			mGameLocalT = d.getTime();
		} catch (ParseException e) {
			SSLog.d(TAG, e.getMessage());
		}		
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
		mGameLocalT = mGameLocalT + mDeltaT;
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

		if(mViewport == null){SSLog.e("VIEWPORT ERROR", "VIEWPORT WAS NULL!!!");}
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
	
	public long getGameT() {
		return mGameLocalT;
	}
	
}
