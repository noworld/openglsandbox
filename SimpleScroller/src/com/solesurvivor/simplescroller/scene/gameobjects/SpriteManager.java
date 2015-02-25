package com.solesurvivor.simplescroller.scene.gameobjects;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.JsonReader;
import android.util.JsonToken;

import com.solesurvivor.simplescroller.R;
import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec2;

public class SpriteManager {

	private static final String FRAMES_OBJ = "frames";
	private static final String FRAME_OBJ = "frame";
	private static final String META_OBJ = "meta";
	private static final String TEXTURE_OBJ = "texture";
	private static final String SIZE_OBJ = "size";
	private static final String SCALE_OBJ = "scale";
	private static final String X_OBJ = "x";
	private static final String Y_OBJ = "y";
	private static final String W_OBJ = "w";
	private static final String H_OBJ = "h";
	

	private static final String TAG = SpriteManager.class.getSimpleName();


	private static SpriteSheet sheet;
	private static Map<String,Sprite> sprites = new HashMap<String,Sprite>();

	public static void init() {		
		Resources res =  RendererManager.getContext().getResources();
		AssetManager assets = RendererManager.getAssets();

		String[] spriteResources = res.getStringArray(R.array.sprites);

		for(int i = 0; i < spriteResources.length; i++) {			
			InputStream is = null;
			JsonReader reader = null;
			List<IntermediateSprite> isl = new ArrayList<IntermediateSprite>();
			try {
				is = assets.open(spriteResources[i]);
				reader = new JsonReader(new InputStreamReader(is));
				
				while(reader.hasNext()) {
					beginJson(reader);
					JsonToken token = reader.peek();
					if(token.equals(JsonToken.NAME)) {
						String name = reader.nextName();
						if(StringUtils.equalsIgnoreCase(name, FRAMES_OBJ)) {
							isl.addAll(parseSprites(reader));
						} else if(StringUtils.equalsIgnoreCase(name, META_OBJ)) {
							parseSpriteSheet(reader);
						} else {
							SSLog.w(TAG, "Unkown name encountered parsing JSON: %s", name);
							reader.skipValue();
						}
					}

					endJson(reader); //endJson is inside while() to iterate over multiple objects
					
					if(endJsonDocument(reader)) {
						break;
					}
				}
				
				createSprites(isl);
				
			} catch (IOException e) {
				SSLog.w(TAG, "Could not open sprite resource: %s", e, spriteResources[i]);
			} finally {
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(is);
			}

		}

	}
	
	private static void createSprites(List<IntermediateSprite> isl) {		
		for(IntermediateSprite is : isl) {
			sprites.put(is.name, new Sprite(new Point(is.w, is.h), new Vec2(is.x, is.y), sheet));
		}
	}

	public static SpriteSheet getSpriteSheet() {
		return sheet;
	}
	
	public static Sprite getSprite(String name) {
		return sprites.get(name);
	}


	private static boolean endJsonDocument(JsonReader reader) throws IOException {
		return reader.peek().equals(JsonToken.END_DOCUMENT);
	}

	private static boolean beginJson(JsonReader reader) throws IOException {
		JsonToken token = reader.peek();
		boolean success = false;
		
		switch(token) {
		case BEGIN_ARRAY: reader.beginArray(); 
			success = true;
			break;
		case BEGIN_OBJECT: reader.beginObject(); 
			success = true;
			break;
		default: success = false;
			break;
		}
		
//		if(!success) {
//			SSLog.w(TAG, "BEGIN_x JSON token expected but found %s", token.toString());
//		}
		
		return success;
	}
	
	private static boolean endJson(JsonReader reader) throws IOException {
		JsonToken token = reader.peek();
		boolean success = false;
		
		switch(token) {
		case END_ARRAY: reader.endArray();
			success = true;
			break;
		case END_OBJECT: reader.endObject(); 
			success = true;
			break;
		default: success = false;
			break;
		}
		
//		if(!success) {
//			SSLog.w(TAG, "END_x JSON token expected but found %s", token.toString());
//		}
		
		return success;
	}


