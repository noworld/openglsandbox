package com.solesurvivor.pthirtyeight.scene.gameobjects;

import android.graphics.Point;

import com.solesurvivor.pthirtyeight.rendering.BaseRenderer;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.math.Vec2;

public class Sprite {
	
	protected int texHandle;
	protected int datHandle;
	protected int idxHandle;
	protected Point dimension;
	protected float[] vertices;
	protected short[] indexes = {0,2,1,			
			1,2,3};
	protected int posSize = 3;
	protected int nrmSize = 3;
	protected int txcSize = 2;
	protected int numElements = 6;
	protected int elementStride = 32;
	protected int posOffset = 0;
	protected int nrmOffset = 12;
	protected int txcOffset = 24;
	protected Vec2 size;

	public Sprite(Point spriteDim, Vec2 spriteCo, SpriteSheet sheet) {
		this.dimension = spriteDim;
		float left = spriteCo.getX() / sheet.getDimensions().x;
		float right = (spriteCo.getX() + spriteDim.x) / sheet.getDimensions().x;
		float top = spriteCo.getY() / sheet.getDimensions().y;
		float bottom = (spriteCo.getY() + spriteDim.y) / sheet.getDimensions().y;

		float halfW = ((float)spriteDim.x)/2.0f;
		float halfH = ((float)spriteDim.y)/2.0f;

		vertices = new float[]{
				/*vvv*/-halfW,  halfH, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ left, top,
				/*vvv*/ halfW,  halfH, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ right, top,
				/*vvv*/-halfW, -halfH, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ left, bottom,
				/*vvv*/ halfW, -halfH, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ right, bottom};
		
		size = new Vec2(spriteDim.x, spriteDim.y);

		BaseRenderer ren = RendererManager.getRenderer();
		datHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(vertices));
		idxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(indexes));
		texHandle = sheet.getTextureId();
	}

	public int getTexHandle() {
		return texHandle;
	}

	public int getDatHandle() {
		return datHandle;
	}

	public int getIdxHandle() {
		return idxHandle;
	}

	public Point getDimension() {
		return dimension;
	}

	public float[] getVertices() {
		return vertices;
	}

	public short[] getIndexes() {
		return indexes;
	}

	public int getPosSize() {
		return posSize;
	}

	public int getNrmSize() {
		return nrmSize;
	}

	public int getTxcSize() {
		return txcSize;
	}

	public int getNumElements() {
		return numElements;
	}

	public int getElementStride() {
		return elementStride;
	}

	public int getPosOffset() {
		return posOffset;
	}

	public int getNrmOffset() {
		return nrmOffset;
	}

	public int getTxcOffset() {
		return txcOffset;
	}
	
	public Vec2 getSize() {
		return size;
	}
	
	
}
