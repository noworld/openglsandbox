package com.solesurvivor.simplerender2_5.scene;

import java.util.List;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.util.SSArrayUtil;

public abstract class Geometry_16_9 implements Drawable {

	@SuppressWarnings("unused")
	private static final String TAG = Geometry_16_9.class.getSimpleName();
	
	public static final Point DIMENSION = new Point(1920,1080);
	public static final float ASPECT_HEIGHT = 1.0f;
	public static final float ASPECT_WIDTH = 16.0f/9.0f;

	protected int mDatHandle;
	protected int mIdxHandle;
	protected float[] mVertices = {
			/*vvv*/-ASPECT_WIDTH,  ASPECT_HEIGHT, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,0.0f,
			/*vvv*/ ASPECT_WIDTH,  ASPECT_HEIGHT, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,0.0f,
			/*vvv*/-ASPECT_WIDTH, -ASPECT_HEIGHT, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,1.0f,
			/*vvv*/ ASPECT_WIDTH, -ASPECT_HEIGHT, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,1.0f};
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
	
	public Geometry_16_9() {
		BaseRenderer ren = RendererManager.getRenderer();
		mDatHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(mVertices));
		mIdxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(mIndexes));
		Matrix.setIdentityM(mModelMatrix, 0);
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
	
	@Override
	public float[] getModelMatrix() {
		return this.mModelMatrix;
	}
}
