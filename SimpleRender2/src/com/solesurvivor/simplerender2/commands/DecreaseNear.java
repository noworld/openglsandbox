package com.solesurvivor.simplerender2.commands;

import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.input.InputEvent;

public class DecreaseNear implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = DecreaseNear.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameGlobal.inst().getCamera().adjustNear(-0.1f);
	}
}
