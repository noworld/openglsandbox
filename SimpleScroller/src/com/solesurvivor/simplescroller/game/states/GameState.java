package com.solesurvivor.simplescroller.game.states;

import java.util.List;

import android.graphics.Point;

import com.solesurvivor.simplescroller.input.InputHandler;
import com.solesurvivor.simplescroller.scene.Geometry;
import com.solesurvivor.util.math.Vec3;

public interface GameState {
	
	public void enter();
	
	public void execute();
	
	public void render();
	
	public void exit();
	
	public long getDeltaT();
	
	public void resizeViewport(Point p);
	
	public Point getViewport();
	
	public List<InputHandler> getInputs();
	
	public List<Geometry> getUiElements();
	
	public void rotateCurrentCamera(float angle, Vec3 rot);
	
	public void translateCurrentCamera(Vec3 trans);
	
	public void setZoom(float zoom);

}
