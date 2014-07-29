package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameWorld;
import com.solesurvivor.simplerender2.input.InputEvent;

public class Jump implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = Jump.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameWorld.inst().impulseView(0.0f, 0.3f, 0.0f);
	}
}
