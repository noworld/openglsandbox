package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.util.math.Vec3;

public class TurnLeft implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = TurnLeft.class.getSimpleName();
	
	protected float mYRot = -1.5f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().rotateCurrentCamera(mYRot, new Vec3(0.0f, 1.0f, 0.0f));
	}
}
