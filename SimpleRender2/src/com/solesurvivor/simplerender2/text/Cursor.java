package com.solesurvivor.simplerender2.text;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import android.opengl.Matrix;

import com.solesurvivor.simplerender2.text.Cursor.CursorPosition;

public class Cursor implements Iterable<CursorPosition> {

	private static final int BYTES_PER_INDEX = 2;

	private Font mFont = null;
	private float[] mScale;
	private float[] mOriginalScale;
	private float[] mPosition;
	private float[] mOriginalPosition;
	private float mLineLen = Float.POSITIVE_INFINITY;
	private float mLineHeight = 0.0f;
	private float mCharPadding = 0.0f;
	private String mValue = null; 

	public Cursor(Font font, float[] scale, float[] position, float charPadding, String value) {
		this.mFont = font;
		this.mOriginalScale = scale;
		this.mScale = scale;
		this.mOriginalPosition = position;
		this.mPosition = position;
		this.mCharPadding = charPadding;
		this.mValue = value;
	}
	
	public void setLineLength(float lineLen) {
		this.mLineLen = lineLen;
	}
	
	public void setLineHeight(float lineHieght) {
		this.mLineHeight = lineHieght;
	}
	
	public void scale(float x, float y, float z) {
		float[] intermediate = {mScale[0] * x, 
				mScale[1] * y, 
				mScale[2] * z};
		this.mScale = intermediate;
	}
	
	public void translate(float x, float y, float z) {
		float[] intermediate = {mPosition[0] + x, 
				mPosition[1] + y, 
				mPosition[2] + z, 
				mPosition[3]};
		this.mPosition = intermediate;
	}

	public void reset() {
		this.mScale = mOriginalScale;
		this.mPosition = mOriginalPosition;
	}
	
	public Font getFont() {
		return mFont;
	}
	
	public void setValue(String value) {
		this.mValue = value;
	}
	
	public void setValue(String format, Object... args) {
		this.mValue = String.format(format, args);
	}

	@Override
	public Iterator<CursorPosition> iterator() {
		return new CursorIterator();
	}
	
	public class CursorPosition {
		public float[] mModelMatrix = new float[16];
		public int mGlyphIndex;
	}
	
	private class CursorIterator implements Iterator<CursorPosition> {
		
		private int mCurrentIndex = 0;
		private CursorPosition mCursorPosition = new CursorPosition();
		private float mAdvance = 0;
		
		public CursorIterator() {
			//Left align
			mAdvance = (mFont.getGlyph(mValue.toCharArray()[0]).mWidth/2) * mScale[0];
		}
		
		@Override
		public boolean hasNext() {
			if(StringUtils.isBlank(mValue)) return false;
			return (mCurrentIndex > -1) && (mCurrentIndex < mValue.length());
		}

		@Override
		public CursorPosition next() {
			updateCursorPosition();
			return mCursorPosition;
		}

		private void updateCursorPosition() {
			Glyph glyph =  mFont.getGlyph(mValue.toCharArray()[mCurrentIndex++]);
			Glyph nextGlyph = null;
			
			//*
			if(mCurrentIndex < mValue.toCharArray().length) {
				nextGlyph = mFont.getGlyph(mValue.toCharArray()[mCurrentIndex]);
			} else {
				nextGlyph = glyph;
			}

			mCursorPosition.mGlyphIndex = Cursor.BYTES_PER_INDEX * glyph.mOffset;
			
			float[] temp = new float[16];
			Matrix.setIdentityM(temp, 0);
			Matrix.translateM(temp, 0, mPosition[0] + mAdvance, mPosition[1], mPosition[2]);
			Matrix.scaleM(temp, 0, mScale[0], mScale[1], mScale[2]);
			mCursorPosition.mModelMatrix = temp;
			
			//* - /2
			mAdvance += (((glyph.mWidth/2) + (nextGlyph.mWidth/2)) * mScale[0]) + mCharPadding;
			
			// * This is necessary to account for the fact
			//that the glyph is centered on 0,0 in model space
			
		}

		@Override
		public void remove() {
			//I have no idea why I bothered to try to implement this...
			if(StringUtils.isNotBlank(mValue)) {
				StringBuilder temp = null;
				
				if(mCurrentIndex == 0) {
					temp = new StringBuilder();
					temp.append(mValue.substring(1)); //Grab without the first
				} else if(mCurrentIndex == mValue.length()) {
					temp = new StringBuilder();
					temp.append(mValue.substring(0,mValue.length() - 1)); //Grab except the last
				} else if(mCurrentIndex < mValue.length()) {
					temp = new StringBuilder();
					temp.append(mValue.substring(0, mCurrentIndex));
					temp.append(mValue.substring(mCurrentIndex + 1));
				}
				if(temp != null) {
					mValue = temp.toString();
				}
			}
			
		}
		
	}

}
