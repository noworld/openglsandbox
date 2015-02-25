package com.solesurvivor.simplescroller.scene.gameobjects;

import android.graphics.Point;

import com.solesurvivor.simplescroller.rendering.TextureManager;
import com.solesurvivor.util.logging.SSLog;

public class SpriteSheet {
	
	private static final String TAG = SpriteSheet.class.getSimpleName();

	protected String name;
	protected Point dimensions;
	protected double scale;
	protected int textureId;
	
	public SpriteSheet(Point dimensions, String textureName, double scale) {
		this.dimensions = dimensions;
		this.name = textureName;
		this.scale = scale;
		this.textureId = TextureManager.getTextureId(textureName);
		
		if(this.textureId < 0) {
			SSLog.w(TAG, "Texture not found: %s", textureName);
		}
	}

	public String getName() {
		return name;
	}

	public Point getDimensions() {
		return dimensions;
	}

	public int getTextureId() {
		return textureId;
	}

	public double getScale() {
		return scale;
	}
	
}
