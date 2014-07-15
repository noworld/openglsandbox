package com.pimphand.simplerender2.ui;

import android.graphics.PointF;

import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.rendering.PositionTypeEnum;

public class UiElement {

	protected Geometry mGeometry;
	protected PointF mPosition;
	protected PositionTypeEnum mPositionType;
	protected PointF mScale;

	public UiElement(Geometry geometry) {
		this.mGeometry = geometry;
	}
	
	public Geometry getGeometry() {
		return mGeometry;
	}
	
	public void setScale(PointF scale) {
		this.mScale = scale;
	}
	
	public void setPosition(PointF position, PositionTypeEnum positionType) {
		this.mPosition = position;
		this.mPositionType = positionType;
	}
}
