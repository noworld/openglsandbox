package com.solesurvivor.simplescroller.rendering;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.util.Log;

import com.solesurvivor.simplescroller.R;

public class TextureManager {
	
	private static final String TAG = TextureManager.class.getSimpleName();
	
	private static Map<String,Integer> textures = new HashMap<String,Integer>();
	
	public static void init() {
		Log.i(TAG, String.format("Supports ETC1 Textures: %s", ETC1Util.isETC1Supported()));
		load2DTextures();
	}
	
	public static int getTextureId(String name) {
		Integer texture = null;
		
		if(StringUtils.isNotBlank(name)) {
			texture = textures.get(name);
		}
		
		if(texture == null) {
			texture = -1;
		}
		
		return texture;		
	}
	
	public static int getTextureId(String name, String... alternate) {
		Integer texture = getTextureId(name);
		
		if(texture != null && texture > -1) {
			return texture;
		} else {
			for(String s : alternate) {
				if(StringUtils.isNotBlank(s)) {
					texture = getTextureId(s);
					if(texture != null) break;
				}
			}
		}
		
		if(texture == null) {
			texture = -1;
		}
		
		return texture;
	}

	private static void load2DTextures() {

		Resources res =  RendererManager.getContext().getResources();
		BaseRenderer ren = RendererManager.getRenderer();

		TypedArray textureResources = res.obtainTypedArray(R.array.textures_2d);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		for(int i = 0; i < textureResources.length(); i++) {			
			int resourceId = textureResources.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);

			textures.put(resourceName, ren.loadTexture(bitmap));
			
			bitmap.recycle();
			
			Log.d(TAG, String.format("Loaded texture %s", resourceName));
		}

		textureResources.recycle();
	}
	
	public static Integer registerTexture(String name, Integer handle) {
		return textures.put(name, handle);
	}
}
