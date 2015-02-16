package com.solesurvivor.simplerender2_5.game.states;

import java.util.ArrayList;
import java.util.List;

import android.os.SystemClock;

import com.solesurvivor.simplerender2_5.input.InputEventBus;
import com.solesurvivor.simplerender2_5.input.InputHandler;
import com.solesurvivor.simplerender2_5.input.InputUiElement;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.Scene;


public abstract class BaseState implements GameState {
	
	public static final boolean RENDER_INPUT_AREAS = false;

	@SuppressWarnings("unused")
	private static final String TAG = BaseState.class.getSimpleName();
	
	protected Long mDeltaT;
	protected Long mLastT = SystemClock.uptimeMillis();
	protected Scene mScene = new Scene();
	protected List<InputHandler> mInputHandlers = new ArrayList<InputHandler>();
	protected List<Geometry> mUi = new ArrayList<Geometry>();

	@Override
	public void execute() {
		long tempT = SystemClock.uptimeMillis();
		mDeltaT = tempT - mLastT;
		mLastT = tempT;
		InputEventBus.inst().executeCommands(mInputHandlers);
		mScene.update();
	}

	@Override
	public void render() {
		mScene.render();
		for(Geometry g : mUi) {
			RendererManager.getRenderer().drawUI(g);
		}
		
		if(RENDER_INPUT_AREAS) {
			for(InputHandler ih : mInputHandlers) {
				if(ih instanceof InputUiElement) {
					RendererManager.getRenderer().drawUI(((InputUiElement)ih).getGeometry());
				}
			}
		}
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
	
	@Override
	public List<Geometry> getUiElements() {
		return mUi;
	}

}
