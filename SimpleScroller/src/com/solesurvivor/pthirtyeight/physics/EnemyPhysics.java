package com.solesurvivor.pthirtyeight.physics;

import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.pthirtyeight.scene.gameobjects.behavior.EnemyDeadAnimation;

public class EnemyPhysics implements PhysicsComponent {

	@Override
	public void collide(SpriteNode self, SpriteNode other) {
		self.changeState(new EnemyDeadAnimation());
		self.setPhysics(new EmptyPhysics());
	}


}
