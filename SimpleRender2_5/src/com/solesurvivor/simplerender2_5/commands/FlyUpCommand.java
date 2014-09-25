package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.util.math.Vec3;

public class FlyUpCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = FlyUpCommand.class.getSimpleName();
	
	protected float mYDisp = -0.1f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().translateCurrentCamera(new Vec3(0.0f, mYDisp, 0.0f));
	}
}
