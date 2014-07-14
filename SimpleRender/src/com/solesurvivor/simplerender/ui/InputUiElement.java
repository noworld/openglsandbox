package com.solesurvivor.simplerender.ui;

import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;

import com.solesurvivor.simplerender.Geometry;

public class InputUiElement extends UiElement {

	private final Geometry mUiGeo;
	private final InputArea mInputArea;
	private Map<String,Command> mCommands = new HashMap<String,Command>();
	
	public InputUiElement(Geometry uiGeo, InputArea area) {
		this.mUiGeo = uiGeo;
		this.mInputArea = area;
	}
	
	public Geometry getGeometry() {
		return mUiGeo;
	}
	
	public void inputEvent(PointF p) {
		if(mInputArea.isPressed(p)) {
			for(Command c : mCommands.values()) {
				c.execute(new Object[]{mUiGeo.mName,p.x,p.y});
			}
		}
	}
	
	public void registerCommand(String name, Command command) {
		mCommands.put(name, command);
	}

}
