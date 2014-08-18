package com.solesurvivor.simplerender2.game;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.simplerender2.fsm.GameState;
import com.solesurvivor.simplerender2.input.InputEventBus;

public class GameWorld {

	private static GameWorld sInstance;

	private GameState<GameWorld> mCurrentState;
	private GameState<GameWorld> mPreviousState;
	private Point mViewport = null;
	private float[] mWaveRotMatrix = new float[16];

	private GameWorld() {
		Matrix.setIdentityM(mWaveRotMatrix, 0);
	}

	public static GameWorld inst() {
		return sInstance;
	}

	public static void init() {
		sInstance = new GameWorld();
		sInstance.changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}
	
	public GameState<GameWorld> getCurrentState() {
		return mCurrentState;
	}
	
	public Point getViewport() {
		return mViewport;
	}

	public float[] getUiMatrix() {
		return mCurrentState.getUiMatrix();
	}

	public float[] getViewMatrix() {
		return mCurrentState.getViewMatrix();
	}

	public float[] getProjectionMatrix() {
		return mCurrentState.getProjectionMatrix();
	}
	
	public float[] getAgentViewRotation() {
		return mCurrentState.getAgentViewRotation();
	}
	
	public float[] getAgentViewMatrix() {
		return mCurrentState.getAgentViewMatrix();
	}
	
	public float[] getWaveRotMatrix() {
		return mWaveRotMatrix;
	}
	
	public void setWaveRotMatrix(float[] rot) {
		mWaveRotMatrix = rot;
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
	
	public void resizeViewport(Point point) {
		this.mViewport = point;
		this.mCurrentState.resizeViewport(point);
	}

	public void translateView(float x, float y, float z) {
		this.mCurrentState.translateView(x, y, z);
	}

	public void rotateView(float angle, float x, float y, float z) {
		this.mCurrentState.rotateView(angle, x, y, z);
	}
	
	public void impulseView(float x, float y, float z) {		
		this.mCurrentState.impulseView(x, y, z);
	}
	
}
