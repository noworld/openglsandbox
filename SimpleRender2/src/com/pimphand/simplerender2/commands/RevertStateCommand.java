package com.pimphand.simplerender2.commands;

import android.util.Log;

import com.pimphand.simplerender2.game.GameWorld;
import com.pimphand.simplerender2.input.InputEvent;

public class RevertStateCommand implements Command {

	private static final String TAG = RevertStateCommand.class.getSimpleName();
	
	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Reverting world to previous state.");
		GameWorld.inst().revertState();
	}

}
