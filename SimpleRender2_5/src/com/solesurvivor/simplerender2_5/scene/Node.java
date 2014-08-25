package com.solesurvivor.simplerender2_5.scene;

public interface Node {

	public void update();
	public void render();
	public void addChild(Node n);
	
}
