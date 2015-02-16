package com.solesurvivor.simplerender2_5.scene;

import java.util.ArrayList;
import java.util.List;

import com.solesurvivor.simplerender2_5.rendering.GridMapRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;

public class GeometryNode extends StatefulNodeImpl {

	@SuppressWarnings("unused")
	private static final String TAG = GeometryNode.class.getSimpleName();
	
	protected Geometry mGeometry = null;
	protected GridMapRenderer mRen = null;
	
//	protected float[] mWorldMatrix;
	protected int mShaderHandle;
	protected int mTextureHandle;
	protected List<Light> mLights;
	
//	protected Vec3 mTransDir;
//	protected Vec3 mScaleFac;
//	protected Vec3 mRotAxes;
//	protected float mRotAngle;	
	
//	protected boolean mDirty = true;
	
	protected GeometryNode() {
		mWorldMatrix = new float[16];		
		mLights = new ArrayList<Light>();
//		resetTransforms();
	}
	
	public GeometryNode(Geometry geometry, int shaderHandle, int textureHandle) {
		this();
		this.mShaderHandle = shaderHandle;
		this.mTextureHandle = textureHandle;
		this.mGeometry = geometry;
		this.mRen = (GridMapRenderer)RendererManager.getRenderer();
	}
	
	public GeometryNode(Geometry geometry) {
		this(geometry, geometry.getShaderHandle(), geometry.getTextureHandle());
	}

	@Override
	public void render() {
		mRen.drawGeometry(mGeometry, this.getWorldMatrix());
		renderChildren();
	}
	
//	public float[] getWorldMatrix() {
//		if(mDirty) {
//			applyTransforms();
//		}
//		return mWorldMatrix;
//	}
	
//	public void rotate(float angle, Vec3 dir) {
//		if(mRotAxes == null) {
//			mRotAxes = dir.normalizeClone();
//		} else {
//			mRotAxes.add(dir);
//			mRotAxes.normalize();
//		}
//		
//		mRotAngle += angle;
//		mDirty = true;
//	}
//	
//	public void translate(Vec3 dir) {
//		mTransDir.add(dir);
//		mDirty = true;
//	}
	
//	public void scale(Vec3 fac) {
//		mScaleFac.componentScale(fac);
//		mDirty = true;
//	}
//	
//	public void resetTransforms() {
//		Matrix.setIdentityM(mWorldMatrix, 0);
//		mTransDir = new Vec3(0.0f,0.0f,0.0f);
//		mScaleFac = new Vec3(1.0f,1.0f,1.0f);
//		mRotAxes = null;
//		mRotAngle = 0.0f;	
//		mDirty = false;
//	}
	
//	public void applyTransforms() {
//		Matrix.setIdentityM(mWorldMatrix, 0);		
//		Matrix.translateM(mWorldMatrix, 0, mTransDir.getX(), mTransDir.getY(), mTransDir.getZ());
//		if(mRotAxes != null) {
//			Matrix.rotateM(mWorldMatrix, 0, mRotAngle, mRotAxes.getX(), mRotAxes.getY(), mRotAxes.getZ());
//		}
//		Matrix.scaleM(mWorldMatrix, 0, mScaleFac.getX(), mScaleFac.getY(), mScaleFac.getZ());
//
//		mDirty = false;
//	}
//
//	@Override
//	public boolean isDirty() {
//		return mDirty;
//	}
	
}
