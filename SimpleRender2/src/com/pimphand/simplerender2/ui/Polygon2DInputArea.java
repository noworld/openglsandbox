package com.pimphand.simplerender2.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

import com.solesurvivor.util.math.VectorMath;


public class Polygon2DInputArea implements InputArea {
	
	private static final int X_INDEX = 0;
	private static final int Y_INDEX = 1;
	
	private List<Float[]> mHull;
	private List<Float[]> mHullScaled;
	private PointF mTopLeft;
	private PointF mBottomRight;
	private PointF mScale = new PointF(1.0f,1.0f);
	
	public Polygon2DInputArea(List<Float[]> hull) {
		this.mHull = hull;
		initBoundingBox();
		scaleHull();
	}

	@Override
	public boolean isPressed(PointF p) {
		
		return inBoundingBox(p) && inHull(p);
	}
	
	@Override
	public void setScale(PointF factor) {
		this.mScale = factor;
		initBoundingBox();
		scaleHull();
	}

	private boolean inBoundingBox(PointF p) {
		return p.x > mTopLeft.x
				&& p.x < mBottomRight.x
				&& p.y < mTopLeft.y
				&& p.y > mBottomRight.y;
	}

	private boolean inHull(PointF p) {
		//TODO: Convert everything to work with PointF or something.
		Float[] target = new Float[]{(float)p.x, (float)p.y};
		
		/*
		 * Work around all the hull line segments in a loop.
		 * If p is found to be to the left of any of them
		 * then return false.
		 * 
		 * This assumes the points in mHull are in clockwise order
		 */
		for(int i = 0; i < mHullScaled.size(); i++) {
			if(i == (mHullScaled.size() - 1)) {
				//Handle special case of looping back to the first
				if(VectorMath.crossZ(mHullScaled.get(i), mHullScaled.get(0), target) > 0) {
					return false;
				}
			} else {
				//Test coordinates
				if(VectorMath.crossZ(mHullScaled.get(i), mHullScaled.get(i+1), target) > 0) {
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
			if(point[X_INDEX] * mScale.x > right) {
				right = point[X_INDEX] * mScale.x;
			}
			
			if(point[X_INDEX] * mScale.x < left) {
				left = point[X_INDEX] * mScale.x;
			}
			
			if(point[Y_INDEX] * mScale.y > top) {
				top = point[Y_INDEX] * mScale.y;
			}
			
			if(point[Y_INDEX] * mScale.y < bottom) {
				bottom = point[Y_INDEX] * mScale.y;
			}
		}
		
		mTopLeft = new PointF(left, top);
		mBottomRight = new PointF(right, bottom);
		
	}
	
	private void scaleHull() {
		if(mHullScaled == null) {
			mHullScaled = new ArrayList<Float[]>(mHull.size());
		} else {
			mHullScaled.clear();
		}
		
		for(Float[] point : mHull) {
			Float[] scaledPoint = new Float[point.length];
			System.arraycopy(point, 0, scaledPoint, 0, scaledPoint.length);
			scaledPoint[0] *= mScale.x;
			scaledPoint[1] *= mScale.y;
			mHullScaled.add(scaledPoint);
		}
		
	}

}
