package com.solesurvivor.simplerender2_5.rendering;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import com.solesurvivor.simplerender2_5.R;
import com.solesurvivor.simplerender2_5.game.GameGlobal;

public class ShaderManager {
	
	private static final String TAG = ShaderManager.class.getSimpleName();
	
	private static Map<String,Integer> mShaders = new HashMap<String,Integer>();
	
	public static void init() {
		loadShaders();
	}
	
	public static int getShaderId(String name) {
		return mShaders.get(name);
	}
	
	private static void loadShaders() {
		Resources res = GameGlobal.inst().getContext().getResources();
		BaseRenderer ren = RendererManager.getRenderer();
		
		TypedArray shaderPrograms = res.obtainTypedArray(R.array.shaderPrograms);		
		
		for(int i = 0; i < shaderPrograms.length(); i++) {			
			int resourceId = shaderPrograms.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);
			Log.d(TAG, String.format("Loading resource: %s.", resourceName));		
			
			TypedArray shaders = res.obtainTypedArray(resourceId);
			
			InputStream vShadIn = null;
			InputStream fShadIn = null;
			String vShadCode = null;
			String fShadCode = null;
			
			int vShadId = shaders.getResourceId(0, 0);		
			int fShadId = shaders.getResourceId(1, 0);

			try {
				vShadIn = res.openRawResource(vShadId);
				vShadCode = IOUtils.toString(vShadIn);
				fShadIn = res.openRawResource(fShadId);
				fShadCode = IOUtils.toString(fShadIn);
			} catch (IOException e) {
				Log.e(TAG, "Error loading shaders.", e);
			} finally {
				IOUtils.closeQuietly(vShadIn);
				IOUtils.closeQuietly(fShadIn);
			}
			
			//XXX HACK: For now, assume only 1 v and f shader
			//TODO: Reconfigure for reusing individual shaders (i.e. don't compile the same code over and over)
			mShaders.put(resourceName, ren.loadShaderProgram(new String[]{vShadCode}, new String[]{fShadCode}));
			
			shaders.recycle();
		
		}
			
		shaderPrograms.recycle();
		
		Log.d(TAG, "Shaders loaded.");
	}
	
}
