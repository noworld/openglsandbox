package com.pimphand.simplerender2.fsm;

import android.graphics.Point;

import com.pimphand.simplerender2.scene.GameObjectLibrary;

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
	public float[] getProjectionMatrix();
}
