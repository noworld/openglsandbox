package com.solesurvivor.pthirtyeight.scene.gameobjects;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.SparseArray;

import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.pthirtyeight.scene.gameobjects.levels.GameMap;
import com.solesurvivor.pthirtyeight.scene.gameobjects.levels.MapCell;
import com.solesurvivor.simplescroller.R;
import com.solesurvivor.util.json.JsonUtils;
import com.solesurvivor.util.logging.SSLog;

public class MapManager {
	
	private static final String MAPS_OBJ = "maps";
	private static final String SIZE_OBJ = "size";
	private static final String ROW_INDEX_OBJ = "row_index";	
	private static final String CELLS_OBJ = "cells";
	private static final String CELL_INDEX_OBJ = "cell_index";
	private static final String ID_OBJ = "id";
	private static final String META_OBJ = "meta";
	private static final String SCALE_OBJ = "scale";
	private static final String MAPSET_OBJ = "mapset";
	private static final String CAMPAIGN_OBJ = "campaign";
	private static final String SPRITE_OBJ = "sprite";
	private static final String X_OBJ = "x";
	private static final String Y_OBJ = "y";
	private static final String W_OBJ = "w";
	private static final String H_OBJ = "h";
	private static final int CELL_SIZE = 128;
	
	private static final String TAG = MapManager.class.getSimpleName();

	private static Map<String,GameMap> maps;
	
	public static void init() {
		maps = new HashMap<String,GameMap>();
		JsonUtils.setDebug(false);
		SSLog.d(TAG, "MapManager initialized.");
	}
	
	public static GameMap getGameMap(String mapName) {
		GameMap gm = maps.get(mapName);
		
		if(gm == null) {
			gm = loadMap(mapName);
			maps.put(mapName, gm);
		}
		
		return gm;
	}

