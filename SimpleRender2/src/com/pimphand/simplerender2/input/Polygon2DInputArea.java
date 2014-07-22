package com.pimphand.simplerender2.input;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.opengl.Matrix;

import com.solesurvivor.util.math.VectorMath;


public class Polygon2DInputArea implements InputArea {
	
	private static final int X_INDEX = 0;
	private static final int Y_INDEX = 1;
	
	private List<Float[]> mModelSpaceHull;
	private List<Float[]> mHull;
	private PointF mTopLeft;
	private PointF mBottomRight;
	private float[] mModelMatrix;
	private boolean mDirty = true;
	
	public Polygon2DInputArea(List<Float[]> hull) {
		this.mHull = new ArrayList<Float[]>();
		this.mModelSpaceHull = hull;
		this.mModelMatrix = new float[16];
		reset();	
	}

	@Override
	public boolean isPressed(PointF p) {		
		if(mDirty) {
			updateHull();
			updateBoundingBox();
//			this.mDirty = false;
		}
		return inBoundingBox(p) && inHull(p);
	}
	
	@Override
	public void scale(float x, float y, float z) {
		Matrix.scaleM(mModelMatrix, 0, x, y, z);
		mDirty = true;
	}
	
	@Override
	public void translate(float x, float y, float z) {
		Matrix.translateM(mModelMatrix, 0, x, y, z);
		mDirty = true;
	}
	
	@Override
	public void reset() {
		Matrix.setIdentityM(mModelMatrix, 0);
		mDirty = true;
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
	
	private void updateHull() {
		mHull.clear();
		for(Float[] point : mModelSpaceHull) {
			float[] oldVec = {point[0], point[1], point[2], 1.0f};
			float[] newVec = new float[4]; 
			Matrix.multiplyMV(newVec, 0, mModelMatrix, 0, oldVec, 0);
			mHull.add(new Float[]{newVec[0], newVec[1]});
		}
	}
	
	private void updateBoundingBox() {
		float top = Float.NEGATIVE_INFINITY;
		float left = Float.POSITIVE_INFINITY;
		float bottom = Float.POSITIVE_INFINITY;
		float right = Float.NEGATIVE_INFINITY;
		
		for(Float[] point : mHull) {
			if(point[X_INDEX] > right) {
				right = point[X_INDEX];
			}
			
			if(point[X_INDEX] < left) {
				left = point[X_INDEX];
			}
			
			if(point[Y_INDEX] > top) {
				top = point[Y_INDEX];
			}
			
			if(point[Y_INDEX] < bottom) {
				bottom = point[Y_INDEX];
			}
		}
		
		mTopLeft = new PointF(left, top);
		mBottomRight = new PointF(right, bottom);
	}

}
