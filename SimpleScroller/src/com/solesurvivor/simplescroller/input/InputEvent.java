package com.solesurvivor.simplescroller.input;


import android.graphics.Point;
import android.graphics.PointF;

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.util.view.UiUtil;

public class InputEvent {

	private InputEventEnum event;
	private PointF coords;
	private PointF viewCoords;
	private PointF controlCenter;
	
	public InputEvent(InputEventEnum event, PointF coords) {
		this.event = event;
		this.coords = coords;
		//XXX HACK NEED TO SPEED THIS UP
		Point vp = GameWorld.inst().getCurrentState().getViewport();
		this.viewCoords = UiUtil.screenToViewCoords(vp, coords);
	}
	
	public InputEvent(InputEventEnum event, PointF coords, PointF controlCenter) {
		this.event = event;
		this.coords = coords;
		//XXX HACK NEED TO SPEED THIS UP
		Point vp = GameWorld.inst().getCurrentState().getViewport();
		this.viewCoords = UiUtil.screenToViewCoords(vp, coords);
		this.controlCenter = controlCenter;
	}

	public InputEventEnum getEvent() {
		return event;
	}

	public PointF getCoords() {
		return coords;
	}
	
	public PointF getViewCoords() {
		return viewCoords;
	}
	
	public PointF getControlCenter() {
		return controlCenter;
	}
	
	public void setControlCenter(PointF controlCenter) {
		this.controlCenter = controlCenter;
	}

}
