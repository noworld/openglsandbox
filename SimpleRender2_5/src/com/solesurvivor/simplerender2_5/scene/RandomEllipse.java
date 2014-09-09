package com.solesurvivor.simplerender2_5.scene;

import java.util.Random;

import com.solesurvivor.util.SSRandomUtil;
import com.solesurvivor.util.logging.SSLog;

import android.graphics.PointF;

public class RandomEllipse {

	public static final int NUM_OPERATIONS = 2;

	@SuppressWarnings("unused")
	private static final String TAG = RandomEllipse.class.getSimpleName();

	protected PointF mLocation;
	protected PointF mAxes;
	protected PointF mAxesSq;
	protected PointF mVec;
	protected float mRFocSq;
	protected int mOper;

	protected RandomEllipse[] mPerimeter = null; 

	public RandomEllipse(Random randy, float[] levels){
		
		//this.mLocation = new PointF(SSRandomUtil.clampRandomF(randy, low, high), SSRandomUtil.clampRandomF(randy, low, high));
		this.mLocation = new PointF(0.0f, 0.0f);
		this.mAxes = new PointF(SSRandomUtil.clampRandomF(randy, 0.2f, 0.9f), SSRandomUtil.clampRandomF(randy, 0.2f, 0.9f)); 
		this.mAxesSq = new PointF(mAxes.x*mAxes.x, mAxes.y*mAxes.y);
		this.mVec = new PointF(randy.nextFloat(), randy.nextFloat());
		this.mRFocSq = Math.abs((mAxes.x*mAxes.x) - (mAxes.y*mAxes.y));
		this.mOper = Double.valueOf(Math.floor(randy.nextDouble() * NUM_OPERATIONS)).intValue();

		if(levels != null && levels.length > 0) {
			int num = 500;
			mPerimeter = new RandomEllipse[num];
			for(int i = 0; i < num; i++) {
				mPerimeter[i] = new RandomEllipse(randy, levels, this);
			}
		}

	}

	protected RandomEllipse(Random randy, float[] levels, RandomEllipse parent){

		float level = 1.0f;
		float[] more = null;

		if(levels.length > 0) {
			level = levels[0];
			if(levels.length > 1) {
				more = new float[levels.length - 1];
				System.arraycopy(levels, 1, more, 0, more.length);
			}
		}

		//Random starting angle
		double rd = randy.nextDouble();
		double theta = Math.acos((rd * 2.0) - 1.0) * 2.0;
		SSLog.d(TAG, "Random angle (%.2f/%.2f): %.2f", rd, theta, Math.toDegrees(theta));
		
		float avg = (parent.mAxes.x + parent.mAxes.y) / 2.0f;
		PointF rng = new PointF(avg/10.0f, avg/3.0f);
		
		this.mLocation = new PointF(Double.valueOf(parent.getLocation().y + (parent.getAxes().y * Math.sin(theta))).floatValue(), 
				Double.valueOf(parent.getLocation().x + (parent.getAxes().x * Math.cos(theta))).floatValue());
		this.mAxes = new PointF(SSRandomUtil.clampRandomF(randy, rng.x, rng.y)*level, SSRandomUtil.clampRandomF(randy, rng.x, rng.y)*level);
		this.mAxesSq = new PointF(mAxes.x*mAxes.x, mAxes.y*mAxes.y);
		this.mVec = new PointF(randy.nextFloat(), randy.nextFloat());
		this.mRFocSq = Math.abs((mAxes.x*mAxes.x) - (mAxes.y*mAxes.y));
		this.mOper = Double.valueOf(Math.floor(randy.nextDouble() * NUM_OPERATIONS)).intValue();

		if(more != null) {
			int num = 0;
			mPerimeter = new RandomEllipse[num];
			for(int i = 0; i < num; i++) {
				mPerimeter[i] = new RandomEllipse(randy, more, this);
			}
		}

	}

	public PointF getLocation() {
		return mLocation;
	}

	public PointF getAxes() {
		return mAxes;
	}

	public PointF getVec() {
		return mVec;
	}

	public float getRFocSq() {
		return mRFocSq;
	}

	public PointF getAxesSq() {
		return this.mAxesSq;
	}

	public int getOper() {
		return this.mOper;
	}
	
	public RandomEllipse[] getEllipses() {
		return this.mPerimeter;
	}
	

}
