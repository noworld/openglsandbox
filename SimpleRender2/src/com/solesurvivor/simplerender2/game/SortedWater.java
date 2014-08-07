package com.solesurvivor.simplerender2.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.opengl.Matrix;
import android.util.Log;
import android.util.SparseArray;

import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.DrawingConstants;
import com.solesurvivor.simplerender2.rendering.Geometry;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.simplerender2.rendering.shaders.ShaderManager;
import com.solesurvivor.simplerender2.rendering.textures.TextureManager;
import com.solesurvivor.simplerender2.rendering.water.Wave;
import com.solesurvivor.simplerender2.scene.Water;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec2;
import com.solesurvivor.util.math.Vec3;

public class SortedWater  {

//	@SuppressWarnings("unused")
	private static final String TAG = SortedWater.class.getSimpleName();
	
	private static final boolean DEBUG_MESH_DATA = true;
	private static final float DEPTH = 12.0f;
	private static final float WIDTH = 12.0f;
	private static final int DIV = 32;
	private static final int NUM_PRIMS = DIV*DIV*2;
	private static final int NUM_VERTS = (DIV+1)*(DIV+1);
	private static final float Y_VAL = 0.0f;
	private static final int TRI = 3;
	private static final int VERTEX_DATA_SIZE = 3;
	private static final int TEXTURE_DATA_SIZE = 2;
	private static final int QUAD = 4;
	private static final int START_INDEX = 0;
	
	private Water mWater;
	private int mIboHandle;
	private int mVboHandle;
	private SparseArray<Vec3> mIndexedVerts;
	private SparseArray<Vec2> mIndexedTxc;
	private List<SortablePrim> mSortedPrims;
	private Geometry mGeometry;
	
	public SortedWater() {
		List<Wave> waves = new ArrayList<Wave>(1);
		Wave w = new Wave();
		w.setDirection(new Vec3(0.0f,0.0f,1.0f));
		w.setAmplitude(0.5f);
		w.setWavelength(1.5f);
		waves.add(w);	
		mGeometry = new Geometry();
		mGeometry.mName = "SortedWater";
		mGeometry.mAssetXml = "<asset>SortedWater</asset>";
		mGeometry.mPosSize = VERTEX_DATA_SIZE;
		mGeometry.mNrmSize = -1;
		mGeometry.mTxcSize = TEXTURE_DATA_SIZE;
		
		mGeometry.mPosOffset = 0;
		mGeometry.mNrmOffset = -1;
		mGeometry.mTxcOffset = VERTEX_DATA_SIZE * DrawingConstants.BYTES_PER_FLOAT;
		
		mGeometry.mElementStride = (VERTEX_DATA_SIZE + TEXTURE_DATA_SIZE) * DrawingConstants.BYTES_PER_FLOAT;
		mGeometry.mShaderHandle = ShaderManager.getShaderId("water_shader");
		mGeometry.mTextureHandle = TextureManager.getTextureId("quads");
		
		buildMesh();
		loadVboGeometry();		
		loadIboGeometry();
		mWater = new Water(waves, mGeometry, TextureManager.getTextureId("quads"), ShaderManager.getShaderId("water_shader"));
		Matrix.translateM(mGeometry.mModelMatrix, 0, 0.0f, -1.0f, -5.0f);
	}

	public Water getWater() {
		return mWater;
	}
	
	private void loadVboGeometry() {
		float[] vboFloats = new float[(mIndexedVerts.size()*VERTEX_DATA_SIZE)
		                              + (mIndexedTxc.size()*TEXTURE_DATA_SIZE)];
		for(int i = 0, j = 0; i < mIndexedVerts.size(); i++, j += (VERTEX_DATA_SIZE + TEXTURE_DATA_SIZE)) {
			Vec3 vert = mIndexedVerts.get(i);
			Vec2 txc = mIndexedTxc.get(i);
			if(vert == null || txc == null) {
				throw new NullPointerException(String.format("Could not find vertex data building SortedWater mesh. (v: %s, t: %s)", vert, txc));
			}
			vboFloats[j] = vert.getX();
			vboFloats[j+1] = vert.getY();
			vboFloats[j+2] = vert.getZ();
			vboFloats[j+3] = txc.getX();
			vboFloats[j+4] = txc.getY();
		}
		byte[] vboBytes = SSArrayUtil.floatToByteArray(vboFloats);
		
		BaseRenderer ren = RendererManager.inst().getRenderer();
		mVboHandle = ren.loadToVbo(vboBytes);
		mGeometry.mDatBufIndex = mVboHandle;
	}
	
