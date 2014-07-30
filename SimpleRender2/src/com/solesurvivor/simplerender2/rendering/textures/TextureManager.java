package com.solesurvivor.simplerender2.rendering.textures;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.RendererManager;

public class TextureManager {
	
	private static final String TAG = TextureManager.class.getSimpleName();

	private static Map<String,Integer> mTextures = new HashMap<String,Integer>();
	
	public static void init() {
		Log.i(TAG, String.format("Supports ETC1 Textures: %s", ETC1Util.isETC1Supported()));
		loadTextures();
	}
	
	public static int getTextureId(String name) {
		return mTextures.get(name);		
	}

	private static void loadTextures() {

		Resources res =  GameGlobal.inst().getContext().getResources();
		BaseRenderer ren = RendererManager.inst().getRenderer();

		TypedArray textures = res.obtainTypedArray(R.array.textures);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		for(int i = 0; i < textures.length(); i++) {			
			int resourceId = textures.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
			
			mTextures.put(resourceName, ren.loadTexture(bitmap));	
			
			bitmap.recycle();
			
			Log.d(TAG, String.format("Loaded texture %s", resourceName));
		}

		textures.recycle();
	}
}