	private static void parseSpriteSheet(JsonReader reader) throws IOException {
		
		Point dim = null;
		String textureName = null;
		double scale = 1.0;
		
		while(reader.hasNext()) {			
			beginJson(reader);
			JsonToken token = reader.peek();
			switch(token) {
			case NAME:
				String name = reader.nextName();
				if(StringUtils.equalsIgnoreCase(name, TEXTURE_OBJ)) {
					if(reader.peek().equals(JsonToken.STRING)) {
						textureName = reader.nextString();
					}
				} else if(StringUtils.equalsIgnoreCase(name, SIZE_OBJ)) {
					dim = parseSheetSize(reader);
				} else if(StringUtils.equalsIgnoreCase(name, SCALE_OBJ)) {
					if(reader.peek().equals(JsonToken.NUMBER)) {
						scale = reader.nextDouble();
					}
				} else {
					reader.skipValue();
				}
			break;
			default: reader.skipValue();
				break;
			}			
		}
		endJson(reader); //endJson is outside while() to read a single object
		
		sheet = new SpriteSheet(dim, textureName, scale);

	}

	private static Point parseSheetSize(JsonReader reader) throws IOException {	

		int w = 0,h = 0;
		
		while(reader.hasNext()) {			
			beginJson(reader);
			JsonToken token = reader.peek();
			switch(token) {
			case NAME:
				String name = reader.nextName();
				if(StringUtils.equalsIgnoreCase(name, W_OBJ)) {
					if(reader.peek().equals(JsonToken.NUMBER)) {
						w = reader.nextInt();
					}
				} else if(StringUtils.equalsIgnoreCase(name, H_OBJ)) {
					if(reader.peek().equals(JsonToken.NUMBER)) {
						h = reader.nextInt();
					}
				} else {
					reader.skipValue();
				}
			break;
			default: reader.skipValue();
				break;
			}			
		}
		endJson(reader);				

		return new Point(w,h);
	}


	private static List<IntermediateSprite> parseSprites(JsonReader reader) throws IOException {
		
		List<IntermediateSprite> isl = new ArrayList<IntermediateSprite>();
		
		while(reader.hasNext()) {
			beginJson(reader);
			isl.add(parseSprite(reader));			
		}		
		endJson(reader);
		
		return isl;
	}

	private static IntermediateSprite parseSprite(JsonReader reader) throws IOException {

		IntermediateSprite is = new IntermediateSprite();
		
		while(reader.hasNext()) {
			beginJson(reader);
			JsonToken token = reader.peek();
			switch(token) {
			case NAME:
				String name = reader.nextName();
				if(StringUtils.equalsIgnoreCase(name, FRAME_OBJ)) {


					while(reader.hasNext()) {
						beginJson(reader);
						token = reader.peek();
						switch(token) {
						case NAME:
							name = reader.nextName();
							if(StringUtils.equalsIgnoreCase(name, X_OBJ)) {
								is.x = (float)reader.nextDouble();
							} else if(StringUtils.equalsIgnoreCase(name, Y_OBJ)) {
								is.y = (float)reader.nextDouble();
							} else if(StringUtils.equalsIgnoreCase(name, W_OBJ)) {
								is.w = reader.nextInt();
							} else if(StringUtils.equalsIgnoreCase(name, H_OBJ)) {
								is.h = reader.nextInt();
							} else {
								reader.skipValue();
							}

							break;
						default: reader.skipValue();
						break;
						}						
					}
					endJson(reader);
				} else {
					is.name = name;
				}
				break;
			default: reader.skipValue();
			break;
			}
		}
		endJson(reader);

		return is;
		
	}
	
	private static class IntermediateSprite {
		public String name;
		public float x;
		public float y;
		public int w;
		public int h;
	}
}
