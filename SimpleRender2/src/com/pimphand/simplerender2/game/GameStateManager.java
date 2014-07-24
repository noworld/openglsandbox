package com.pimphand.simplerender2.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pimphand.simplerender2.fsm.GameState;
import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameStateManager {

	private static Map<GameStateEnum,GameState<GameWorld>> sGameStates = null;
	
	public static void init() {
		Map<GameStateEnum,GameState<GameWorld>> states = new HashMap<GameStateEnum,GameState<GameWorld>>();
		
		states.put(GameStateEnum.MAIN_MENU, new MainMenuState());
		states.put(GameStateEnum.WATER_WORLD, new WaterWorldState());
		states.put(GameStateEnum.MODEL_VIEWER, new ModelViewerState());
		
		sGameStates = Collections.unmodifiableMap(states);
	}
	
	public static GameState<GameWorld> getState(GameStateEnum key) {
		if(sGameStates == null) {
			throw new NotInitializedException();
		}
		return sGameStates.get(key);
	}
	
}