	private void loadIboGeometry() {
		Collections.sort(mSortedPrims);		
		short[] iboShorts = new short[mSortedPrims.size()*TRI];
		for(int i = 0, j = 0; i < mSortedPrims.size(); i++, j += TRI) {
			SortablePrim sp = mSortedPrims.get(i);
			iboShorts[j] = sp.mPos[0];
			iboShorts[j+1] = sp.mPos[1];
			iboShorts[j+2] = sp.mPos[2];
		}
		
		byte[] iboBytes = SSArrayUtil.shortToByteArray(iboShorts);
		
		BaseRenderer ren = RendererManager.inst().getRenderer();
		mIboHandle = ren.loadToIbo(iboBytes);
		mGeometry.mIdxBufIndex = mIboHandle;
		mGeometry.mNumElements = iboShorts.length;
		SSLog.d(TAG, "Number of Water Elements: %s", mGeometry.mNumElements);
	}
	
	private void buildMesh() {
		float startx = -(WIDTH/2);
		float startz = (DEPTH/2);
		float stepx = WIDTH/DIV;
		float stepz = DEPTH/DIV;
		
		short indexStepZ = DIV + 1;
		
		mSortedPrims = new ArrayList<SortablePrim>(NUM_PRIMS);
		mIndexedVerts = new SparseArray<Vec3>(NUM_VERTS);
		mIndexedTxc = new SparseArray<Vec2>(NUM_VERTS);
		
		for(short i = 0; i < DIV; i++) {
			for(short j = 0; j < DIV; j++) {
				float localX = startx + (stepx * j);
				float localZ = startz - (stepz * i);
				
				short[] indicies = new short[QUAD];
				indicies[0] = (short)(START_INDEX + j + i*indexStepZ);
				indicies[1] = (short)(START_INDEX + 1 + j + i*indexStepZ);
				indicies[2] = (short)(START_INDEX + (DIV+1) + j + i*indexStepZ);
				indicies[3] = (short)(START_INDEX + (DIV+2) + j + i*indexStepZ);
				
				if(DEBUG_MESH_DATA) {
					Log.d(TAG, String.format("Vertex indicies for quadrant (%s,%s): %s, %s, %s, %s",
							i, j, indicies[0], indicies[1], indicies[2], indicies[3]));
				}

				Vec3[] points = new Vec3[QUAD];
				points[0] = mIndexedVerts.get(indicies[0]) == null ? new Vec3(localX,Y_VAL,localZ) : mIndexedVerts.get(indicies[0]);
				points[1] = mIndexedVerts.get(indicies[1]) == null ? new Vec3(localX+stepx,Y_VAL,localZ) : mIndexedVerts.get(indicies[1]);
				points[2] = mIndexedVerts.get(indicies[2]) == null ? new Vec3(localX,Y_VAL,localZ-stepz) : mIndexedVerts.get(indicies[2]);
				points[3] = mIndexedVerts.get(indicies[3]) == null ? new Vec3(localX+stepx,Y_VAL,localZ-stepz) : mIndexedVerts.get(indicies[3]);
				
				Vec2[] txcs = new Vec2[QUAD];
				for(int k = 0; k < txcs.length; k++) {
					float txcX = (points[k].getX() + (WIDTH/2))/WIDTH;
					float txcZ = (points[k].getZ() + (DEPTH/2))/DEPTH;
						
					txcs[k] = new Vec2(txcX, txcZ);
				}
				
				if(DEBUG_MESH_DATA) {
					for(int k = 0; k < points.length; k++) {
						SSLog.d(TAG, "Points[%s]: %s", k, points[k].prettyString());
						SSLog.d(TAG, "Txcs[%s]: %s", k, txcs[k].prettyString());
					}
				}
				
				for(int k = 0; k < indicies.length; k++) {
					mIndexedVerts.put(indicies[k], points[k]);
					mIndexedTxc.put(indicies[k], txcs[k]);
				}
				
				//Left tri - 1,0,4
				Vec3[] lPrimPoints = {points[1], points[0], points[2]};
				short[]  lPrimIndicies = {indicies[1], indicies[0], indicies[2]};
				SortablePrim lPrim = new SortablePrim(lPrimPoints, lPrimIndicies);
				
				//Right tri - 1,4,5
				Vec3[] rPrimPoints = {points[1], points[2], points[3]};
				short[]  rPrimIndicies = {indicies[1], indicies[2], indicies[3]};
				SortablePrim rPrim = new SortablePrim(rPrimPoints, rPrimIndicies);
				
				mSortedPrims.add(lPrim);
				mSortedPrims.add(rPrim);
			}
		}
	}
	
	private class SortablePrim implements Comparable<SortablePrim> {
		public short[] mPos;
		public float mAvgZ;
		
		public SortablePrim(Vec3[] points, short[] posIndicies) {
			mAvgZ = (points[0].getZ() + points[1].getZ() + points[2].getZ())/3.0f;
			mPos = posIndicies;
		}

		@Override
		public int compareTo(SortablePrim arg0) {
			if(arg0 == null) throw new NullPointerException("Cannot compare to null. (SortablePrim)");
		
			return Float.compare(mAvgZ,arg0.mAvgZ);
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null || !(o instanceof SortablePrim)) {return false;}
			
			SortablePrim sp = (SortablePrim)o;
			return sp.mAvgZ == mAvgZ ;
		}
	}

}
