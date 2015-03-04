package com.solesurvivor.pthirtyeight.scene;

import java.util.List;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.pthirtyeight.rendering.BaseRenderer;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.math.Vec2;

public abstract class Rectangle extends StatefulNodeImpl implements Drawable {

	@SuppressWarnings("unused")
	private static final String TAG = Rectangle.class.getSimpleName();
	
	protected int mDatHandle;
	protected int mIdxHandle;
	protected Point mDimension;
	protected float[] mVertices;
	protected short[] mIndexes = {0,2,1,
			1,2,3};
	protected int mPosSize = 3;
	protected int mNrmSize = 3;
	protected int mTxcSize = 2;
	protected int mNumElements = 6;
	protected int mElementStride = 32;
	protected int mPosOffset = 0;
	protected int mNrmOffset = 12;
	protected int mTxcOffset = 24;
	protected float[] mModelMatrix = new float[16];
	
	public Rectangle(Point dim) {
		this.mDimension = dim;
		float aspectWidth = ((float)dim.x)/((float)dim.y);

		mVertices = new float[]{
				/*vvv*/-aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,1.0f, /*cc*/ 0.0f,0.0f,
				/*vvv*/ aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,1.0f, /*cc*/ 1.0f,0.0f,
				/*vvv*/-aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,1.0f, /*cc*/ 0.0f,1.0f,
				/*vvv*/ aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,1.0f, /*cc*/ 1.0f,1.0f};

		BaseRenderer ren = RendererManager.getRenderer();
		mDatHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		Matrix.setIdentityM(mModelMatrix, 0);
	}
	
	public Rectangle(Point dim, float texScale) {
		this.mDimension = dim;
		float aspectWidth = ((float)dim.x)/((float)dim.y);

		mVertices = new float[]{
				/*vvv*/-aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f, 0.0f,
				/*vvv*/ aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ texScale,0.0f,
				/*vvv*/-aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f, texScale,
				/*vvv*/ aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ texScale, texScale};

		BaseRenderer ren = RendererManager.getRenderer();
		mDatHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		Matrix.setIdentityM(mModelMatrix, 0);
	}
	
	public Rectangle(Point spriteDim, Vec2 spriteCo, Vec2 sheetDim) {
		this.mDimension = spriteDim;
//		float aspectWidth = ((float)spriteDim.x)/((float)spriteDim.y);
		float left = spriteCo.getX() / sheetDim.getX();
		float right = (spriteCo.getX() + spriteDim.x) / sheetDim.getX();
		float top = spriteCo.getY() / sheetDim.getY();
		float bottom = (spriteCo.getY() + spriteDim.y) / sheetDim.getY();

		float halfW = ((float)spriteDim.x)/2.0f;
		float halfh = ((float)spriteDim.y)/2.0f;
		
//		mVertices = new float[]{
//				/*vvv*/-aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ left, top,
//				/*vvv*/ aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ right, top,
//				/*vvv*/-aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ left, bottom,
//				/*vvv*/ aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ right, bottom};
		
		mVertices = new float[]{
				/*vvv*/-halfW,  halfh, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ left, top,
				/*vvv*/ halfW,  halfh, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ right, top,
				/*vvv*/-halfW, -halfh, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ left, bottom,
				/*vvv*/ halfW, -halfh, -0.5f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ right, bottom};


		BaseRenderer ren = RendererManager.getRenderer();
		mDatHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		Matrix.setIdentityM(mModelMatrix, 0);
	}
	
	public Point getDimension() {
		return mDimension;
	}
	
	@Override
	public int getDatBufHandle() {
		return mDatHandle;
	}

	@Override
	public int getIdxBufHandle() {
		return mIdxHandle;
	}

	@Override
	public int getNrmSize() {
		return this.mNrmSize;
	}

	@Override
	public int getTxcSize() {
		return this.mTxcSize;
	}

	@Override
	public int getNrmOffset() {
		return this.mNrmOffset;
	}

	@Override
	public int getTxcOffset() {
		return this.mTxcOffset;
	}

	@Override
	public List<Light> getLights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPosSize() {
		return this.mPosSize;
	}

	@Override
	public int getNumElements() {
		return this.mNumElements;
	}

	@Override
	public int getElementStride() {
		return this.mElementStride;
	}

	@Override
	public int getPosOffset() {
		return this.mPosOffset;
	}
	
}
