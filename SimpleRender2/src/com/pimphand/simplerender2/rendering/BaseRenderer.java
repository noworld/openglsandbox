package com.pimphand.simplerender2.rendering;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.pimphand.simplerender2.scene.GameWorld;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.logging.SSLog;

public class BaseRenderer implements GLSurfaceView.Renderer {
	
	private static final String TAG = BaseRenderer.class.getSimpleName();
	
	@Override
	public void onDrawFrame(GL10 gl) {
		synchronized(GameWorld.instance()) {
			clearOpenGL();
			GameWorld.instance().update();
			GameWorld.instance().render();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "Renderer.onSurfaceChanged");
		synchronized(GameWorld.instance()) {
			GameWorld.instance().resizeViewport(new Point(width, height));
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		
	}
	
	public void resizeViewport(Point viewport) {
		GLES20.glViewport(0, 0, viewport.x, viewport.y);
	}
	
	public void clearOpenGL() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	public int loadToIbo(byte[] iboBytes) {
		return loadGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes);
	}

	public int loadToVbo(byte[] vboBbytes) {
		return loadGLBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_FLOAT, vboBbytes);
	}
	
	public int loadGLBuffer(int glTarget, int glType, byte[] bytes) {
		int bufIdx = -1;

		final int buffers[] = new int[1];
		GLES20.glGenBuffers(1, buffers, 0);		
		if(buffers[0] < 1) {
			Log.e(TAG, "IBO buffer could not be created.");
		}

		bufIdx = buffers[0];
		Buffer buf = null;
		int dataSize = -1;
	
		if(glType == GLES20.GL_UNSIGNED_SHORT) {
		
			ShortBuffer iboBuf = SSArrayUtil.bytesToShortBufBigEndian(bytes);
			buf = iboBuf;
			dataSize = DrawingConstants.BYTES_PER_SHORT;

		} else if(glType == GLES20.GL_FLOAT) {

			FloatBuffer vboBuf = SSArrayUtil.bytesToFloatBufBigEndian(bytes);
			buf = vboBuf;
			dataSize = DrawingConstants.BYTES_PER_FLOAT;
			
		}
		
		GLES20.glBindBuffer(glTarget, bufIdx);
		GLES20.glBufferData(glTarget, buf.capacity() * dataSize, buf, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(glTarget, 0);
				
		return bufIdx;
	}
		
	protected int checkError() {
		int errorCode =  GLES20.GL_NO_ERROR;
		/* Check for errors */
		if(GLES20.glGetError() != GLES20.GL_NO_ERROR) {			
			SSLog.w(TAG, "OpenGL Error Encountered: " + interpretError(GLES20.glGetError()));
			errorCode = GLES20.glGetError();
		}
		
		return errorCode;
	}

	protected String interpretError(int errNum) {		

		switch(errNum) {
		case GLES20.GL_NO_ERROR: return "GL_NO_ERROR";
		case GLES20.GL_INVALID_ENUM: return "GL_INVALID_ENUM";
		case GLES20.GL_INVALID_VALUE: return "GL_INVALID_VALUE";
		case GLES20.GL_INVALID_OPERATION: return "GL_INVALID_OPERATION"; 
		case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION: return "GL_INVALID_FRAMEBUFFER_OPERATION";
		case GLES20.GL_OUT_OF_MEMORY: return "GL_OUT_OF_MEMORY";
		}

		return "Error code unrecognized: " + errNum;
	}

}
