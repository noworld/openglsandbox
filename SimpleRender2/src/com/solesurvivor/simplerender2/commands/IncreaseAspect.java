package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.input.InputEvent;

public class IncreaseAspect implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = IncreaseAspect.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameGlobal.inst().getCamera().adjustAspect(0.05f);
	}
}
