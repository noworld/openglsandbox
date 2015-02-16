package com.solesurvivor.simplerender2_5.scene;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.io.GeometryIO;
import com.solesurvivor.util.logging.SSLog;


public class MapGrid extends NodeImpl {
	
	@SuppressWarnings("unused")
	private static final String TAG = MapGrid.class.getSimpleName();
	private static final float[] TILE_TRANS_MATRIX = new float[128];
	private static final int[] OFFSETS = new int[]{0,16,32,48,64,80,96,112};
	private static final int[] OPPOSITE = new int[]{4,5,6,7,0,1,2,3};
	private static final float TILE_SIZE = 8.0f;
	private static final int generationDepth = 3;
	
	static {
		
		for(int i = 0; i < TILE_TRANS_MATRIX.length; i += 16) {			
			Matrix.setIdentityM(TILE_TRANS_MATRIX, i);
		}
		
		float x = 0.0f;
		float y = 0.0f;
		float z = TILE_SIZE;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[0], x, y, z);

		x = TILE_SIZE;
		z = TILE_SIZE;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[1], x, y, z);

		x = TILE_SIZE;
		z = 0.0f;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[2], x, y, z);

		x = TILE_SIZE;
		z = -TILE_SIZE;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[3], x, y, z);
		
		x = 0.0f;
		z = -TILE_SIZE;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[4], x, y, z);

		x = -TILE_SIZE;
		z = -TILE_SIZE;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[5], x, y, z);

		x = -TILE_SIZE;
		z = 0.0f;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[6], x, y, z);

		x = -TILE_SIZE;
		z = TILE_SIZE;
		Matrix.translateM(TILE_TRANS_MATRIX, OFFSETS[7], x, y, z);
	}
	
	
	protected TerrainTile head = null;
	protected TerrainTile current = null;
	protected Set<TerrainTile> renderSet;
	protected int renderDepth = 10;

	public MapGrid() throws IOException {
		renderSet = new HashSet<TerrainTile>();
		Geometry floor = GeometryIO.loadGeometryMap(R.raw.tile1).get("tile_floor.001");
		head = new TerrainTile(floor);
		current = head;
		addAdjacentTiles(head, floor, 0);	
		Geometry wall = GeometryIO.loadGeometryMap(R.raw.tile1).get("wall_n_ext.001");
		GeometryNode gn =  new GeometryNode(wall);
		head.addChild(gn);
		SSLog.d(TAG, "Num Tiles: %s", renderSet.size());
	}
	
	@Override
	public void update() {
		
		super.update();
	}
	
	@Override
	public void render() {

		for(TerrainTile t : renderSet) {
			t.render();
		}
		
		renderChildren();
	}
	
	// NW7 N-0 NE1
	// W-6 ttt E-2
	// SW5 S-4 SE3
	protected void addAdjacentTiles(TerrainTile tile, Geometry geo, int level) {
		
		if(level >= generationDepth) {
			return;
		}
		
		int nextLevel = level + 1;
		
		//N-0
		for(int offset = 0; offset < OFFSETS.length; offset++) {
			if(tile.getAdjacent(offset) == null) {
				TerrainTile n = new TerrainTile(geo);
				tile.setAdjacent(offset, n);
				n.setAdjacent(OPPOSITE[offset], tile);

				Matrix.setIdentityM(n.mTransMatrix, 0);
				Matrix.multiplyMM(n.mTransMatrix, 0, TILE_TRANS_MATRIX, OFFSETS[offset], tile.getWorldMatrix(), 0);

				addAdjacentTiles(n, geo, nextLevel);
			}
		}
		
		renderSet.add(tile);

	}

}
