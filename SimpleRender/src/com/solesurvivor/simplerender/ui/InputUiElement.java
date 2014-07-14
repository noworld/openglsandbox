package com.solesurvivor.simplerender.ui;

import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;

import com.solesurvivor.simplerender.Geometry;

public class InputUiElement extends UiElement {

	private final Geometry mUiGeo;
	private final InputArea mInputArea;
	private Map<String,Command> mContinuous = new HashMap<String,Command>();
	private Map<String,Command> mOneTime = new HashMap<String,Command>();
	
	private boolean mIsPressed = false;
	
	public InputUiElement(Geometry uiGeo, InputArea area) {
		this.mUiGeo = uiGeo;
		this.mInputArea = area;
	}
	
	public Geometry getGeometry() {
		return mUiGeo;
	}
	
	public void inputEvent(PointF p) {			
		if(mInputArea.isPressed(p)) {

			if(!mIsPressed) {
				mIsPressed = true;
				for(Command c : mOneTime.values()) {
					c.execute(new Object[]{mUiGeo.mName,p.x,p.y});
				}
			}
			
			for(Command c : mContinuous.values()) {
				c.execute(new Object[]{mUiGeo.mName,p.x,p.y});
			}
		} else {
			mIsPressed = false;
		}
	}

	public void registerContinuousCommand(String name, Command command) {
		mContinuous.put(name, command);
	}
	
	public void registerOneTimeCommand(String name, Command command) {
		mOneTime.put(name, command);
	}
	
	public void deRegisterContinuousCommand(String name) {
		mContinuous.remove(name);
	}
	
	public void deRegisterOneTimeCommand(String name) {
		mOneTime.remove(name);
	}

}
