package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.input.InputEvent;

public class StrafeLeft implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = StrafeLeft.class.getSimpleName();
	
	protected float mXDisp = -0.1f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().translateView(mXDisp, 0.0f, 0.0f);
	}
}
