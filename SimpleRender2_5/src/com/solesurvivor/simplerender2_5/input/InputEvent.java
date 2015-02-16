package com.solesurvivor.simplerender2_5.input;


import android.graphics.PointF;

import com.solesurvivor.simplerender2_5.util.UiUtil;

public class InputEvent {

	private InputEventEnum mEvent;
	private PointF mCoords;
	private PointF mViewCoords;
	private PointF mControlCenter;
	
	public InputEvent(InputEventEnum event, PointF coords) {
		this.mEvent = event;
		this.mCoords = coords;
		this.mViewCoords = UiUtil.screenToViewCoords(coords);
	}
	
	public InputEvent(InputEventEnum event, PointF coords, PointF controlCenter) {
		this.mEvent = event;
		this.mCoords = coords;
		this.mViewCoords = UiUtil.screenToViewCoords(coords);
		this.mControlCenter = controlCenter;
	}

	public InputEventEnum getEvent() {
		return mEvent;
	}

	public PointF getCoords() {
		return mCoords;
	}
	
	public PointF getViewCoords() {
		return mViewCoords;
	}
	
	public PointF getControlCenter() {
		return mControlCenter;
	}
	
	public void setControlCenter(PointF controlCenter) {
		this.mControlCenter = controlCenter;
	}
	
}
