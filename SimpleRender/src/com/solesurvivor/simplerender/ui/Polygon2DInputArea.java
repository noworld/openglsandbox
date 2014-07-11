package com.solesurvivor.simplerender.ui;

import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;


public class Polygon2DInputArea implements InputArea {
	
	private static final int X_INDEX = 0;
	private static final int Y_INDEX = 1;
	
	private List<Float[]> mHull;
	private PointF mTopLeft;
	private PointF mBottomRight;
	
	public Polygon2DInputArea(List<Float[]> hull) {
		this.mHull = hull;
		initBoundingBox();
	}

	@Override
	public boolean isPressed(Point p) {
		
		return inBoundingBox(p) && inHull(p);
	}

	private boolean inBoundingBox(Point p) {
		return p.x > mTopLeft.x
				&& p.x < mBottomRight.x
				&& p.y < mTopLeft.y
				&& p.y > mBottomRight.y;
	}

	private boolean inHull(Point p) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void initBoundingBox() {
		float top = Float.NEGATIVE_INFINITY;
		float left = Float.POSITIVE_INFINITY;
		float bottom = Float.POSITIVE_INFINITY;
		float right = Float.NEGATIVE_INFINITY;
		
		for(Float[] point : mHull) {
			if(point[X_INDEX] > right) {
				right = point[X_INDEX];
			} else if(point[X_INDEX] < left) {
				left = point[X_INDEX];
			}
			
			if(point[Y_INDEX] > top) {
				top = point[Y_INDEX];
			} else if(point[Y_INDEX] < bottom) {
				bottom = point[Y_INDEX];
			}
		}
		
		mTopLeft = new PointF(left, top);
		mBottomRight = new PointF(right, bottom);
		
	}

}
