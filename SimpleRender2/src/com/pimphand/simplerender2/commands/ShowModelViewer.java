package com.pimphand.simplerender2.commands;

import android.util.Log;

import com.pimphand.simplerender2.game.GameStateEnum;
import com.pimphand.simplerender2.game.GameStateManager;
import com.pimphand.simplerender2.game.GameWorld;
import com.pimphand.simplerender2.input.InputEvent;

public class ShowModelViewer implements Command {

	private static final String TAG = ShowModelViewer.class.getSimpleName();
	
	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Changing game world state to Water World.");
		GameWorld.inst().changeState(GameStateManager.getState(GameStateEnum.MODEL_VIEWER));
	}

}
