package com.solesurvivor.drawing;

public interface DrawingConstants {

	public static final int BYTES_PER_FLOAT = 4;
	public static final int BYTES_PER_SHORT = 2;
	public static final int BYTES_PER_INT = 4;
	/** Offset of the position data. */
	public static final int POSITION_OFFSET = 0;
	/** Size of the position data in elements. */
	public static final int POSITION_DATA_SIZE = 3;
	/** Offset of the color data. */
	public static final int COLOR_OFFSET = -1;
	/** Size of the color data in elements. */
	public static final int COLOR_DATA_SIZE = 4;
	/** Offset of the normal data. */
	public static final int NORMAL_OFFSET = 1;
	/** Size of the normal data in elements. */
	public static final int NORMAL_DATA_SIZE = 3;
	/** Offset of the texture coordinate data. */
	public static final int TEXCOORD_OFFSET = 2;
	/** Size of the texture coordinate in elements. */
	public static final int TEXCOORD_DATA_SIZE = 2;
	/** Number of vertices in a triangle */
	public static final int TRIANGLE_VERTICES = 3;
	
}
