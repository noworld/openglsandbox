package com.solesurvivor.simplerender2_5.game.states;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameStateManager {

	private static Map<GameStateEnum,GameState> sGameStates = null;
	
	public static void init() {
		Map<GameStateEnum,GameState> states = new HashMap<GameStateEnum,GameState>();
		
		states.put(GameStateEnum.MAIN_MENU, new MainMenuState());
		states.put(GameStateEnum.WATER_RENDERING, new WaterRenderingState());
		states.put(GameStateEnum.SKY_GRADIENT, new SkyGradientState());
		states.put(GameStateEnum.GRID_MAP, new GridMapState());

		sGameStates = Collections.unmodifiableMap(states);
	}
	
	public static GameState getState(GameStateEnum key) {
		if(sGameStates == null) {
			throw new NotInitializedException();
		}
		return sGameStates.get(key);
	}
	
}
