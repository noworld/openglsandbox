package com.solesurvivor.simplerender2_5.game.states;

import java.util.List;

import android.graphics.Point;

import com.solesurvivor.simplerender2_5.input.InputHandler;

public interface GameState {

	public void enter();
	
	public void execute();
	
	public void render();
	
	public void exit();
	
	public long getDeltaT();
	
	public void resizeViewport(Point p);
	
	public List<InputHandler> getInputs();

}
