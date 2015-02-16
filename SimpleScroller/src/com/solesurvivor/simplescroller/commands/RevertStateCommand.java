package com.solesurvivor.simplescroller.commands;

import android.util.Log;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.simplescroller.input.InputEvent;

public class RevertStateCommand implements Command {

	private static final String TAG = RevertStateCommand.class.getSimpleName();
	
	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Reverting world to previous state.");
		GameWorld.inst().revertState();
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
