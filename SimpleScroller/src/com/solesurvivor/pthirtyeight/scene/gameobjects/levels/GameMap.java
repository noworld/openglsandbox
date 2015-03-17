package com.solesurvivor.pthirtyeight.scene.gameobjects.levels;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.solesurvivor.pthirtyeight.rendering.RendererManager;


public class GameMap {

	protected MapCell[][] cells;
	protected int cellSize;
	protected Point oldViewport;
	protected List<MapCell> visibleCells;
	protected int visibleRows;
	protected int visibleColumns;
	int oldCenterRow = -1;
	
	public GameMap(int cellSize, MapCell[][] cells) {
		this.cellSize = cellSize;
		this.cells = cells;
		visibleCells = new ArrayList<MapCell>();
	}
	
	public float getCellSize() {
		return cellSize;
	}
	
	public MapCell[][] getCells() {
		return cells;
	}
	
	public List<MapCell> getViewable() {
		return visibleCells;
	}
	
	public void calcViewable(Point mapCenter) {
		//TODO: Make the viewport changes everywhere
		//event driven. interface ViewportAware
		if(oldViewport == null) {
			oldViewport = RendererManager.getViewport();
			visibleRows = (oldViewport.y / cellSize);
			visibleColumns = (oldViewport.x / cellSize);
		} else {
			Point viewport = RendererManager.getViewport();
			if(!oldViewport.equals(viewport)){ 
				visibleRows = (viewport.y / cellSize);
				visibleColumns = (viewport.x / cellSize);
			}
		}

		int centerRow =  Math.max(0, mapCenter.y / cellSize / 2);
				
		if(centerRow != oldCenterRow) {
			oldCenterRow = centerRow;
			int lowRow = Math.max(0, centerRow - (visibleRows / 2) - 1); //-1 to make sure we extend to the edge
			int highRow = Math.min(cells.length, lowRow + visibleRows);
			
			visibleCells.clear();
			
			for(int i = lowRow; i < highRow; i++) {
				for(int j = 0; j < cells[i].length; j++) {
					visibleCells.add(cells[i][j]);
				}
			}
		}
	}
	
	
}
