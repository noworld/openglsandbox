package com.solesurvivor.simplerender;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class RendererFactory {
	
	private static Context mContext;
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	public static Context getContext() {
		return mContext;
	}

	public static GLSurfaceView.Renderer getRenderer(RendererType rType) {
		 GLSurfaceView.Renderer renderer = null;
		 
		 switch(rType) {
		case IBO: renderer = new IboGLTextureRenderer(mContext);
			break;
		case PACKED_ARRAY: renderer = new PackedArrayGLTextureRenderer(mContext);
			break;
		case PACKED_ARRAY_ZIP: renderer = new PackedArrayZipGLTextureRenderer(mContext);
			break;
		case SIMPLE: renderer = new SimpleGLRenderer(mContext);
			break;
		case TEXTURE: renderer = new SimpleGLTextureRenderer(mContext);
			break;
		case VBO: renderer = new VboGLTextureRenderer(mContext);
			break;
		default: renderer = new SimpleGLRenderer(mContext);
			break;
		 }
		 
		 return renderer;
	}
}
