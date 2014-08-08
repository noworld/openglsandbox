package com.solesurvivor.simplerender2.fsm;

import android.graphics.Point;

import com.solesurvivor.simplerender2.scene.GameObjectLibrary;
import com.solesurvivor.util.math.Vec3;

public interface GameState<T> {

	public void enter(T target);
	
	public void execute(T target);
	
	public void render(T target);
	
	public void exit(T target);
	
	//TODO: Is this the right place for this?
	public GameObjectLibrary getLibrary();
	public void resizeViewport(Point point);
	public void translateView(float x, float y, float z);
	public void rotateView(float angle, float x, float y, float z);
	public float[] getUiMatrix();
	public float[] getViewMatrix();
	public float[] getAgentViewMatrix();
	public float[] getProjectionMatrix();
	public void impulseView(float x, float y, float z);
	public float getDistanceToCamera(Vec3 point);
}
