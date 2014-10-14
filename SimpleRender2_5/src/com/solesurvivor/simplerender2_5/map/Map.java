package com.solesurvivor.simplerender2_5.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import com.solesurvivor.util.math.Vec3;


public class Map {

	protected Vec3 mCenter;
	protected Bitmap mDepthMap;
	
	public Map() {
		mCenter = new Vec3(0.0f, 0.0f, 0.0f);
	}
	
	public float distanceBetween(Vec3 loc1, Vec3 loc2) {
		return 0.0f;
	}
	
	public float getWindAt(Vec3 loc) {
		return 0.0f;
	}
	
	public int getWaterDepthAt(Vec3 loc) {
		Point p = getPixelLoc(loc);
		return Color.blue(mDepthMap.getPixel(p.x, p.y));
	}

	public float getWaterTempAt(Vec3 loc) {
		return 0.0f;
	}
	
	public float getAtmoTempAt(Vec3 loc) {
		return 0.0f;
	}
	
	public Point getPixelLoc(Vec3 loc) {
		return new Point();
	}
	
}
