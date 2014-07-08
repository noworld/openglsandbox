package com.solesurvivor.simplerender.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.util.SSArrayUtil;


public class Font {
	
	public static final String GLYPH_PREFIX = "glyph_";
	public static final String EQUALS_CODE = "eq";
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
	
	public Map<Character,Integer> mGlyphs = null;
	
	private List<Float> mData = new ArrayList<Float>();
	private List<Short> mIdx = new ArrayList<Short>();
	private int mRunningOffset = 0;
			
	public Font(Map<String,String> glyphProperties) {
		
		this.mAsset = glyphProperties.get("asset");
		this.mName = glyphProperties.get("name");
		this.mAtlasWidth = Float.parseFloat(glyphProperties.get("atlas_width"));
		this.mAtlasHeight = Float.parseFloat(glyphProperties.get("atlas_height"));
		
		Log.d(TAG, String.format("Loading %s font atlas: %s x %s", mName, mAtlasWidth, mAtlasHeight));
		
		mGlyphs = new HashMap<Character,Integer>(glyphProperties.size());
		
		for(String s : glyphProperties.keySet()) {
			if(s.startsWith(GLYPH_PREFIX)) {
				String values = glyphProperties.get(s);
				String glyph = s.substring(GLYPH_PREFIX.length());
				
				//If length is 1, then we have a single character
				//Else, we have a code
				if(glyph.length() > 1) {
					if(glyph.equals(EQUALS_CODE)) {
						glyph = "=";
					}
				}				
				
				mGlyphs.put(glyph.charAt(0), mRunningOffset);
				loadGlyphIntoList(values);
			}
		}
			
	}
	
	public int getGlyphIndex(char glyph) {
		return this.mGlyphs.get(glyph);
	}
	
	public byte[] getVbo() {
		return SSArrayUtil.floatToByteArray(ArrayUtils.toPrimitive(mData.toArray(new Float[mData.size()])));
	}
	
	public byte[] getIbo() {
		return SSArrayUtil.shortToByteArray(ArrayUtils.toPrimitive(mIdx.toArray(new Short[mIdx.size()])));
	}
	
	private void loadGlyphIntoList(String values) {
		String[] bbStr = values.split(",");
		
		if(bbStr.length != 4) {
			throw new IllegalArgumentException("Glyphs must have exactly left, top, right, bottom.");
		}
		
		float[] bb = new float[4];
		bb[0] = Float.parseFloat(bbStr[0]);
		bb[1] = Float.parseFloat(bbStr[1]);
		bb[2] = Float.parseFloat(bbStr[2]);
		bb[3] = Float.parseFloat(bbStr[3]);
		
		loadGlyphToVboList(bb);
		loadGlyphToIboList();
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
		
		float top = bbTop;
		float bottom = bbBottom / mAtlasHeight;
		float left = bbLeft / mAtlasWidth;
		float right = bbRight / mAtlasWidth;
		
		float halfW = 2.5f;
		float halfH = 4.5f;
		
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
