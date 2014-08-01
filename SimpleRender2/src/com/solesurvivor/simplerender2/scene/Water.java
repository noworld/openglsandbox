package com.solesurvivor.simplerender2.scene;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplerender2.rendering.Geometry;
import com.solesurvivor.simplerender2.rendering.water.Wave;

public class Water extends GameEntity {

	@SuppressWarnings("unused")
	private static final String TAG = Water.class.getSimpleName();

	private List<Wave> mWaves;
	private float mTransparency = 0.5f;
	private int mTextureId;
	private int mShaderId;
	
	private Water(Geometry geometry) {
		super(geometry);
		this.mWaves = new ArrayList<Wave>();
	}
	
	public Water(List<Wave> waves, Geometry geometry, int textureId, int shaderId) {
		this(geometry);
		this.mWaves.addAll(waves);
		this.mTextureId = textureId;
		this.mShaderId = shaderId;
	}
	
	public List<Wave> getWaves() {
		return mWaves;
	}
	
	public boolean addWave(Wave wave) {
		return mWaves.add(wave);
	}
	
	public float getTransparency() {
		return mTransparency;
	}

	public void setTransparency(float transparency) {
		this.mTransparency = transparency;
	}

	public int getmTextureId() {
		return mTextureId;
	}

	public void setmTextureId(int mTextureId) {
		this.mTextureId = mTextureId;
	}

	public int getShaderId() {
		return mShaderId;
	}

	public void setShaderId(int mShaderId) {
		this.mShaderId = mShaderId;
	}
	
}
