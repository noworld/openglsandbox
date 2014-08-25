package com.solesurvivor.simplerender2_5.game.states;

import android.graphics.Point;

public interface GameState {

	public void enter();
	
	public void execute();
	
	public void render();
	
	public void exit();
	
	public long getDeltaT();
	
	public void resizeViewport(Point p);

}
