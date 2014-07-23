package com.pimphand.simplerender2.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pimphand.simplerender2.fsm.State;
import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameStateManager {

	private static Map<GameStateEnum,State<GameWorld>> sGameStates = null;
	
	public static void init() {
		Map<GameStateEnum,State<GameWorld>> states = new HashMap<GameStateEnum,State<GameWorld>>();
		
		states.put(GameStateEnum.MAIN_MENU, new MainMenuState());
		states.put(GameStateEnum.WATER_WORLD, new WaterWorldState());
		states.put(GameStateEnum.MODEL_VIEWER, new ModelViewerState());
		
		sGameStates = Collections.unmodifiableMap(states);
	}
	
	public static State<GameWorld> getState(GameStateEnum key) {
		if(sGameStates == null) {
			throw new NotInitializedException();
		}
		return sGameStates.get(key);
	}
	
}
