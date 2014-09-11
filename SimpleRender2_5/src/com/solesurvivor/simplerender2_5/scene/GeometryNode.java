package com.solesurvivor.simplerender2_5.scene;

import org.apache.commons.lang.NotImplementedException;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.util.math.Vec3;

public class GeometryNode implements Node {

	@SuppressWarnings("unused")
	private static final String TAG = GeometryNode.class.getSimpleName();
	
	protected Geometry mGeometry = null;
	protected BaseRenderer mRen = null;
	
	public GeometryNode(Geometry geometry) {
		this.mGeometry = geometry;
		mGeometry.translate(new Vec3(0.0f, -2.0f, -5.0f));
		this.mRen = RendererManager.getRenderer();
	}

	@Override
	public void update() {
		float angle = (GameWorld.inst().getDeltaT() / 1000.0f) * 10.0f;
		mGeometry.rotate(angle, new Vec3(1.0f, 0.0f, 0.0f));
	}

	@Override
	public void render() {
		mRen.drawGeometry(mGeometry);
	}

	@Override
	public void addChild(Node n) {
		throw new NotImplementedException(this.getClass());
	}
	
	
}
