package com.solesurvivor.simplescroller.input;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.opengl.Matrix;

import com.solesurvivor.util.math.VectorMath;


public class Polygon2DInputArea implements InputArea {
	
	private static final int X_INDEX = 0;
	private static final int Y_INDEX = 1;
	
	private List<Float[]> modelSpaceHull;
	private List<Float[]> hull;
	private PointF topLeft;
	private PointF bottomRight;
	private PointF center;
	private float[] modelMatrix;
	private boolean dirty = true;
	
	public Polygon2DInputArea(List<Float[]> hull) {
		this.hull = new ArrayList<Float[]>();
		this.modelSpaceHull = hull;
		this.modelMatrix = new float[16];
		reset();	
	}

	@Override
	public boolean isPressed(PointF p) {		
		if(dirty) {
			updateHull();
			updateBoundingBox();
//			this.mDirty = false;
		}
		return inBoundingBox(p) && inHull(p);
	}
	
	@Override
	public void scale(float x, float y, float z) {
		Matrix.scaleM(modelMatrix, 0, x, y, z);
		dirty = true;
	}
	
	@Override
	public void translate(float x, float y, float z) {
		Matrix.translateM(modelMatrix, 0, x, y, z);
		dirty = true;
	}
	
	@Override
	public void reset() {
		Matrix.setIdentityM(modelMatrix, 0);
		dirty = true;
	}
	
	@Override
	public PointF getInputCenter() {		
		return center;
	}

	private boolean inBoundingBox(PointF p) {
		return p.x > topLeft.x
				&& p.x < bottomRight.x
				&& p.y < topLeft.y
				&& p.y > bottomRight.y;
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
		for(int i = 0; i < hull.size(); i++) {
			if(i == (hull.size() - 1)) {
				//Handle special case of looping back to the first
				if(VectorMath.crossZ(hull.get(i), hull.get(0), target) > 0) {
					return false;
				}
			} else {
				//Test coordinates
				if(VectorMath.crossZ(hull.get(i), hull.get(i+1), target) > 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void updateHull() {
		hull.clear();
		for(Float[] point : modelSpaceHull) {
			float[] oldVec = {point[0], point[1], point[2], 1.0f};
			float[] newVec = new float[4]; 
			Matrix.multiplyMV(newVec, 0, modelMatrix, 0, oldVec, 0);
			hull.add(new Float[]{newVec[0], newVec[1]});
		}
	}
	
	private void updateBoundingBox() {
		float top = Float.NEGATIVE_INFINITY;
		float left = Float.POSITIVE_INFINITY;
		float bottom = Float.POSITIVE_INFINITY;
		float right = Float.NEGATIVE_INFINITY;
		
		for(Float[] point : hull) {
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
		
		topLeft = new PointF(left, top);
		bottomRight = new PointF(right, bottom);
		
		float cx = (bottomRight.x - topLeft.x) / 2.0f;
		cx = topLeft.x + cx;
		float cy = (bottomRight.y - topLeft.y) / 2.0f;
		cy = bottomRight.y - cy;
		center = new PointF(cx, cy);
	}

}
