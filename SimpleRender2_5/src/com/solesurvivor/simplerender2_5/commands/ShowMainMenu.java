package com.solesurvivor.simplerender2_5.commands;

import android.util.Log;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.game.states.GameStateEnum;
import com.solesurvivor.simplerender2_5.game.states.GameStateManager;
import com.solesurvivor.simplerender2_5.input.InputEvent;

public class ShowMainMenu implements Command {
	
	private static final String TAG = ShowMainMenu.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Changing game world state to Main Menu.");
		GameWorld.inst().changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}

	@Override
	public void onStateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void release(InputEvent event) {
		// TODO Auto-generated method stub
		
	}

}
