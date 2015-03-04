package com.solesurvivor.pthirtyeight.game;

import android.graphics.Point;
import android.os.SystemClock;

import com.solesurvivor.pthirtyeight.commands.CommandEnum;
import com.solesurvivor.pthirtyeight.game.messaging.GameMessageBus;
import com.solesurvivor.pthirtyeight.game.states.GameState;
import com.solesurvivor.pthirtyeight.game.states.GameStateEnum;
import com.solesurvivor.pthirtyeight.game.states.GameStateManager;
import com.solesurvivor.pthirtyeight.input.InputEventBus;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.pthirtyeight.scene.Camera;
import com.solesurvivor.util.logging.SSLog;

public class GameWorld {
	
	private static final String TAG = GameWorld.class.getSimpleName();
	
	public static final float SCALE = 3.0f;
	
	private static GameWorld inst;

	private Camera currentCamera;
	private GameState currentState;
	private GameState previousState;
	
	private long deltaT = 0L;
	private long lastT = SystemClock.uptimeMillis(); 
	private long gameLocalT;

	private GameWorld() {
		gameLocalT = SystemClock.uptimeMillis();		
	}

	public static GameWorld inst() {
		return inst;		
	}

	public static void init() {		
		if(inst == null) {
			inst = new GameWorld();
			GameStateManager.init();		
			SSLog.d(TAG, "GameWorld initialized.");
		}
	}
	
	public GameState getCurrentState() {
		return currentState;
	}
	
	public void enter() {
		inst.changeState(GameStateManager.getState(GameStateEnum.DEFAULT));
	}
	
	public void update() {
		long tempT = SystemClock.uptimeMillis();
		deltaT = tempT - lastT;
		lastT = tempT;
		gameLocalT = gameLocalT + deltaT;
		InputEventBus.inst().executeCommands(currentState.getInputs());
		GameMessageBus.update();
		this.currentState.execute();
	}

	public void render() {
		this.currentState.render();
	}

	public boolean changeState(GameState state) {

		if(currentState != null) {
			currentState.exit();
			previousState = currentState;
		}
		
		currentState = state;
		currentState.resizeViewport(RendererManager.getViewport());
		currentState.enter();
		
		for(CommandEnum ce : CommandEnum.values()) {
			ce.getCommand().onStateChanged();
		}

		return true;
	}
	
	public boolean revertState() {
		if(previousState != null) {
			return changeState(previousState);
		}
		
		return false;
	}
	
	public void resizeViewport(Point viewport) {
		if(currentState != null) {
			currentState.resizeViewport(viewport);
			SSLog.d(TAG, "State Viewport Resized.");
		}
	}
	
	public long getDeltaT() {
		return deltaT;
	}
	
	public long getGameT() {
		return gameLocalT;
	}
	
	public void setCamera(Camera cam) {
		this.currentCamera = cam;
	}
	
	public Camera getCamera() {
		return this.currentCamera;
	}
	
}
