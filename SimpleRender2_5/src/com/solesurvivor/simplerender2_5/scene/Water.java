package com.solesurvivor.simplerender2_5.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.io.GeometryIO;
import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class Water implements Node {
	
	private static final float TILE_SIZE = 40;
	private static final float HALF_TILE = TILE_SIZE/2.0f; 
	private static final int WATER_SIZE = 1;
	private static final int HALF = WATER_SIZE/2;
	private static final float DISP = (float)(WATER_SIZE - HALF);
	private static final int L1_DIST = 15; 
	private static final int L2_DIST = 30;
	
	protected Geometry[] geos;
	protected WaterTile[][] tiles;
	protected List<Wave> waves;
	
	public Water() throws IOException {
		 Map<String,Geometry> waters = GeometryIO.loadGeometryMap(R.raw.circle_water);
		 
		 geos = new Geometry[waters.size()];
		 
		 Light l = new Light();
		 l.mPosition = new float[]{0.0f, 10.0f, 0.0f, 0.0f};
		 
		 for(int i = 0; i < geos.length; i++) {
//			 geos[i] = waters.get("water_plane_" + String.valueOf(i));
			 geos[i] = (Geometry)waters.values().toArray()[i];
			 geos[i].addLight(l);
		 }
		 
		 tiles = new WaterTile[WATER_SIZE][WATER_SIZE];
		 
		 for(int i = 0; i < tiles.length; i++) {
			 for(int j = 0; j < tiles[i].length; j++) {
				 tiles[i][j] = new WaterTile();
				 Matrix.setIdentityM(tiles[i][j].modelMatrix, 0);
				 Matrix.translateM(tiles[i][j].modelMatrix, 0, DISP + (TILE_SIZE * i), 0.0f, TILE_SIZE * j);
				 //Center of tile
				 tiles[i][j].x = (TILE_SIZE * (DISP+i)) + HALF_TILE;
				 tiles[i][j].z = (TILE_SIZE * (DISP+j)) + HALF_TILE;
			 }
		 }

		 waves = new ArrayList<Wave>(1);
		 
		 float tsc = 0.5f;
		 Wave wave = new Wave(0.05f, new Vec3(0.0f, 0.0f, 1.0f), 5.0f);		 
		 Wave wave2 = new Wave(0.02f, new Vec3(0.25f, 0.0f, 0.70f), 10.6186f);
		 wave.setTimeScale(tsc);
		 wave2.setTimeScale(tsc);
		 waves.add(wave);
		 waves.add(wave2);
	}

	@Override
	public void update() {
//		Camera c = RendererManager.getRenderer().getCurrentCamera();
//		
//		if(c.mViewDirty) {
//			Vec3 cameraPos = c.getAgentTranslation();
//
//			for(int i = 0; i < tiles.length; i++) {
//				for(int j = 0; j < tiles[i].length; j++) {
//					double xDist = (cameraPos.getX() - tiles[i][j].x);
//					double zDist = (cameraPos.getZ() - tiles[i][j].z);
//					double dist = Math.sqrt((xDist*xDist) + (zDist*zDist));
//					
//					if(i == 0 && j == 0) {
//						SSLog.d("LOD", "Distance to 0,0 tile: %s", dist);
//						SSLog.d("CAM", "Camera loc: %.3f,%.3f", cameraPos.getX(), cameraPos.getZ());
//						SSLog.d("TIL", "Tile loc: %.3f,%.3f", tiles[i][j].x, tiles[i][j].z);
//					}
//
//				}
//			}
//
//		}
	}

	@Override
	public void render() {
		BaseRenderer ren = RendererManager.getRenderer(); 
		
		 for(int i = 0; i < tiles.length; i++) {
			 for(int j = 0; j < tiles[i].length; j++) {
				 ren.drawWater(geos[tiles[i][j].level], tiles[i][j].modelMatrix, waves);
			 }
		 }
	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}

	public class WaterTile {
		public int level = 0;
		public float[] modelMatrix = new float[16];
		public float x;
		public float z;
	}

	@Override
	public void scale(Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(Vec3 trans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getWorldMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getTransMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetTranslation() {
		// TODO Auto-generated method stub
		
	}
	
}
