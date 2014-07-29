package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.input.InputEvent;

public class TurnLeft implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = TurnLeft.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().rotateView(-1.0f, 0.0f, 1.0f, 0.0f);
	}
}
