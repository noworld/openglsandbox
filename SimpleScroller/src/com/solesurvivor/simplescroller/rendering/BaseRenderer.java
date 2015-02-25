package com.solesurvivor.simplescroller.rendering;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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

import com.solesurvivor.simplescroller.game.GameWorld;
import com.solesurvivor.simplescroller.scene.Camera;
import com.solesurvivor.simplescroller.scene.Drawable;
import com.solesurvivor.simplescroller.scene.Geometry;
import com.solesurvivor.simplescroller.scene.gameobjects.SpriteManager;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class BaseRenderer implements GLSurfaceView.Renderer {
	
	private static final float[] CLEAR_COLOR = {0.5f,0.5f,0.5f,DrawingConstants.ONE_F};
	private static final String TAG = BaseRenderer.class.getSimpleName();
	
	protected Camera currentCamera = null;
	protected Vec3 clearColor = Vec3.fromFloatArray(CLEAR_COLOR);
	
	public void setCurrentCamera(Camera camera) {
		this.currentCamera = camera;
	}
	
	public void setClearColor(Vec3 color) {
		this.clearColor = color;
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		synchronized(GameWorld.inst()) {
			clearOpenGL();
			GameWorld.inst().update();
			GameWorld.inst().render();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		synchronized(GameWorld.inst()) {			
			this.resizeViewport(width, height);
			GameWorld.inst().resizeViewport(new Point(width, height));
		}
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		ShaderManager.init();
		TextureManager.init();
		SpriteManager.init();
		GameWorld.init();		
		GameWorld.inst().enter();
	}
	
	public void resizeViewport(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}
	
	public void initOpenGLDefault() {
		Log.d(TAG, "Renderer.initOpenGL");

		// Set the background clear color
		GLES20.glClearColor(clearColor.getR(), clearColor.getG(), clearColor.getB(), DrawingConstants.ONE_F);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
	}
	
	/*
	 * LOADING METHODS
	 */
	
	public int loadShaderProgram(String[] vertCode, String[] fragCode) {

		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vertCode[0]);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fragCode[0]);

		int shaderHandle = createAndLinkProgram(vShadHand, fShadHand);

		return shaderHandle;
	}
	
	protected int compileShader(final int shaderType, final String shaderSource) {
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
	
	public int loadTexture(Bitmap bitmap) {
		int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);		
			
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}
	
	public int reloadTexture(Bitmap bitmap, int texHandle) {
		int[] textureHandle = {texHandle};

		if (textureHandle[0] != 0) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);		
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error reloading texture.");
		}

		return textureHandle[0];
	}
	
	public int loadToIbo(byte[] iboBytes) {
		return loadGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes, GLES20.GL_STATIC_DRAW);
	}

	public int loadToVbo(byte[] vboBbytes) {
		return loadGLBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_FLOAT, vboBbytes, GLES20.GL_STATIC_DRAW);
	}

	public int loadGLBuffer(int glTarget, int glType, byte[] bytes, int usage) {
		int bufIdx = -1;

		final int buffers[] = new int[1];
		GLES20.glGenBuffers(1, buffers, 0);		
		if(buffers[0] < 1) {
			Log.e(TAG, "IBO buffer could not be created.");
		}

		bufIdx = buffers[0];

		return loadExistingGLBuffer(glTarget, glType, bytes, bufIdx, usage);
	}

	public int loadExistingGLBuffer(int glTarget, int glType, byte[] bytes, int bufIdx, int usage) {

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
		GLES20.glBufferData(glTarget, buf.capacity() * dataSize, buf, usage);
		GLES20.glBindBuffer(glTarget, 0);

		return bufIdx;
	}
	
	public int[] genTextureBuffer(Point dim) {
		//frame, depth, texture
		int[] buffers = new int[3];
		GLES20.glGenFramebuffers(1, buffers, 0);
		GLES20.glGenRenderbuffers(1, buffers, 1);
		GLES20.glGenTextures(1, buffers, 2);

		//texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buffers[2]);

//		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
//		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

		int size = dim.x * dim.y * DrawingConstants.BYTES_PER_FLOAT;
		IntBuffer pixelBuf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, dim.x, dim.y, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, pixelBuf);

		//depth
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, buffers[1]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, dim.x, dim.y);

		return buffers;
	}

	
	
	/*
	 * DRAWING METHODS
	 */
	
	public void clearOpenGL() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearTexture(int[] buffers) {
		//frame, depth, texture
		
		//frame
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, buffers[0]);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, buffers[2], 0);

		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, buffers[1]);

		if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Frame buffer not ready.");
		}

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
	}
	
	public void drawGeometry(Drawable draw) {
		drawGeometry(draw, draw.getWorldMatrix(), GLES20.GL_TRIANGLES);
	}
	
	public void drawGeometry(Drawable draw, float[] mModelMatrix) {
		drawGeometry(draw, mModelMatrix, GLES20.GL_TRIANGLES);
	}
	
	public void drawGeometryTristrips(Drawable draw, float[] mModelMatrix) {
		drawGeometry(draw, mModelMatrix, GLES20.GL_TRIANGLE_STRIP);
	}

	public void drawGeometry(Drawable draw, float[] modelMatrix, int primType) {

		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = currentCamera.getProjectionMatrix();
		float[] viewMatrix = currentCamera.getAgentViewMatrix();

		int shaderHandle = draw.getShaderHandle();
		int textureHandle = draw.getTextureHandle();

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, draw.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, draw.getPosSize(), GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getPosOffset());

		if(a_nrm > -1 && draw.getNrmOffset() > -1 && draw.getNrmSize() > -1) {
			GLES20.glEnableVertexAttribArray(a_nrm);
			GLES20.glVertexAttribPointer(a_nrm, draw.getNrmSize(), GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getNrmOffset());
		}

		if(a_txc > -1 && draw.getTxcOffset() > -1 && draw.getTxcSize() > -1) {
			GLES20.glEnableVertexAttribArray(a_txc);
			GLES20.glVertexAttribPointer(a_txc, draw.getTxcSize(), GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getTxcOffset());
		}

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		// --MV--		
		
		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);

		if(u_mv > -1) {
			GLES20.glUniformMatrix4fv(u_mv, 1, false, mvMatrix, 0); //1282
		}

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
		
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, draw.getIdxBufHandle());
		GLES20.glDrawElements(primType, draw.getNumElements(), GLES20.GL_UNSIGNED_SHORT, draw.getElementOffset());

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
	public void drawUI(Geometry geo) {

		float[] mvpMatrix = new float[16];
		float[] uiMatrix = currentCamera.getOrthoMatrix();
		float[] viewMatrix = currentCamera.getViewMatrix();

		GLES20.glUseProgram(geo.getShaderHandle());

		int u_mvp = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_MVPMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(geo.getShaderHandle(), "a_Position");
		int a_txc = GLES20.glGetAttribLocation(geo.getShaderHandle(), "a_TexCoordinate");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, geo.getTextureHandle());
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geo.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, geo.getPosSize(), GLES20.GL_FLOAT, false, geo.getElementStride(), geo.getPosOffset());

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, geo.getTxcSize(), GLES20.GL_FLOAT, false, geo.getElementStride(), geo.getTxcOffset());

		// --MV--
		//make mMVPMatrix MV
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, geo.getWorldMatrix(), 0);
		//make mMVPMatrix MVP
		Matrix.multiplyMM(mvpMatrix, 0, uiMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw

		/* Draw the arrays as triangles */
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		//unbind buffers
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
	
	
	/*
	 * UTILITY METHODS
	 */

	protected int checkError() {
		int currentError = GLES20.glGetError();
		if(currentError != GLES20.GL_NO_ERROR) {			
			SSLog.w(TAG, "OpenGL Error Encountered: " + interpretError(currentError));
		}

		return currentError;
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
