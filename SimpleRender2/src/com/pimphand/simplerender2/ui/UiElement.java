package com.pimphand.simplerender2.ui;

import android.graphics.PointF;
import android.opengl.Matrix;

import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.rendering.PositionTypeEnum;
import com.pimphand.simplerender2.util.UiUtil;

public class UiElement {

	protected Geometry mGeometry;
	protected PointF mPosition;
	protected float mZPos;
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
	
	public void setPosition(PointF position) {
		this.mPosition = position;
		reposition();
	}

	public void setPositionType(PositionTypeEnum positionType) {
		this.mPositionType = positionType;
	}
	
	public void setZPos(float zPos) {
		this.mZPos = zPos;
		reposition();
	}
	
	public void reposition() {
		if(mPosition != null) {
			PointF elementTrans = UiUtil.viewToScreenCoords(mPosition);
			Matrix.translateM(mGeometry.mModelMatrix, 0, elementTrans.x, elementTrans.y, mZPos);
		}
	}
}
