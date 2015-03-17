package com.solesurvivor.pthirtyeight.scene.gameobjects.levels;

import com.solesurvivor.pthirtyeight.scene.StatefulNodeImpl;

public class GameLevel extends StatefulNodeImpl {
	
	protected String name;
	
	public static GameLevel readFromJson(String resName) {
		
		GameLevel gl = new GameLevel();
		
		return gl;
	}
}
