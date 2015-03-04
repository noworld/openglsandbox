package com.solesurvivor.pthirtyeight.physics;

import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;

public class BulletPhysics implements PhysicsComponent {

	@Override
	public void collide(SpriteNode self, SpriteNode other) {
		self.setAlive(false);
	}


}
