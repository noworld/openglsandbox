package com.solesurvivor.simplerender2_5.weather;

import com.solesurvivor.simplerender2_5.scene.Node;
import com.solesurvivor.util.math.Vec3;

public class WeatherManager implements Node {
	
	private static WeatherManager sInstance;

	private WeatherManager() {
		
	}
	
	public static void init() {
		sInstance = new WeatherManager();
	}
	
	public static WeatherManager inst() {
		return sInstance;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scale(Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(Vec3 trans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getWorldMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getTransMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetTranslation() {
		// TODO Auto-generated method stub
		
	}
	
	
}
