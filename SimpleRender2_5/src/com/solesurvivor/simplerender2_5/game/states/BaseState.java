package com.solesurvivor.simplerender2_5.game.states;

import java.util.ArrayList;
import java.util.List;

import android.os.SystemClock;

import com.solesurvivor.simplerender2_5.input.InputEventBus;
import com.solesurvivor.simplerender2_5.input.InputHandler;
import com.solesurvivor.simplerender2_5.scene.Scene;


public abstract class BaseState implements GameState {

	@SuppressWarnings("unused")
	private static final String TAG = BaseState.class.getSimpleName();
	
	protected Long mDeltaT;
	protected Long mLastT = SystemClock.uptimeMillis();
	protected Scene mScene = new Scene();
	protected List<InputHandler> mInputHandlers = new ArrayList<InputHandler>();

	@Override
	public void execute() {
		long tempT = SystemClock.uptimeMillis();
		mDeltaT = tempT - mLastT;
		mLastT = tempT;
		mScene.update();
	}

	@Override
	public void render() {
		mScene.render();
	}

	@Override
	public void exit() {
		for(InputHandler ih : mInputHandlers) {
			ih.quiet();
		}
		//Clear input events when leaving
		InputEventBus.inst().clear();
	}
	
	@Override
	public long getDeltaT() {
		return mDeltaT;
	}
	
	@Override
	public List<InputHandler> getInputs() {
		return mInputHandlers;
	}

}
