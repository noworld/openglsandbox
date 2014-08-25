package com.solesurvivor.simplerender2_5.rendering;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.util.Log;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.game.GameGlobal;

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
		BaseRenderer ren = RendererManager.getRenderer();

		TypedArray textures = res.obtainTypedArray(R.array.textures);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		for(int i = 0; i < textures.length(); i++) {			
			int resourceId = textures.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
			
			if(resourceName.equals("skybox2")) {
				mTextures.put(resourceName, ren.loadTexture(bitmap, GLES20.GL_TEXTURE_CUBE_MAP));
			} else {
				mTextures.put(resourceName, ren.loadTexture(bitmap, GLES20.GL_TEXTURE_2D));
			}
				
			
			bitmap.recycle();
			
			Log.d(TAG, String.format("Loaded texture %s", resourceName));
		}

		textures.recycle();
	}
}
