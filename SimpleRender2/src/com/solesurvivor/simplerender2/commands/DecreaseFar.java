package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.input.InputEvent;

public class DecreaseFar implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = DecreaseFar.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameGlobal.inst().getCamera().adjustFar(-1.0f);
	}
}
