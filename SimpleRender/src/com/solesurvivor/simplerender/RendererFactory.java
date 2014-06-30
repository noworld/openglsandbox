package com.solesurvivor.simplerender;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.solesurvivor.simplerender.renderer.AlphaPngGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.IboGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.MultiModelGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.PackedArrayGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.PackedArrayZipGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.SimpleGLRenderer;
import com.solesurvivor.simplerender.renderer.SimpleGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.VboGLTextureRenderer;

public class RendererFactory {
	
	private static Context mContext;
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	public static Context getContext() {
		return mContext;
	}

	public static GLSurfaceView.Renderer getRenderer(RendererType rType, String model) {
		 GLSurfaceView.Renderer renderer = null;
		 
		 switch(rType) {
		case IBO: renderer = new IboGLTextureRenderer(mContext);
			break;
		case PACKED_ARRAY: renderer = new PackedArrayGLTextureRenderer(mContext);
			break;
		case PACKED_ARRAY_ZIP: renderer = new PackedArrayZipGLTextureRenderer(mContext, model);
			break;
		case SIMPLE: renderer = new SimpleGLRenderer(mContext);
			break;
		case TEXTURE: renderer = new SimpleGLTextureRenderer(mContext);
			break;
		case VBO: renderer = new VboGLTextureRenderer(mContext);
			break;
		case MULTI_MODEL: renderer = new MultiModelGLTextureRenderer(mContext);
			break;
		case ALPHA_PNG: renderer = new AlphaPngGLTextureRenderer(mContext);
			break;
		default: renderer = new SimpleGLRenderer(mContext);
			break;
		 }
		 
		 return renderer;
	}
}
