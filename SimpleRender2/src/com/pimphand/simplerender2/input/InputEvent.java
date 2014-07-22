package com.pimphand.simplerender2.input;

import com.pimphand.simplerender2.ui.InputEventEnum;

import android.graphics.PointF;

public class InputEvent {

	private InputEventEnum mEvent;
	private PointF mCoords;
	
	public InputEvent(InputEventEnum event, PointF coords) {
		this.mEvent = event;
		this.mCoords = coords;
	}

	public InputEventEnum getEvent() {
		return mEvent;
	}

	public PointF getCoords() {
		return mCoords;
	}
	
}
