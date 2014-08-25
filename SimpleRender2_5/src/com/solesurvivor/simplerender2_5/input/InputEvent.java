package com.solesurvivor.simplerender2_5.input;


import android.graphics.PointF;

import com.solesurvivor.simplerender2_5.util.UiUtil;

public class InputEvent {

	private InputEventEnum mEvent;
	private PointF mCoords;
	private PointF mViewCoords;
	
	public InputEvent(InputEventEnum event, PointF coords) {
		this.mEvent = event;
		this.mCoords = coords;
		this.mViewCoords = UiUtil.screenToViewCoords(coords);
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
	
}
