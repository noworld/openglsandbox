package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.util.math.Vec3;

public class StrafeLeftCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = StrafeLeftCommand.class.getSimpleName();
	
	protected float mXDisp = -0.05f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().translateCurrentCamera(new Vec3(mXDisp, 0.0f, 0.0f));
	}

	@Override
	public void onStateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void release(InputEvent event) {
		// TODO Auto-generated method stub
		
	}
}
