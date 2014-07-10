package com.solesurvivor.util.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.solesurvivor.util.SSArrayUtil;


public class QuickHull {

	private static final short NUM_LINE_ENDPOINTS = 2;
	private static final int LINE_START_INDEX = 0;
	private static final int LINE_END_INDEX = 1;
	private static final int ARRAY_START_ELEMENT = 0;
	
	/**
	 * 
	 */
	public static float[] QuickHull2d(float[] points, int stride) {
		float[] hull = null;		
		
		//Get the leftmost and rightmost points
		int[] minMaxX = getMinMaxX(points, stride);
		
		Float[] pointsObj = ArrayUtils.toObject(points);
		
		Float[] minX = new Float[stride];
		System.arraycopy(pointsObj, minMaxX[LINE_START_INDEX], minX, 0, stride);
		Float[] maxX = new Float[stride];
		System.arraycopy(pointsObj, minMaxX[LINE_END_INDEX], maxX, 0, stride);

		List<Float[]> workingHull = new ArrayList<Float[]>();
		workingHull.add(minX);
		workingHull.add(maxX);
		pointsObj = removeIndexes(pointsObj, minMaxX, stride);
		
		List<Float[]> left = new ArrayList<Float[]>();
		List<Float[]> right = new ArrayList<Float[]>();
		
		for(int i = ARRAY_START_ELEMENT; i < points.length; i += stride) {
			Float[] testPoint = new Float[stride];
			System.arraycopy(points, i, testPoint, ARRAY_START_ELEMENT, stride);
			if(VectorMath.crossZ(minX, maxX, testPoint) < 0.0f) {
				left.add(testPoint);
			} else {
				right.add(testPoint);
			}
		}
		
		doHull(minX, maxX, left, workingHull);
		doHull(maxX, minX, right, workingHull);
		
		return hull;
	}
	
	private static void doHull(Float[] A, Float[] B, List<Float[]> set, List<Float[]> hull) {

		int insertPosition = hull.indexOf(B);
		if (set.size() == 0) return;
		if (set.size() == 1) {
		  Float[] p = set.get(0);
		  set.remove(p);
		  hull.add(insertPosition,p);
		  return;
		}
		float dist = Integer.MIN_VALUE;
		int furthestPoint = -1;
		for (int i = 0; i < set.size(); i++) {
			Float[] p = set.get(i);
		  float distance  = VectorMath.compDist(A,B,p);
		  if (distance > dist) {
		    dist = distance;
		    furthestPoint = i;
		  }
		}
		Float[] P = set.get(furthestPoint);
		set.remove(furthestPoint);
		hull.add(insertPosition,P);
		
		// Determine who's to the left of AP
		ArrayList<Float[]> leftSetAP = new ArrayList<Float[]>();
		for (int i = 0; i < set.size(); i++) {
		  Float[] M = set.get(i);
		  if (VectorMath.crossZ(A,P,M)==1) {
		    //set.remove(M);
		    leftSetAP.add(M);
		  }
		}
		
		// Determine who's to the left of PB
		ArrayList<Float[]> leftSetPB = new ArrayList<Float[]>();
		for (int i = 0; i < set.size(); i++) {
		  Float[] M = set.get(i);
		  if (VectorMath.crossZ(P,B,M)==1) {
		    //set.remove(M);
		    leftSetPB.add(M);
		  }
		}
		doHull(A,P,leftSetAP,hull);
		doHull(P,B,leftSetPB,hull);
		
	}

	private static Float[] removeIndexes(Float[] array, int[] indexes, int stride) {
		
		Float[] newArray = new Float[array.length];
		System.arraycopy(array, ARRAY_START_ELEMENT, newArray, ARRAY_START_ELEMENT, newArray.length);
		
		for(int i = ARRAY_START_ELEMENT; i < indexes.length; i++) {
			newArray = SSArrayUtil.remove(newArray, indexes[i], stride);
		}
		
		return newArray;
	}

	/**
	 * Finds points in the array with the minimum and maximum X values
	 * @param points
	 * @param stride
	 * @return A <tt>float[]</tt> 
	 */
	public static int[] getMinMaxX(float[] points, int stride) {
		float[] minMax = new float[stride*NUM_LINE_ENDPOINTS];
		int[] indexes = new int[NUM_LINE_ENDPOINTS];
		//Initialize to first point
		System.arraycopy(points, ARRAY_START_ELEMENT, minMax, ARRAY_START_ELEMENT, stride);
		System.arraycopy(points, ARRAY_START_ELEMENT, minMax, stride, stride);
		
		for(int i = ARRAY_START_ELEMENT; i < points.length; i += stride) {
			
			//if x coord is less than min x 
			if(points[i] < minMax[ARRAY_START_ELEMENT]) {
				System.arraycopy(points, i, minMax, ARRAY_START_ELEMENT, stride);
				indexes[LINE_START_INDEX] = i;
			}
			
			//if x coord is greater than the max x
			if(points[i] > minMax[stride]) {
				System.arraycopy(points, i, minMax, stride, stride);
				indexes[LINE_END_INDEX] = i;
			}
		}
		
		return indexes;
	}
	
}
