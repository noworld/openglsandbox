package com.solesurvivor.simplescroller.scene.gameobjects;

import java.io.InputStreamReader;

import android.util.JsonReader;

public class GameLevel {
	
	protected String name;
	
	
	public static GameLevel readFromJson(String resName) {
		
//		JsonReader reader = new JsonReader(new InputStreamReader(GameLevel.class.getResourceAsStream(resName)));
		
		GameLevel gl = new GameLevel();
		
		return gl;
	}
}
