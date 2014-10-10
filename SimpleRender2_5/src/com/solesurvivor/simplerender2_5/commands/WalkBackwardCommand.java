package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.util.math.Vec3;

public class WalkBackwardCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = WalkBackwardCommand.class.getSimpleName();
	
	protected float mZDisp = -0.05f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().translateCurrentCamera(new Vec3(0.0f, 0.0f, mZDisp));
	}
}
