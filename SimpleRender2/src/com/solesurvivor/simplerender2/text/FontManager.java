package com.solesurvivor.simplerender2.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.util.SSPropertyUtil;

public class FontManager {
	
	private static final String TAG = FontManager.class.getSimpleName();

	private static Map<String,Font> mFonts;

	private FontManager() {

	}

	public static void init() {
		mFonts = Collections.unmodifiableMap(loadFonts());
	}
	
	public static Font getFont(String name) {
		return mFonts.get(name);
	}

	private static Map<String, Font> loadFonts() {

		Resources res =  GameGlobal.inst().getContext().getResources();
		Map<String, Font> fonts = new HashMap<String,Font>();

		TypedArray fontDefinitions = res.obtainTypedArray(R.array.fonts);

		for(int i = 0; i < fontDefinitions.length(); i++) {			
			int resourceId = fontDefinitions.getResourceId(i, 0);
			Font f = loadFont(resourceId, res);
			fonts.put(f.mName, f);			
		}		

		fontDefinitions.recycle();	
		
		return fonts;
	}

	private static Font loadFont(int resourceId, Resources res) {

		InputStream is = null;
		String resourceName = res.getResourceEntryName(resourceId);
		Font font = null;
		BaseRenderer ren = RendererManager.inst().getRenderer();

		try {

			is = res.openRawResource(resourceId);
			String fontDescriptor = IOUtils.toString(is);
			Map<String,String> props = SSPropertyUtil.parseFromString(fontDescriptor);

			font = new Font(props);			
			font.mIboIndex = ren.loadToIbo(font.getIbo());
			font.mVboIndex = ren.loadToVbo(font.getVbo());

		} catch (IOException e) {
			Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return font;
	}




}
