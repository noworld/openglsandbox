package com.solesurvivor.pthirtyeight.physics;

import java.util.List;

import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.util.math.Vec2;

public class CollisionPhysicsProcessor implements PhysicsProcessor {
	
	@SuppressWarnings("unused")
	private static final String TAG = CollisionPhysicsProcessor.class.getSimpleName();

	@Override
	public void processPhysics(List<SpriteNode> physObjs) {
		
		for(int i = 0; i < physObjs.size() - 1; i++){
			SpriteNode first = physObjs.get(i);
			
			for(int j = i + 1; j < physObjs.size(); j++) {
				SpriteNode second = physObjs.get(j);
				
				if(disallowCollision(first,second)) {
					continue;
				}
				
				if(testForCollision(first, second)) {
					if(first.getPhysics() != null) {
						first.getPhysics().collide(first, second);
					}
					
					if(second.getPhysics() != null) {
						second.getPhysics().collide(second, first);
					}
				}
				
			}
		}
		
	}

	private boolean disallowCollision(SpriteNode first, SpriteNode second) {
		if(first.getType().equals(GameObjectTypesEnum.SELF_BULLET) && second.getType().equals(GameObjectTypesEnum.SELF_BULLET)) {
			return true;
		}
		
		if(first.getType().equals(GameObjectTypesEnum.SELF_BULLET) && second.getType().equals(GameObjectTypesEnum.PLAYER)) {
			return true;
		}
		
		if(first.getType().equals(GameObjectTypesEnum.PLAYER) && second.getType().equals(GameObjectTypesEnum.SELF_BULLET)) {
			return true;
		}
		
		return false;
	}

	private boolean testForCollision(SpriteNode first, SpriteNode second) {
		
			Vec2 fDim = first.getSprite().getSize();
			Vec2 sDim = second.getSprite().getSize();

			Vec2 dist = first.getAnimTrans().clone();
			dist.subtract(second.getAnimTrans());

			return (Math.abs(fDim.getY() + sDim.getY()) > Math.abs(dist.getY() * 2)) 
					&& (Math.abs(fDim.getX() + sDim.getX()) > Math.abs(dist.getX() * 2));
	}

}
