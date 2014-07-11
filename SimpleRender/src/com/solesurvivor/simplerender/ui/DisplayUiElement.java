package com.solesurvivor.simplerender.ui;

import com.solesurvivor.simplerender.Geometry;

public class DisplayUiElement extends UiElement {
	
	private Geometry mUiGeo;
	
	public DisplayUiElement(Geometry uiGeo) {
		this.mUiGeo = uiGeo;
	}
	
	public Geometry getGeometry() {
		return mUiGeo;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

}
