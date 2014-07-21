package com.pimphand.simplerender2.util;

import android.graphics.PointF;

import com.pimphand.simplerender2.scene.GameWorld;

public class UiUtil {

	public static PointF viewToScreenCoords(PointF viewCoords) {
		float screenWidth = (float)GameWorld.instance().getCamera().getViewport().x;
		float screenHeight = (float)GameWorld.instance().getCamera().getViewport().y;
		
		float xPos =  (viewCoords.x/2) - (screenWidth/2);
		float yPos =  (viewCoords.y/2) - (screenHeight/2);
		
		return new PointF(xPos,yPos);
	}
}
