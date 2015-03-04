package com.solesurvivor.pthirtyeight.commands;

import android.util.Log;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.game.states.GameStateEnum;
import com.solesurvivor.pthirtyeight.game.states.GameStateManager;
import com.solesurvivor.pthirtyeight.input.InputEvent;

public class ChangeToPlay implements Command {
	
	private static final String TAG = ChangeToPlay.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		Log.d(TAG, "Changing game world state to Play.");
		GameWorld.inst().changeState(GameStateManager.getState(GameStateEnum.PLAY));
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
