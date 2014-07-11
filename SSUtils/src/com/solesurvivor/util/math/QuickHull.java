package com.solesurvivor.util.math;

import java.util.ArrayList;
import java.util.List;


public class QuickHull {

	//	private static final String TAG = QuickHull.class.getSimpleName();

	private static final short NUM_LINE_ENDPOINTS = 2;
	private static final int LINE_START_INDEX = 0;
	private static final int LINE_END_INDEX = 1;
	private static final int ARRAY_START_ELEMENT = 0;
	private static final int X_INDEX = 0;

	/**
	 * 
	 */
	public static List<Float[]> QuickHull2d(List<Float[]> points) {

		//Get the leftmost and rightmost points
		List<Float[]> minMax = getMinMaxX(points);

		Float[] minX = minMax.get(LINE_START_INDEX);
		Float[] maxX = minMax.get(LINE_END_INDEX);

		//System.out.println(String.format("Furthest left: %s(x), %s(y), %s(z)",(Object[])minX));
		//System.out.println(String.format("Furthest right: %s(x), %s(y), %s(z)",(Object[])maxX));

		List<Float[]> workingHull = new ArrayList<Float[]>();
		workingHull.add(minX);
		workingHull.add(maxX);

		if(!points.remove(minX)) {
			//System.out.println(String.format("FAILED TO REMOVE POINT: %s(x), %s(y), %s(z)",(Object[])minX));
		}

		if(!points.remove(maxX)) {
			//System.out.println(String.format("FAILED TO REMOVE POINT: %s(x), %s(y), %s(z)",(Object[])maxX));
		}

		List<Float[]> left = new ArrayList<Float[]>();
		List<Float[]> right = new ArrayList<Float[]>();

		for(Float[] testPoint : points) {
			if(VectorMath.crossZ(minX, maxX, testPoint) < 0.0f) {
				//System.out.println(String.format("Adding left: %s(x), %s(y), %s(z)",(Object[])testPoint));
				left.add(testPoint);
			} else {
				//System.out.println(String.format("Adding right: %s(x), %s(y), %s(z)",(Object[])testPoint));
				right.add(testPoint);
			}
		}

		doHull(minX, maxX, right, workingHull);
		doHull(maxX, minX, left, workingHull);

		return workingHull;
	}

	private static void doHull(Float[] lineStart, Float[] lineEnd, List<Float[]> points, List<Float[]> workingHull) {

		int insertPosition = workingHull.indexOf(lineEnd);

		if (points.size() == 0) return;

		if (points.size() == 1) {
			Float[] p = points.get(0);
			points.remove(p);
			workingHull.add(insertPosition,p);
			return;
		}


		float dist = Float.MIN_VALUE;
		Float[] furthestPoint = null;
		for (Float[] p : points) {
			float distance = VectorMath.compDist(lineStart,lineEnd,p);
			if (distance > dist) {
				dist = distance;
				furthestPoint = p;
			}
		}

		//System.out.println(String.format("Found furthest point: %s(x), %s(y), %s(z)",(Object[])furthestPoint));

		if(!points.remove(furthestPoint)) {
			//System.out.println(String.format("FAILED TO REMOVE POINT: %s(x), %s(y), %s(z)", (Object[])furthestPoint));
		}

		workingHull.add(insertPosition,furthestPoint);

		// Determine who's to the left of AP
		ArrayList<Float[]> leftStart = new ArrayList<Float[]>();
		for (int i = 0; i < points.size(); i++) {
			Float[] M = points.get(i);
			if (VectorMath.crossZ(lineStart,furthestPoint,M) > 0) {
				leftStart.add(M);
			}
		}

		// Determine who's to the left of PB
		ArrayList<Float[]> leftEnd = new ArrayList<Float[]>();
		for (int i = 0; i < points.size(); i++) {
			Float[] M = points.get(i);
			if (VectorMath.crossZ(furthestPoint,lineEnd,M) > 0) {
				leftEnd.add(M);
			}
		}

		doHull(lineStart,furthestPoint,leftStart,workingHull);
		doHull(furthestPoint,lineEnd,leftEnd,workingHull);

	}

	/**
	 * Finds points in the array with the minimum and maximum X values
	 * @param points
	 * @param stride
	 * @return A <tt>float[]</tt> 
	 */
	public static List<Float[]> getMinMaxX(List<Float[]> points) {

		//Initialize to first point
		Float[] minPoint = points.get(ARRAY_START_ELEMENT);
		Float[] maxPoint = points.get(ARRAY_START_ELEMENT);

		for(Float[] point : points) {
			if(point[X_INDEX] < minPoint[X_INDEX]) {
				//if x coord is less than min x
				minPoint = point;
			} else if(point[X_INDEX] > maxPoint[X_INDEX]) {
				//if x coord is greater than max x
				maxPoint = point;
			}
		}

		List<Float[]> minMax = new ArrayList<Float[]>(NUM_LINE_ENDPOINTS);
		minMax.add(minPoint);
		minMax.add(maxPoint);

		return minMax;
	}

}
