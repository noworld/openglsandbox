package com.solesurvivor.util.view;

import android.graphics.Point;
import android.graphics.PointF;

public class UiUtil {

	public static PointF viewToScreenCoords(Point viewport, PointF viewCoords) {
		
		float screenWidth = (float)viewport.x;
		float screenHeight = (float)viewport.y;
		
		float xPos =  (viewCoords.x/2) - (screenWidth/2);
		float yPos =  (viewCoords.y/2) - (screenHeight/2);
		
		return new PointF(xPos,yPos);
	}
	
	public static PointF screenToViewCoords(Point viewport, PointF screenCoords) {

		float screenWidth = (float)viewport.x;
		float screenHeight = (float)viewport.y;
		
		float xPos =  screenCoords.x - (screenWidth/2);
		float yPos =  (screenHeight/2) - screenCoords.y;
		
		return new PointF(xPos,yPos);
	}
}
