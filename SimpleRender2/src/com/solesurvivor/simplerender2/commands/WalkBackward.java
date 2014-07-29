package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.input.InputEvent;

public class WalkBackward implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = WalkBackward.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameWorld.inst().translateView(0.0f, 0.0f, -0.1f);
	}
}
