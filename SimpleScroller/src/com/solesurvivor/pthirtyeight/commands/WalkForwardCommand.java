package com.solesurvivor.pthirtyeight.commands;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.input.InputEvent;
import com.solesurvivor.util.math.Vec3;


public class WalkForwardCommand implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = WalkForwardCommand.class.getSimpleName();
	
	protected float zDisp = 0.05f;

	@Override
	public void execute(InputEvent event) {		
		GameWorld.inst().getCurrentState().translateCurrentCamera(new Vec3(0.0f, 0.0f, zDisp));
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
