package com.solesurvivor.simplerender.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.PointF;
import android.os.Vibrator;

import com.solesurvivor.simplerender.Geometry;

public class InputUiElement extends UiElement {

	private final Geometry mUiGeo;
	private final InputArea mInputArea;
	private Map<String,Command> mCommands = new HashMap<String,Command>();	
	private boolean mIsPressed = false;
	
	public InputUiElement(Geometry uiGeo, InputArea area) {
		this.mUiGeo = uiGeo;
		this.mInputArea = area;
	}
	
	public Geometry getGeometry() {
		return mUiGeo;
	}
	
	public boolean isPressed() {
		return mIsPressed;
	}
	
	public void inputEvent(PointF p) {			
		if(mInputArea.isPressed(p)) {
			
			if(!mIsPressed) {
				//If this is the first event of the press...
				TouchFeedback.instance().shortVib();
				mIsPressed = true;
			}
			
			for(Command c : mCommands.values()) {
				c.execute(new Object[]{mUiGeo.mName,p.x,p.y});
			}
		} else {
			mIsPressed = false;
		}
	}

	public void registerCommand(String name, Command command) {
		mCommands.put(name, command);
	}

}
