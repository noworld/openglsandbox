package com.solesurvivor.simplerender2_5.scene;



public class TerrainTile extends GeometryNode {

	@SuppressWarnings("unused")
	private static final String TAG = TerrainTile.class.getSimpleName();
	
	private static final int NUM_ADJACENT = 8;
	
	protected TerrainTile[] adjacent = new TerrainTile[NUM_ADJACENT];
	
	public TerrainTile(Geometry geometry) {
		super(geometry);
	}
	
	public void setAdjacent(int pos, TerrainTile tile) {
		adjacent[pos] = tile;
	}
	
	public TerrainTile getAdjacent(int pos) {
		return adjacent[pos];
	}

}
