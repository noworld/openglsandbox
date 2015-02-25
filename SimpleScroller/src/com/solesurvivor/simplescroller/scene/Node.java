package com.solesurvivor.simplescroller.scene;

import com.solesurvivor.simplescroller.game.messaging.MessageReceiver;
import com.solesurvivor.util.math.Vec3;

public interface Node extends MessageReceiver {
	
	public void scale(Vec3 axes);
	public void rotate(float angle, Vec3 axes);
	public void translate(Vec3 trans);
	public void update();
	public void render();
	public void addChild(Node n);
	public void removeChild(Node n);
	public Node getParent();
	public void setParent(Node n);
	public boolean isDirty();
	public boolean isAlive();
	public void setAlive(boolean alive);
	public float[] getWorldMatrix();
	public float[] getTransMatrix();
	public String getName();
	
}
