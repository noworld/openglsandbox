package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.util.math.Vec3;

public class StrafeLeft implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = StrafeLeft.class.getSimpleName();
	
	protected float mXDisp = -0.1f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().translateCurrentCamera(new Vec3(mXDisp, 0.0f, 0.0f));
	}
}
