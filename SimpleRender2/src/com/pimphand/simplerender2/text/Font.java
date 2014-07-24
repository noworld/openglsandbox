package com.pimphand.simplerender2.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import android.util.Log;

import com.pimphand.simplerender2.rendering.shaders.ShaderManager;
import com.pimphand.simplerender2.rendering.textures.TextureManager;
import com.solesurvivor.util.SSArrayUtil;


public class Font {
	
	public static final String UNDERSCORE = "_";
	public static final String EQUALS_CODE = "eq";
	public static final String EQUALS = "=";	
	public static final String SPACE_CODE = "space";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final int NUM_ELEMENTS = 6; 
	public static final int ELEMENTS_STRIDE = 32;
	public static final int POS_OFFSET = 0;
	public static final int NRM_OFFSET = 12;
	public static final int TXC_OFFSET = 24;
	
	private static final String TAG = Font.class.getSimpleName();
		
	public String mAsset;
	public String mName;
	
	public int mShaderHandle = 0;
	public int mTextureHandle = 0;
	public int mVboIndex = 0;
	public int mIboIndex = 0;
	
	public int mPosSize = 3;
	public int mNrmSize = 3;
	public int mTxcSize = 2;
	
	public float mAtlasWidth = 1024.0f; //Font atlas size
	public float mAtlasHeight = 1024.0f;
	
	public Map<Character,Glyph> mGlyphs = null;
	
	private List<Float> mData = new ArrayList<Float>();
	private List<Short> mIdx = new ArrayList<Short>();
	private int mRunningOffset = 0;
			
	public Font(Map<String,String> glyphProperties) {
		
		this.mAsset = glyphProperties.get(FontKeysEnum.ASSET.toString());
		this.mName = glyphProperties.get("name");
		this.mAtlasWidth = Float.parseFloat(glyphProperties.get(FontKeysEnum.ATLAS_WIDTH.toString()));
		this.mAtlasHeight = Float.parseFloat(glyphProperties.get(FontKeysEnum.ATLAS_HEIGHT.toString()));
		this.mShaderHandle = ShaderManager.getShaderId(glyphProperties.get(FontKeysEnum.SHADER_NAME.toString()));
		this.mTextureHandle = TextureManager.getTextureId(glyphProperties.get(FontKeysEnum.TEXTURE_NAME.toString()));
		
		Log.d(TAG, String.format("Loading %s font atlas: %s x %s", mName, mAtlasWidth, mAtlasHeight));
		
		mGlyphs = new HashMap<Character,Glyph>(glyphProperties.size());
		String glpyhPrefix = FontKeysEnum.GLYPH.toString() + UNDERSCORE;		
		for(String s : glyphProperties.keySet()) {
			if(s.startsWith(glpyhPrefix)) {
				String values = glyphProperties.get(s);
				String glyph = s.substring(glpyhPrefix.length());
				
				//If length is 1, then we have a single character
				//Else, we have a code
				if(glyph.length() > 1) {
					if(glyph.equals(EQUALS_CODE)) {
						glyph = EQUALS;
					} else if(glyph.equals(SPACE_CODE)) {
						glyph = SPACE;
					}
				}				
				
				mGlyphs.put(glyph.charAt(0), loadGlyphIntoList(glyph.charAt(0), values));
			}
		}
			
	}
	
	public Glyph getGlyph(char glyph) {
		return this.mGlyphs.get(glyph);
	}
	
	public byte[] getVbo() {
		return SSArrayUtil.floatToByteArray(ArrayUtils.toPrimitive(mData.toArray(new Float[mData.size()])));
	}
	
	public byte[] getIbo() {
		return SSArrayUtil.shortToByteArray(ArrayUtils.toPrimitive(mIdx.toArray(new Short[mIdx.size()])));
	}
	
	private Glyph loadGlyphIntoList(char c, String values) {
		
		
		String[] bbStr = values.split(COMMA);
		
		if(bbStr.length != 4) {
			throw new IllegalArgumentException(String.format("Glyphs must have exactly left, top, right, bottom. Encountered: %s", values));
		}
		
		float[] bb = new float[4];
		bb[0] = Float.parseFloat(bbStr[0]);
		bb[1] = Float.parseFloat(bbStr[1]);
		bb[2] = Float.parseFloat(bbStr[2]);
		bb[3] = Float.parseFloat(bbStr[3]);
		
		Glyph glyph = new Glyph();
		glyph.mGlyph = c;
		glyph.mWidth = bb[2] - bb[0];
		glyph.mOffset = mRunningOffset;
		
		loadGlyphToVboList(bb);
		loadGlyphToIboList();
		
		return glyph;
	}
	
	private void loadGlyphToIboList() {
		//Each glyph is drawn on a rectangle	
		//2 triangles = 6 vertices
		for(int i = 0; i < NUM_ELEMENTS; i++) {			
			mIdx.add((short)(mRunningOffset++));
		}
	}

	private void loadGlyphToVboList(float[] bb) {
		
		float bbLeft = bb[0];
		float bbTop = bb[1];
		float bbRight = bb[2];
		float bbBottom = bb[3];
		
		float top = bbTop / mAtlasHeight;
		float bottom = bbBottom / mAtlasHeight;
		float left = bbLeft / mAtlasWidth;
		float right = bbRight / mAtlasWidth;
		
		float halfW = (bbRight - bbLeft)/2f;
		float halfH = (bbBottom - bbTop)/2f;
		
//		Bottom Left Vertex
		//Pos
		mData.add(-(halfW));
		mData.add(-(halfH));
		mData.add(0.0f);		
		//Nrm
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(1.0f);
		//Txc
		mData.add(left);
		mData.add(bottom);
		
//		Bottom Right
		mData.add((halfW));
		mData.add(-(halfH));
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(1.0f);
		mData.add(right);
		mData.add(bottom);
	
//		Top Right
		mData.add((halfW));
		mData.add((halfH));
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(1.0f);
		mData.add(right);
		mData.add(top);
		
//		Top Left
		mData.add(-(halfW));
		mData.add((halfH));
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(1.0f);
		mData.add(left);
		mData.add(top);
		
//		Bottom Left
		mData.add(-(halfW));
		mData.add(-(halfH));
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(1.0f);
		mData.add(left);
		mData.add(bottom);
		
//		Top Right
		mData.add((halfW));
		mData.add((halfH));
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(0.0f);
		mData.add(1.0f);
		mData.add(right);
		mData.add(top);

	}
	
}
