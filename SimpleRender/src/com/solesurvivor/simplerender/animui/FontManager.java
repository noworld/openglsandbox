package com.solesurvivor.simplerender.animui;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.solesurvivor.simplerender.R;
import com.solesurvivor.util.SSArrayUtil;

public class FontManager {
	
//	private static final String FONT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()<>?/{}[]\\;':\"`~_+-=";
//	
//	private static final float IMAGE_SIZE = 1024.0f;
//	
//	public Map<String,Font> mFonts = new HashMap<String,Font>();
//	
//	private Context mContext;
//	
//	public FontManager(Context context) {
//		this.mContext = context;
//		buildFonts();
//	}
//	
//	public FloatBuffer[] getDisplayObj(String fontName, char c) {
//		
//		FloatBuffer[] buffers = new FloatBuffer[3];
//		Font f = mFonts.get(fontName);
//		
//		buffers[0] = getCharBoxPos(f.mSize);
//		buffers[1] = getCharBoxNrm();
//		
//		Point start = f.mChars.get(c);
//		Point size = f.mSize;
//		
//		float[] texCoords = new float[12];
//		
//		//Triangle 1
//		texCoords[0] = start.x + size.x; //x
//		texCoords[1] = start.y; //y
//	
//		texCoords[2] = start.x;
//		texCoords[3] = start.y;
//		
//		texCoords[4] = start.x;
//		texCoords[5] = start.y + size.y;
//		
//		//Triangle 2
//		texCoords[6] = start.x + size.x;
//		texCoords[7] = start.y;
//		
//		texCoords[8] = start.x;
//		texCoords[9] = start.y + size.y;
//		
//		texCoords[10] = start.x + size.x;
//		texCoords[11] = start.y + size.y;
//		
//		//Normalize?
//		for(int i = 0; i < texCoords.length; i++) {
//			texCoords[i] = texCoords[i] / IMAGE_SIZE;
//		}
//		
//		buffers[2] = SSArrayUtil.arrayToFloatBuffer(texCoords);
//				
//		return buffers;
//		
//	}
//
//	private FloatBuffer getCharBoxPos(Point size) {
//		float[] pos = new float[18];
//		
//		Point start = new Point(0,0);
//		
//		pos[0] = start.x + size.x; //x
//		pos[1] = start.y; //y
//		pos[2] = 0.0f; //z
//	
//		pos[3] = start.x;
//		pos[4] = start.y;
//		pos[5] = 0.0f;
//		
//		pos[6] = start.x;
//		pos[7] = start.y + size.y;
//		pos[8] = 0.0f;
//		
//		//Triangle 2
//		pos[9] = start.x + size.x;
//		pos[10] = start.y;
//		pos[11] = 0.0f;
//		
//		pos[12] = start.x;
//		pos[13] = start.y + size.y;
//		pos[14] = 0.0f;
//		
//		pos[15] = start.x + size.x;
//		pos[16] = start.y + size.y;
//		pos[17] = 0.0f;
//		
//		return SSArrayUtil.arrayToFloatBuffer(pos);
//	}
//
//	private FloatBuffer getCharBoxNrm() {
//		float[] norms = new float[18];
//		
//		for(int i = 0; i < norms.length; i += 3) {
//			norms[i] = 0.0f;
//			norms[i+1] = 0.0f;
//			norms[i+2] = 1.0f;
//		}
//		
//		return SSArrayUtil.arrayToFloatBuffer(norms);
//	}
//
//	private void buildFonts() {		
//		Resources res =  mContext.getResources();
//
//		TypedArray fonts = res.obtainTypedArray(R.array.fonts);
//		
//		for(int i = 0; i < fonts.length(); i++) {
//			int fontResId = fonts.getResourceId(i, 0);
//			String fontResName = res.getResourceEntryName(fontResId);
//			TypedArray font = res.obtainTypedArray(fontResId);
//			
//			for(int j = 0; j < font.length(); j++) {
//				int resourceId = font.getResourceId(j, 0);
//				String resourceName = res.getResourceEntryName(resourceId);
//				mFonts.put(fontResName, loadFont(fontResName, resourceId));	
//			}
//			
//			font.recycle();
//		}
//		
//		fonts.recycle();
//
//	}	
//
//	private Font loadFont(String resourceName, int resourceId) {
//		Font font = new Font();
//		font.mName = resourceName;
//		font.mTextureHandle = loadTexture(resourceId);
//		
//		Point[] lineStarts = null;
//		int hStride = 0;
//		char[] breakAfter = null;
//		int line = 0;
//		int lineCharPos = 0;
//		
////		if(resourceName.equals("praeFont")) {
//			font.mSize = new Point(20, 36);			
//			lineStarts = new Point[]{new Point(9,37), new Point(4,152), new Point(4,203)};
//			hStride = 28;
//			breakAfter = new char[]{'i',']'};
////		}
//				
//		for(int i = 0; i < FONT_CHARS.toCharArray().length; i++, lineCharPos++) {			
//			char c = FONT_CHARS.toCharArray()[i];
//			
//			Point currStart = lineStarts[line];
//			
//			int currX = currStart.x + (lineCharPos * hStride);
//			
//			int currY = currStart.y;
//			
//			Point p = new Point(currX, currY);
//			font.mChars.put(c, p);
//			
//			for(char breakChar : breakAfter) {
//				if(c == breakChar) {
//					lineCharPos = 0;
//					line++;
//				}
//			}
//		}
//		
//		return font;
//	}
//	
//	protected int loadTexture(final int resourceId) {
//		final int[] textureHandle = new int[1];
//		
//		GLES20.glGenTextures(1, textureHandle, 0);
//		
//		if (textureHandle[0] != 0)
//		{
//			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inScaled = false;	// No pre-scaling
//
//			// Read in the resource
//			final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, options);
//						
//			// Bind to the texture in OpenGL
//			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
//			
//			// Set filtering
//			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
//			
//			// Load the bitmap into the bound texture.
//			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//			
//			// Recycle the bitmap, since its data has been loaded into OpenGL.
//			bitmap.recycle();						
//		}
//		
//		if (textureHandle[0] == 0)
//		{
//			throw new RuntimeException("Error loading texture.");
//		}
//		
//		return textureHandle[0];
//	}
	
}
