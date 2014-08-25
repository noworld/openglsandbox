package com.solesurvivor.simplerender2_5.commands;

import android.util.Log;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;

public class RevertStateCommand implements Command {

	private static final String TAG = RevertStateCommand.class.getSimpleName();
	
	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Reverting world to previous state.");
		GameWorld.inst().revertState();
	}

}
