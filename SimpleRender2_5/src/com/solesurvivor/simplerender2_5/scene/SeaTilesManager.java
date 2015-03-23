package com.solesurvivor.simplerender2_5.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.io.GeometryIO;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class SeaTilesManager implements Node {
	
	private static final String TAG = SeaTilesManager.class.getSimpleName();
	
	protected List<Node> mChildren; 
	
	protected PointF mTileSize = new PointF(20.0f,20.0f);
	protected Point mSeaSize = new Point(10,10);
	
	public SeaTilesManager() {
		this.mChildren = new ArrayList<Node>();
		Geometry geoMipPlane = null;
		try {
			geoMipPlane = GeometryIO.loadGeometry(R.raw.geomip).get(0);
		} catch (IOException e) {
			SSLog.d(TAG, "Error loading geomip.", e);
		}
		
		float startX = -(mTileSize.x * mSeaSize.x / 2.0f);
		float startZ = -(mTileSize.y * mSeaSize.y / 2.0f); //ZDepth
		
		if(geoMipPlane != null) {
			for(int i = 0; i < mSeaSize.x; i++) {
				for(int j = 0; j < mSeaSize.y; j++) {
					GeometryNode gn = new GeometryNode(geoMipPlane);
					gn.translate(new Vec3(startX + (mTileSize.x * i), 0.0f, startZ + (mTileSize.y * j)));
					gn.rotate(270, new Vec3(1.0f, 0.0f, 0.0f));
//					gn.applyTransforms();
					this.addChild(gn);
				}
			}
		}
	}

	@Override
	public void update() {
		for(Node n : mChildren) {
			n.update();
		}
	}

	@Override
	public void render() {
		for(Node n : mChildren) {
			n.render();
		}
	}

	@Override
	public void addChild(Node n) {
		mChildren.add(n);
	}

	@Override
	public void scale(Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(Vec3 trans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getWorldMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getTransMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetTranslation() {
		// TODO Auto-generated method stub
		
	}

}
