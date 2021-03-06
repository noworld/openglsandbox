package com.solesurvivor.simplerender2_5.rendering;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

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
		Integer shader = null;

		if(StringUtils.isNotBlank(name)) {
			shader = mShaders.get(name);
		}
		
		if(shader == null) {
			shader = -1;
		}
		
		return shader;
	}
	
	public static int getShaderId(String name, String... alternate) {
		Integer shader = getShaderId(name);
		
		if(shader != null && shader > -1) {
			return shader;
		} else {
			for(String s : alternate) {
				if(StringUtils.isNotBlank(s)) {
					shader = getShaderId(s);
					if(shader != null) break;
				}
			}
		}
		
		if(shader == null) {
			shader = -1;
		}
		
		return shader;
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
