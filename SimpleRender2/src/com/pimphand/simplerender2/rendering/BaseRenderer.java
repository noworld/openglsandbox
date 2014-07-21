package com.pimphand.simplerender2.rendering;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.pimphand.simplerender2.rendering.shaders.ShaderManager;
import com.pimphand.simplerender2.rendering.textures.TextureManager;
import com.pimphand.simplerender2.scene.GameWorld;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.logging.SSLog;

public class BaseRenderer implements GLSurfaceView.Renderer {
	
	private static final String TAG = BaseRenderer.class.getSimpleName();
	
	/* --------------------------------- */
	/* Event Methods that drive the game */
	/* --------------------------------- */
	
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
		ShaderManager.init();
		TextureManager.init();
		GameWorld.init();
	}
	
	
	/* ------------------------------- */
	/* Methods to be called externally */
	/* ------------------------------- */

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
	
	public int loadShaderProgram(String[] vertCode, String[] fragCode) {
		
		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vertCode[0]);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fragCode[0]);

		int shaderHandle = createAndLinkProgram(vShadHand, fShadHand);

		return shaderHandle;
	}
	
	public int loadTexture(Bitmap bitmap) {
		int[] textureHandle = new int[1];
		
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0) {
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();						
		}
		
		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}
	
	public void initOpenGL(GlSettings settings) {
		Log.d(TAG, "Renderer.initOpenGL");

		// Set the background clear color
		float[] color = settings.getClearColor();
		GLES20.glClearColor(color[0],color[1],color[2],color[3]);
		
		
		for(GlOption go : settings.getOptions()) {
			if(go.isEnabled()) {
				GLES20.glEnable(go.getIndex());
			} else {
				GLES20.glDisable(go.getIndex());
			}
		}

		GLES20.glBlendFunc(settings.getBlendFunc().getSource(), settings.getBlendFunc().getDest());	
	}
	
	public void drawUI(Geometry geo) {

		float[] mvpMatrix = new float[16];
		float[] uiMatrix = GameWorld.instance().getCamera().getUiMatrix();
		float[] viewMatrix = GameWorld.instance().getCamera().getViewMatrix();
		
		GLES20.glUseProgram(geo.mShaderHandle);
		
		int u_mvp = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_MVPMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_Position");
		int a_txc = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_TexCoordinate");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, geo.mTextureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geo.mDatBufIndex);

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, geo.mPosSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mPosOffset);

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, geo.mTxcSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mTxcOffset);

		// --MV--
		//make mMVPMatrix MV
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, geo.mModelMatrix, 0);
		//make mMVPMatrix MVP
		Matrix.multiplyMM(mvpMatrix, 0, uiMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.mIdxBufIndex);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.mNumElements, GLES20.GL_UNSIGNED_SHORT, 0);

		//unbind buffers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();
		
	}
	
	/* ------------------------------ */
	/*        Utility Methods         */
	/* ------------------------------ */
		
	protected int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle > 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);			

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) 
			{
				Log.e("compileShader", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}

		return shaderHandle;
	}
	
	protected int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle) {
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) 
			{				
				Log.e("createAndLinkProgram", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}

		return programHandle;
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
