package com.solesurvivor.simplerender2_5.scene;

import com.solesurvivor.simplerender2_5.rendering.DrawingConstants;
import com.solesurvivor.util.math.Vec3;

public class Wave {

	@SuppressWarnings("unused")
	private static final String TAG = Wave.class.getSimpleName();
	private static final float GRAV = 9.8f;
	
	private int mWaveType = 0;
	private float mAmplitude = 0.1f;
	private Vec3  mDirection = new Vec3(1.0f, 0.0f, 1.0f);
	private float mWavelength = 1.0f;
	private float mFrequency = 0.0f;
	private float mSpeed = 1.0f;
	private float mPhaseConst = mSpeed * mFrequency;
	private float mTimeScale = 1.0f;
	private float mPhaseShift = 0.0f;
	
	public Wave() {
		setWavelenFreqSpd(mWavelength);
	}
	
	public Wave(float amplitude, Vec3 direction, float wavelength, float speed) {
		this.mAmplitude = amplitude;
		this.mDirection = direction;		
		setWavelenFreqSpd(wavelength);
		setSpeed(speed);
	}
	
	public Wave(float amplitude, Vec3 direction, float wavelength) {
		this.mAmplitude = amplitude;
		this.mDirection = direction;		
		setWavelenFreqSpd(wavelength);
	}

	public float getAmplitude() {
		return mAmplitude;
	}

	public void setAmplitude(float amplitude) {
		this.mAmplitude = amplitude;
	}

	public Vec3 getDirection() {
		return mDirection;
	}

	public void setDirection(Vec3 direction) {
		this.mDirection = direction;
	}

	public float getWavelength() {
		return mWavelength;
	}

	public void setWavelength(float wavelength) {
		this.mWavelength = wavelength;
	}
	
	public void setWavelenFreqSpd(float wavelength) {
		this.mWavelength = wavelength;
		this.mFrequency = (float)Math.sqrt(GRAV * (DrawingConstants.TWO_PI/mWavelength));
		setSpeed((mWavelength/mFrequency) + 0.25f);
	}

	public float getSpeed() {
		return mSpeed;
	}

	public void setSpeed(float speed) {
		this.mSpeed = speed;
		this.mPhaseConst = mSpeed * mFrequency;
	}

	public float getPhaseConst() {
		return mPhaseConst;
	}

	public float getFrequency() {
		return mFrequency;
	}

	public float getTimeScale() {
		return mTimeScale;
	}

	public void setTimeScale(float timeScale) {
		this.mTimeScale = timeScale;
	}

	public float getPhaseShift() {
		return mPhaseShift;
	}

	public void setPhaseShift(float phaseShift) {
		this.mPhaseShift = phaseShift;
	}

	public int getWaveType() {
		return mWaveType;
	}

	public void setWaveType(int waveType) {
		this.mWaveType = waveType;
	}

}
