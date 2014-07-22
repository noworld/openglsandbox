package com.pimphand.simplerender2.util;

import android.graphics.PointF;

import com.pimphand.simplerender2.game.GameWorld;

public class UiUtil {

	public static PointF viewToScreenCoords(PointF viewCoords) {

		//If viewport has not been initialized yet
		if(GameWorld.inst() == null
				|| GameWorld.inst().getCamera() == null
				|| GameWorld.inst().getCamera().getViewport() == null
				|| GameWorld.inst().getCamera().getViewport().x <= 0
				|| GameWorld.inst().getCamera().getViewport().y <=0) {
			return new PointF(0.0f, 0.0f);
		}
		
		float screenWidth = (float)GameWorld.inst().getCamera().getViewport().x;
		float screenHeight = (float)GameWorld.inst().getCamera().getViewport().y;
		
		float xPos =  (viewCoords.x/2) - (screenWidth/2);
		float yPos =  (viewCoords.y/2) - (screenHeight/2);
		
		return new PointF(xPos,yPos);
	}
	
	public static PointF screenToViewCoords(PointF screenCoords) {

		//If viewport has not been initialized yet
		if(GameWorld.inst() == null
				|| GameWorld.inst().getCamera() == null
				|| GameWorld.inst().getCamera().getViewport() == null
				|| GameWorld.inst().getCamera().getViewport().x <= 0
				|| GameWorld.inst().getCamera().getViewport().y <=0) {
			return new PointF(0.0f, 0.0f);
		}
		
		float screenWidth = (float)GameWorld.inst().getCamera().getViewport().x;
		float screenHeight = (float)GameWorld.inst().getCamera().getViewport().y;
		
		float xPos =  screenCoords.x - (screenWidth/2);
		float yPos =  (screenHeight/2) - screenCoords.y;
		
		return new PointF(xPos,yPos);
	}
}
