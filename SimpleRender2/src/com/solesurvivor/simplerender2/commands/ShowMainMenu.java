package com.solesurvivor.simplerender2.commands;

import android.util.Log;

import com.solesurvivor.simplerender2.game.GameStateEnum;
import com.solesurvivor.simplerender2.game.GameStateManager;
import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.input.InputEvent;

public class ShowMainMenu implements Command {
	
	private static final String TAG = ShowMainMenu.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Changing game world state to Main Menu.");
		GameWorld.inst().changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}

}