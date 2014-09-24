package com.solesurvivor.simplerender2_5.commands;

import com.solesurvivor.simplerender2_5.input.InputEvent;
import com.solesurvivor.simplerender2_5.input.InputEventEnum;
import com.solesurvivor.util.math.Vec2;

public class RotateView implements Command {

	@SuppressWarnings("unused")
	private static final String TAG = RotateView.class.getSimpleName();
	
	protected float mAngle = 1.0f;
	protected Vec2 mLastMove = new Vec2(0.0f, 0.0f);

	@Override
	public void execute(InputEvent event) {
		if(event.getEvent().equals(InputEventEnum.MOVE)) {
//			event.getViewCoords().
//			
//			length = 
//			angle = 
//			GameWorld.inst().getCurrentState().rotateCurrentCamera(mYRot, new Vec3(0.0f, 1.0f, 0.0f));
		}
	}
}
