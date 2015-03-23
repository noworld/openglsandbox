package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;

public class Curve extends NodeImpl {

	protected String name;
	protected float[] points;
	protected boolean closed = false;
	protected int dataSize = 3;

	public Curve(String name, float[] points) {
		super();
		this.name = name;
		this.points = points;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float[] getPoints() {
		return points;
	}

	public void setPoints(float[] points) {
		this.points = points;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public int getDataSize() {
		return dataSize;
	}
	
	public void setDataSize(int size) {
		this.dataSize = size;
	}
	
	@Override
	public void render() {
		((GridMapRenderer)RendererManager.getRenderer()).drawCurve(this);
		super.render();
	}
	
}
