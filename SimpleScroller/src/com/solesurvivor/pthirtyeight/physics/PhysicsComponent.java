package com.solesurvivor.pthirtyeight.physics;

import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;

public interface PhysicsComponent {

	public void collide(SpriteNode self, SpriteNode other);
	
}
