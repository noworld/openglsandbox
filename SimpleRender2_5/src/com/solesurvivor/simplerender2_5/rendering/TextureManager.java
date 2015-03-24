package com.solesurvivor.simplerender2_5.rendering;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.util.Log;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.util.logging.SSLog;

public class TextureManager {
	
	private static final String TAG = TextureManager.class.getSimpleName();

	private static final int[] IMAGE_ORDER = {GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
		GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
		GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z,
		GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
		GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
		GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z};
	
	private static Map<String,Integer> mTextures = new HashMap<String,Integer>();
	
	public static void init() {
		Log.i(TAG, String.format("Supports ETC1 Textures: %s", ETC1Util.isETC1Supported()));
		load2DTextures();
		load2DEtc1Textures();
		loadCubeMapTextures();
		loadCubeMapEtc1Textures();
	}
	
	public static int getTextureId(String name) {
		Integer texture = null;
		
		if(StringUtils.isNotBlank(name)) {
			texture = mTextures.get(name);
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

		Resources res =  GameGlobal.inst().getContext().getResources();
		BaseRenderer ren = RendererManager.getRenderer();

		TypedArray textures = res.obtainTypedArray(R.array.textures_2d);

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
	
	private static void load2DEtc1Textures() {

		Resources res =  GameGlobal.inst().getContext().getResources();
		BaseRenderer ren = RendererManager.getRenderer();

		TypedArray textures = res.obtainTypedArray(R.array.textures_2d_etc1);

		for(int i = 0; i < textures.length(); i++) {			
			int resourceId = textures.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);
			InputStream is = res.openRawResource(resourceId);
			try {
				mTextures.put(resourceName, ren.loadTextureEtc1(is, resourceName));
			} finally {
				IOUtils.closeQuietly(is);
			}
			
			Log.d(TAG, String.format("Loaded texture %s", resourceName));
		}

		textures.recycle();
	}
	
	private static void loadCubeMapTextures() {

		Resources res =  GameGlobal.inst().getContext().getResources();
		BaseRenderer ren = RendererManager.getRenderer();

		TypedArray textures = res.obtainTypedArray(R.array.textures_cube);
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		for(int i = 0; i < textures.length(); i++) {			
			
//			each entry should have 6 cubemap textures			
			int resourceId = textures.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);

			TypedArray cubeMapImages = res.obtainTypedArray(resourceId);
			Bitmap[] cubeBitmaps = new Bitmap[cubeMapImages.length()];
			
			for(int j = 0; j < cubeMapImages.length(); j++) {
				int cubeImageId = cubeMapImages.getResourceId(j, 0);
				String cubeImageName = res.getResourceEntryName(cubeImageId);
				SSLog.d(TAG, "Loading cubemap %s: %s", j, cubeImageName);
				
				// Read in the resource
				cubeBitmaps[j] = BitmapFactory.decodeResource(res, cubeImageId, options);				
			}
			
			mTextures.put(resourceName, ren.loadCubeMap(cubeBitmaps, IMAGE_ORDER));
			
			for(Bitmap b : cubeBitmaps) {
				b.recycle();
			}
			cubeMapImages.recycle();
		
			Log.d(TAG, String.format("Loaded texture as cubemap %s", resourceName));
		}

		textures.recycle();
	}
	
	private static void loadCubeMapEtc1Textures() {

		Resources res =  GameGlobal.inst().getContext().getResources();
		BaseRenderer ren = RendererManager.getRenderer();

		TypedArray textures = res.obtainTypedArray(R.array.textures_cube_etc1);
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		for(int i = 0; i < textures.length(); i++) {			
			
//			each entry should have 6 cubemap textures			
			int resourceId = textures.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);

			TypedArray cubeMapImages = res.obtainTypedArray(resourceId);
			InputStream[] imageStreams = new InputStream[cubeMapImages.length()];
			String[] imageNames = new String[cubeMapImages.length()];
			
			for(int j = 0; j < cubeMapImages.length(); j++) {
				int cubeImageId = cubeMapImages.getResourceId(j, 0);
				String cubeImageName = res.getResourceEntryName(cubeImageId);
				SSLog.d(TAG, "Loading cubemap %s: %s", j, cubeImageName);
				
				// Read in the resource
				imageStreams[j] = res.openRawResource(cubeImageId);				
				imageNames[j] = cubeImageName;
			}
			
			try {
				mTextures.put(resourceName, ren.loadCubeMapEtc1(imageStreams, IMAGE_ORDER, imageNames));
			} finally {
				for(int k = 0; k < imageStreams.length; k++) {
					IOUtils.closeQuietly(imageStreams[k]);
				}
			}

			cubeMapImages.recycle();
		
			Log.d(TAG, String.format("Loaded texture as cubemap %s", resourceName));
		}

		textures.recycle();
	}
	
	public static Integer registerTexture(String name, Integer handle) {
		return mTextures.put(name, handle);
	}
}
