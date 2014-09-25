package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.util.math.Vec3;

public class TurnLeftCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = TurnLeftCommand.class.getSimpleName();
	
	protected float mRot = -1.0f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().rotateCurrentCamera(mRot, new Vec3(0.0f, 1.0f, 0.0f));
	}
}
