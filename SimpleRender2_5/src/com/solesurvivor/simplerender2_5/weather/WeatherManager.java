package com.solesurvivor.simplerender2_5.weather;

import com.solesurvivor.simplerender2_5.scene.Node;

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
	
	
}