	private static GameMap loadMap(String mapName) {

		Resources res =  RendererManager.getContext().getResources();
		AssetManager assets = RendererManager.getAssets();

		String[] mapResources = res.getStringArray(R.array.game_levels);
		GameMap gm = null;
		
		for(int i = 0; i < mapResources.length; i++) {			
			InputStream is = null;
			JsonReader reader = null;
			try {
				is = assets.open(mapResources[i]);
				reader = new JsonReader(new InputStreamReader(is));
				
				while(reader.hasNext()) {
					JsonUtils.beginJson(reader);
					JsonToken token = reader.peek();
					if(token.equals(JsonToken.NAME)) {
						String name = reader.nextName();
						
						if(StringUtils.equalsIgnoreCase(name, MAPS_OBJ)) {
							if(containsMapsObj(mapName, reader)) {
								gm = parseGameMap(reader);
							}
						} else if(StringUtils.equalsIgnoreCase(name, META_OBJ)) {
							parseMetaObj(reader);
						} else {
							SSLog.w(TAG, "Unkown name encountered parsing JSON: %s", name);
							reader.skipValue();
						}
					}

					JsonUtils.endJson(reader); //endJson is inside while() to iterate over multiple objects
					
					if(JsonUtils.endJsonDocument(reader)) {
						break;
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(is);
			}
			
		}
		
		return gm;
	}

	private static void parseMetaObj(JsonReader reader) throws IOException {
		
		while(reader.hasNext()) {
			JsonUtils.beginJson(reader);
			if(JsonUtils.endJson(reader)) {
				break;
			} else {
				reader.skipValue();
			}
		}		
		//JsonUtils.endJson(reader);
	}
	
	private static boolean containsMapsObj(String mapName, JsonReader reader) throws IOException {
		while(reader.hasNext()) {
			JsonUtils.beginJson(reader);
			
			JsonToken token = reader.peek();
			switch(token) {
			case NAME:
				String name = reader.nextName();
				if(StringUtils.equalsIgnoreCase(name, mapName)) {
					return true;
				}
			default: reader.skipValue();
			break;
			}	
		}		
		JsonUtils.endJson(reader);
		
		return false;
	}

	private static GameMap parseGameMap(JsonReader reader) throws IOException {

		SparseArray<SparseArray<MapCell>> mapCells = null;
		Point mapSize = null;
		
		while(reader.hasNext()) {
			JsonUtils.beginJson(reader); //BEGIN ARRAY
			
			JsonToken token = reader.peek();
			SSLog.d(TAG, "TOKEN: %s", token.toString());
			
			switch(token) {
			case NAME:
				String name = reader.nextName();
				if(StringUtils.equalsIgnoreCase(name, SIZE_OBJ)) {
					mapSize = parseMapSize(reader);
				} else if(StringUtils.equalsIgnoreCase(name, CELLS_OBJ)) {
					mapCells = parseMapCells(reader);
				}
				break;
			default: reader.skipValue();
			break;
			}					
			
		}
		JsonUtils.endJson(reader);
		
		MapCell[][] cells = null;

		if(mapSize != null) {
			cells = new MapCell[mapSize.y][mapSize.x];

			for(int i = 0; i < mapSize.y; i++) {
				for(int j = 0; j < mapSize.x; j++) {
					MapCell cell = mapCells.get(i).get(j);

					if(cell != null) {
						cells[i][j] = cell;
					} else {
						SSLog.w(TAG, "No map cell found at location [%s,%s]", i, j);
					}
				}
			}
		} else {
			SSLog.w(TAG, "No map size found.");
		}

		return new GameMap(CELL_SIZE,cells);
	}

	private static SparseArray<SparseArray<MapCell>> parseMapCells(JsonReader reader) throws IOException {
		SparseArray<SparseArray<MapCell>> mapCells = new SparseArray<SparseArray<MapCell>>();
		
		int currentRowIndex = -1;
		SparseArray<MapCell> row = null;

		while(reader.hasNext()) {
			JsonUtils.beginJson(reader);
			JsonToken token0 = reader.peek();
			SSLog.d(TAG, "TOKEN: %s", token0.toString());
			while(reader.hasNext()) {
				JsonUtils.beginJson(reader);
				JsonToken token = reader.peek();
				SSLog.d(TAG, "TOKEN: %s", token.toString());
				switch(token) {
				case NAME:
					String name = reader.nextName();
					if(StringUtils.equalsIgnoreCase(name, ROW_INDEX_OBJ)) {
						currentRowIndex = reader.nextInt();
					} else if(StringUtils.equalsIgnoreCase(name, CELLS_OBJ)) {
						row = parseMapRow(reader);
					} else {
						reader.skipValue();
					}

					break;
				default: reader.skipValue();
				break;
				}	

			}
			JsonUtils.endJson(reader);
			
			mapCells.append(currentRowIndex, row);

		}
		JsonUtils.endJson(reader);
		
		return mapCells;
	}

	private static SparseArray<MapCell> parseMapRow(JsonReader reader) throws IOException {
		SparseArray<MapCell> row = new SparseArray<MapCell>();

		while(reader.hasNext()) {
			JsonUtils.beginJson(reader);
			JsonToken token0 = reader.peek();
			SSLog.d(TAG, "TOKEN: %s", token0.toString());

			int cellIndex = -1;
			String id = null;
			String spriteName = null;

			while(reader.hasNext()) {
				JsonUtils.beginJson(reader);
				JsonToken token = reader.peek();
				SSLog.d(TAG, "TOKEN: %s", token.toString());
				switch(token) {
				case NAME:
					String name = reader.nextName();
					if(StringUtils.equalsIgnoreCase(name, CELL_INDEX_OBJ)) {
						cellIndex = reader.nextInt();
					} else if(StringUtils.equalsIgnoreCase(name, ID_OBJ)) {
						id = reader.nextString();
					} else if (StringUtils.equalsIgnoreCase(name, SPRITE_OBJ)) {
						spriteName = reader.nextString();
					} else {
						reader.skipValue();
					}

					break;
				default: reader.skipValue();
				break;
				}
			}
			JsonUtils.endJson(reader);
			
			row.append(cellIndex, new MapCell(spriteName,id));

		}
		JsonUtils.endJson(reader);
		
		return row;
	}

	private static Point parseMapSize(JsonReader reader) throws IOException {

		Point p = new Point();
		
		while(reader.hasNext()) {
			JsonUtils.beginJson(reader);
			JsonToken token = reader.peek();
			SSLog.d(TAG, "TOKEN: %s", token.toString());
			switch(token) {
			case NAME:
				String name = reader.nextName();
				if(StringUtils.equalsIgnoreCase(name, X_OBJ)) {
					p.x = reader.nextInt();
				} else if(StringUtils.equalsIgnoreCase(name, Y_OBJ)) {
					p.y = reader.nextInt();
				} else {
					reader.skipValue();
				}

				break;
			default: reader.skipValue();
			break;
			}						
		}
		JsonUtils.endJson(reader);
		
		return p;
	}


	
}
