package com.solesurvivor.simplescroller.game.states;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.solesurvivor.util.exceptions.NotInitializedException;

public class GameStateManager {

	private static Map<GameStateEnum,GameState> gameStates = null;
	
	public static void init() {
		Map<GameStateEnum,GameState> states = new HashMap<GameStateEnum,GameState>();
		
		states.put(GameStateEnum.DEFAULT, new MainMenuState());
		states.put(GameStateEnum.MAIN_MENU, new MainMenuState());
		states.put(GameStateEnum.PLAY, new PlayState());

		gameStates = Collections.unmodifiableMap(states);
	}
	
	public static GameState getState(GameStateEnum key) {
		if(gameStates == null) {
			throw new NotInitializedException();
		}
		return gameStates.get(key);
	}
	
}
