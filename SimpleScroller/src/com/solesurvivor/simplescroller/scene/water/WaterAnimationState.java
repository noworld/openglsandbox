package com.solesurvivor.simplescroller.scene.water;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.simplescroller.scene.StatefulNodeImpl;
import com.solesurvivor.simplescroller.scene.gameobjects.behavior.NodeState;
import com.solesurvivor.util.math.Vec3;

public class WaterAnimationState implements NodeState {
	
	@SuppressWarnings("unused")
	private static final String TAG = WaterAnimationState.class.getSimpleName();
	private static final float PIXELS_PER_SEC = 50;
	
	@Override
	public void enter(StatefulNodeImpl target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		double deltaSec = ((double)GameWorld.inst().getDeltaT()) / 1000.0;
		double deltaPx = -(deltaSec * PIXELS_PER_SEC);
		Vec3 trans = Vec3.createZeroVec3();
		trans.setY((float)deltaPx);
		
		((Water)target).translateAnimation(trans);
	}

	@Override
	public void exit(StatefulNodeImpl target) {
		
	}



}
