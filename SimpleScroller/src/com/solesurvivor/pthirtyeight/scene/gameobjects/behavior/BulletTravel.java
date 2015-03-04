package com.solesurvivor.pthirtyeight.scene.gameobjects.behavior;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.scene.StatefulNodeImpl;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.math.Vec3;

public class BulletTravel implements NodeState {
	
	@SuppressWarnings("unused")
	private static final String TAG = BulletTravel.class.getSimpleName();
	private static final float SPEED = 400;
	private static final float MAX_D = 250;	
//	private static final long TTL = 1250;
	
	private float dist = 0;
	
	@Override
	public void enter(StatefulNodeImpl target) {
		dist = 0;
	}

	@Override
	public void execute(StatefulNodeImpl target) {
		SpriteNode s = (SpriteNode)target;
		
		double deltaSec = ((double)GameWorld.inst().getDeltaT()) / 1000.0;
		double deltaPx = (deltaSec * (SPEED + s.getSpeed()));
		Vec3 trans = Vec3.createZeroVec3();
		trans.setY((float)deltaPx);
		
		dist = dist + (float)deltaPx;
		
//		if(GameWorld.inst().getGameT() - startTime > TTL) {
		if(dist > MAX_D) {
			s.setAlive(false);
//			SSLog.d(TAG, "Bullet died. Position: %s", s.getAnimTrans().prettyString());
		} else {
			s.translateAnimation(trans);
		}		
	}

	@Override
	public void exit(StatefulNodeImpl target) {
		
	}



}
