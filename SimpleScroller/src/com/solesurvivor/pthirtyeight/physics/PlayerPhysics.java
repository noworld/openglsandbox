package com.solesurvivor.pthirtyeight.physics;

import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.PlayerDeadAnimation;

public class PlayerPhysics implements PhysicsComponent {

	@Override
	public void collide(SpriteNode self, SpriteNode other) {
		if(!other.getType().equals(GameObjectTypesEnum.SELF_BULLET)) {
			self.changeState(new PlayerDeadAnimation());
			self.setPhysics(new EmptyPhysics());
		}
	}



}
