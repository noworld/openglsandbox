package com.solesurvivor.simplescroller.game;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.WindowManager;

import com.solesurvivor.simplescroller.R;
import com.solesurvivor.simplescroller.game.states.GameState;
import com.solesurvivor.simplescroller.game.states.GameStateEnum;
import com.solesurvivor.simplescroller.game.states.GameStateManager;
import com.solesurvivor.simplescroller.input.InputEventBus;
import com.solesurvivor.simplescroller.scene.Camera;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.exceptions.NotInitializedException;
import com.solesurvivor.util.logging.SSLog;

public class GameWorld {
	
	private static final String TAG = GameWorld.class.getSimpleName();
	public static final String SEPARATOR = "\\|";
	public static final String NEWLINE = "\r\n";
	public static final String PLUS = "+";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String ARRAY_RESOURCE_TYPE = "array";
	public static final String RESOURCE_PACKAGE = "com.solesurvivor.simplerender2_5";
	private static GameWorld inst;
	
	private Context context;
	private WindowManager windowManager;
	private Map<GlobalKeysEnum,String> values;
	private Point viewport;
	private Camera currentCamera;
	private GameState currentState;
	private GameState previousState;
	
	private long deltaT = 0L;
	private long lastT = SystemClock.uptimeMillis(); 
	private long gameLocalT;

	private GameWorld(Context context, WindowManager windowManager) {
		this.context = context;
		this.windowManager = windowManager;
		Map<GlobalKeysEnum,String> vals = readGlobalConfig();
		values = Collections.unmodifiableMap(vals);
		viewport = new Point(0,0);
		windowManager.getDefaultDisplay().getSize(viewport);
		gameLocalT = (new Date()).getTime();		
	}

	public static GameWorld inst() {
		return inst;		
	}

	public static void init(Context context, WindowManager windowManager) {		
		inst = new GameWorld(context, windowManager);
		GameStateManager.init();		
		SSLog.d(TAG, "GameWorld initialized.");
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
		currentState.resizeViewport(viewport);
		currentState.enter();

		return true;
	}
	
	public boolean revertState() {
		if(previousState != null) {
			return changeState(previousState);
		}
		
		return false;
	}
	
	public void resizeViewport(Point viewport) {
		this.viewport = viewport;
		if(currentState != null) {
			currentState.resizeViewport(viewport);
		}
	}
	
	public Point getViewport() {
		return this.viewport;
	}
	
	public long getDeltaT() {
		return deltaT;
	}
	
	public long getGameT() {
		return gameLocalT;
	}
	
	public String getVal(GlobalKeysEnum key) {
		return values.get(key);
	}
	
	public Boolean getBool(GlobalKeysEnum key) {
		String boolVal = values.get(key);
		if(boolVal == null) return null;
		return Boolean.valueOf(boolVal);
	}
	
	public Context getContext() {
		if(context == null) {
			throw new NotInitializedException();
		}
		return context;
	}
	
	public WindowManager getWindowManager() {
		if(windowManager == null) {
			throw new NotInitializedException();
		}
		
		return windowManager;
	}
	
	private Map<GlobalKeysEnum, String> readGlobalConfig() {
		String[] globals = context.getResources().getStringArray(R.array.global);
		
		Map<String,String> stringKeys = SSPropertyUtil.parseFromStringArray(globals,SEPARATOR);
		
		Map<GlobalKeysEnum,String> enumKeys = new HashMap<GlobalKeysEnum,String>(stringKeys.size());
		
		for(Map.Entry<String,String> entry: stringKeys.entrySet()) {
			enumKeys.put(GlobalKeysEnum.valueOf(entry.getKey()), entry.getValue());
		}
		
		return enumKeys;
	}
	
	public void setCamera(Camera cam) {
		this.currentCamera = cam;
	}
	
	public Camera getCamera() {
		return this.currentCamera;
	}
	
}
