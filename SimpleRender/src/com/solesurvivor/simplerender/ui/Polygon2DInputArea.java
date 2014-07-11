package com.solesurvivor.simplerender.ui;

import java.util.List;

import com.solesurvivor.util.math.VectorMath;

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
		//TODO: Convert everything to work with PointF or something.
		Float[] target = new Float[]{(float)p.x, (float)p.y};
		
		/*
		 * Work around all the hull line segments in a loop.
		 * If p is found to be to the left of any of them
		 * then return false.
		 * 
		 * This assumes the points in mHull are in clockwise order
		 */
		for(int i = 0; i < mHull.size(); i++) {
			if(i == (mHull.size() - 1)) {
				//Handle special case of looping back to the first
				if(VectorMath.crossZ(mHull.get(i), mHull.get(0), target) > 0) {
					return false;
				}
			} else {
				//Test coordinates
				if(VectorMath.crossZ(mHull.get(i), mHull.get(i+1), target) > 0) {
					return false;
				}
			}
		}
		
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
