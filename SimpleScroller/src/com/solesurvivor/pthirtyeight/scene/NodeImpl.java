package com.solesurvivor.pthirtyeight.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.pthirtyeight.game.messaging.GameMessage;
import com.solesurvivor.util.math.Vec3;

public class NodeImpl implements Node {

	protected String name;
	protected List<Node> children;
	protected Vec3 position;
	protected float[] transMatrix;
	protected float[] rotMatrix;
	protected float[] scaleMatrix;
	protected float[] worldMatrix;
	protected float[] tempMatrix;
	protected boolean dirty = true;
	protected boolean alive = true;
	protected Node parent;

	public NodeImpl() {		
		name = java.util.UUID.randomUUID().toString();
		position = Vec3.createZeroVec3();
		children = Collections.synchronizedList(new ArrayList<Node>());
		transMatrix = new float[16];
		rotMatrix = new float[16];
		scaleMatrix = new float[16];
		worldMatrix = new float[16];
		tempMatrix = new float[16];
		Matrix.setIdentityM(transMatrix, 0);
		Matrix.setIdentityM(rotMatrix, 0);
		Matrix.setIdentityM(scaleMatrix, 0);
		Matrix.setIdentityM(worldMatrix, 0);
		Matrix.setIdentityM(tempMatrix, 0);
	}

	public NodeImpl(Node parent) {
		this();
		this.parent = parent;
	}
	
	public NodeImpl(String name) {
		this();
		this.name = name;
	}
	
	public Vec3 getPosition() {
		return position;
	}
	
	@Override
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}

	@Override
	public void update() {
		
		//XXX should I have another method/s to override
		//to make this code more reusable?
		//e.g. preUpdate();

		if(dirty || (parent != null && parent.isDirty())) {
			recalcMatrix();
			this.dirty = true;
		}

		synchronized(children) {
			for(Node n : children) {
				n.update();
			}
		}
		
		//e.g. postUpdate();
		
//		this.mDirty = false;
	}

	@Override
	public void render() {
		renderChildren();
	}

	@Override
	public void addChild(Node n) {
		synchronized(children) {
			children.add(n);
			n.setParent(this);
		}
	}
	
	@Override
	public void removeChild(Node n) {
		synchronized(children) {
			children.remove(n);
			n.setParent(null);
		}
	}

	@Override
	public void scale(Vec3 axes) {
		Matrix.scaleM(scaleMatrix, 0, axes.getX(), axes.getY(), axes.getZ());
		dirty = true;
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		Matrix.rotateM(rotMatrix, 0, angle, axes.getX(), axes.getY(), axes.getZ());
		dirty = true;
	}

	@Override
	public void translate(Vec3 trans) {
		position.add(trans);
		Matrix.setIdentityM(transMatrix, 0);
		Matrix.translateM(transMatrix, 0, position.getX(), position.getY(), position.getZ());
		dirty = true;
	}

	@Override
	public float[] getWorldMatrix() {

		if(dirty) {
			recalcMatrix();
//			mDirty = false;
		}

		return worldMatrix;
	}
	
	@Override
	public float[] getTransMatrix() {
		
//		if(dirty) {
//			recalcMatrix();
//		}
		
		return transMatrix;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected void renderChildren() {
		for(Node n : children) {
			n.render();
		}
	}

	protected void recalcMatrix() {

		Matrix.setIdentityM(worldMatrix, 0);
		Matrix.setIdentityM(tempMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, worldMatrix, 0, scaleMatrix, 0);		
		Matrix.multiplyMM(worldMatrix, 0, tempMatrix, 0, transMatrix, 0);
		Matrix.multiplyMM(tempMatrix, 0, worldMatrix, 0, rotMatrix, 0);

		if(parent != null) {
			Matrix.multiplyMM(worldMatrix, 0, parent.getWorldMatrix(), 0, tempMatrix, 0);
		} else {
			System.arraycopy(tempMatrix, 0, worldMatrix, 0, tempMatrix.length);
		}
	}

	@Override
	public void receive(GameMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
