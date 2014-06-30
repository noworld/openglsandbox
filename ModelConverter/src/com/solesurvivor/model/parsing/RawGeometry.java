package com.solesurvivor.model.parsing;

public class RawGeometry {
	
	public String name;
	public VertexData[] vertexData;
	public int[] p;
	public int[] vcount;
	
	public int mXSize;
	public int mYSize;
	public int mZSize;
	
	public RawGeometry(int count) {
		vertexData = new VertexData[count];
	}
	
	public class VertexData {
		public Semantic semantic;
		public float[] data;
		public float[] remappedData;
		public int count;
		public int stride;
		public int readCount = 0;
		public int[] remappedIndex;
	}
	
	public enum Semantic {
		VERTEX,
		POSITION,
		NORMAL,
		COLOR,
		TEXCOORD
	}
}
