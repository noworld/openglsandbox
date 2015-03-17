package com.solesurvivor.pthirtyeight.scene.gameobjects.levels;

import com.solesurvivor.pthirtyeight.game.GameWorld;
import com.solesurvivor.pthirtyeight.scene.gameobjects.GameObjectTypesEnum;
import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;

public class MapCell extends SpriteNode {
	
	private String id;
	
	public MapCell(String spriteName, String id) {
		super(spriteName, GameWorld.SCALE, GameObjectTypesEnum.MAP_CELL);
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

}
