package com.pimphand.simplerender2.commands;

import com.pimphand.simplerender2.game.GameWorld;
import com.pimphand.simplerender2.input.InputEvent;

public class TurnRight implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = TurnRight.class.getSimpleName();

	@Override
	public void execute(InputEvent event) {
		GameWorld.inst().rotateView(1.0f, 0.0f, 1.0f, 0.0f);
	}
}
