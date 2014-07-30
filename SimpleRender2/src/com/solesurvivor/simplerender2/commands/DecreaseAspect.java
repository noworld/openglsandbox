package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.input.InputEvent;

public class DecreaseAspect implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = DecreaseAspect.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameGlobal.inst().getCamera().adjustAspect(-0.05f);
	}
}
