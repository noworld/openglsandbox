package com.solesurvivor.simplerender2_5.rendering;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;

import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.game.states.GameStateEnum;
import com.solesurvivor.simplerender2_5.game.states.GameStateManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Drawable;
import com.solesurvivor.simplerender2_5.scene.Light;
import com.solesurvivor.simplerender2_5.scene.ProceduralTexture_16_9;
import com.solesurvivor.simplerender2_5.scene.Skybox;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.logging.SSLog;

public class BaseRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = BaseRenderer.class.getSimpleName();
	
	protected Camera mCurrentCamera;

	/* --------------------------------- */
	/* Event Methods that drive the game */
	/* --------------------------------- */

	@Override
	public void onDrawFrame(GL10 gl) {
		synchronized(GameWorld.inst()) {
			clearOpenGL();
			GameWorld.inst().update();
			GameWorld.inst().render();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "Renderer.onSurfaceChanged");		
		synchronized(GameWorld.inst()) {			
			this.resizeViewport(width, height);
			GameWorld.inst().resizeViewport(new Point(width, height));
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		ShaderManager.init();
		TextureManager.init();
//		FontManager.init();
//		GeometryManager.init();
		GameWorld.init();
		synchronized(GameWorld.inst()) {
			Display d = GameGlobal.inst().getWindowManager().getDefaultDisplay();
			Point p = new Point(0,0);
			d.getSize(p); //Requires API 13+			
			GameWorld.inst().resizeViewport(new Point(p.x, p.y));			
		}
		
		GameStateManager.init();
		GameWorld.inst().changeState(GameStateManager.getState(GameStateEnum.MAIN_MENU));
	}


	/* ----------------------------------------------------- */
	/* Methods for loading drawables to be called externally */
	/* ----------------------------------------------------- */

	public void setCurrentCamera(Camera cam) {
		this.mCurrentCamera = cam;
	}
	
	public void resizeViewport(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}
	
	public void resetViewportToWorld() {
		Point view = GameWorld.inst().getViewport();
		GLES20.glViewport(0, 0, view.x, view.y);
	}
	
	public void clearOpenGL() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	}

//	public int loadToDynamicIbo(byte[] iboBytes) {
//		return loadGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes, GLES20.GL_DYNAMIC_DRAW);
//	}

	public int loadToIbo(byte[] iboBytes) {
		return loadGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes, GLES20.GL_STATIC_DRAW);
	}

//	public int reloadDynamicIbo(byte[] iboBytes, int bufIdx) {
//		return reloadIbo(iboBytes, bufIdx, GLES20.GL_DYNAMIC_DRAW);
//	}
//
//	public int reloadIbo(byte[] iboBytes, int bufIdx, int usage) {
//		return loadExistingGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes, bufIdx, usage);
//	}
//
//	public int loadToIboReverse(byte[] iboBytes) {
//		return loadGLBufferReverse(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes);
//	}

	public int loadToVbo(byte[] vboBbytes) {
		return loadGLBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_FLOAT, vboBbytes, GLES20.GL_STATIC_DRAW);
	}

//	public int loadToVboReverse(byte[] vboBbytes) {
//		return loadGLBufferReverse(GLES20.GL_ARRAY_BUFFER, GLES20.GL_FLOAT, vboBbytes);
//	}
//
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

//	public int loadGLBufferReverse(int glTarget, int glType, byte[] bytes) {
//		int bufIdx = -1;
//
//		final int buffers[] = new int[1];
//		GLES20.glGenBuffers(1, buffers, 0);		
//		if(buffers[0] < 1) {
//			Log.e(TAG, "IBO buffer could not be created.");
//		}
//
//		bufIdx = buffers[0];
//		Buffer buf = null;
//		int dataSize = -1;
//
//		if(glType == GLES20.GL_UNSIGNED_SHORT) {
//
//			short[] backwards = SSArrayUtil.byteToShortArray(bytes);
//			ArrayUtils.reverse(backwards);
//			byte[] reverse = SSArrayUtil.shortToByteArray(backwards);
//
//			ShortBuffer iboBuf = SSArrayUtil.bytesToShortBufBigEndian(reverse);			
//			buf = iboBuf;
//			dataSize = DrawingConstants.BYTES_PER_SHORT;
//
//		} else if(glType == GLES20.GL_FLOAT) {
//
//			float[] backwards = SSArrayUtil.byteToFloatArray(bytes);
//			ArrayUtils.reverse(backwards);
//			byte[] reverse = SSArrayUtil.floatToByteArray(backwards);
//
//			FloatBuffer vboBuf = SSArrayUtil.bytesToFloatBufBigEndian(reverse);
//			buf = vboBuf;
//			dataSize = DrawingConstants.BYTES_PER_FLOAT;
//
//		}
//
//		GLES20.glBindBuffer(glTarget, bufIdx);
//		GLES20.glBufferData(glTarget, buf.capacity() * dataSize, buf, GLES20.GL_STATIC_DRAW);
//		GLES20.glBindBuffer(glTarget, 0);
//
//		return bufIdx;
//	}

	public int loadShaderProgram(String[] vertCode, String[] fragCode) {

		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vertCode[0]);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fragCode[0]);

		int shaderHandle = createAndLinkProgram(vShadHand, fShadHand);

		return shaderHandle;
	}
	
	public int loadCubeMap(Bitmap[] cubemap, int[] imageOrder) {
		int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle[0]);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			for(int i = 0; i < cubemap.length; i++) {
				GLUtils.texImage2D(imageOrder[i], 0, cubemap[i], 0);
			}
			
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
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
	
	public int[] genTextureBuffer(Point dim) {
		//frame, depth, texture
		int[] buffers = new int[3];
		GLES20.glGenFramebuffers(1, buffers, 0);
		GLES20.glGenRenderbuffers(1, buffers, 1);
		GLES20.glGenTextures(1, buffers, 2);
		
		//texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buffers[2]);
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		
		int size = dim.x * dim.y * DrawingConstants.BYTES_PER_FLOAT;
		IntBuffer pixelBuf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, dim.x, dim.y, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, pixelBuf);
		
		//depth
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, buffers[1]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, dim.x, dim.y);
		
		return buffers;
	}

	/* ------------------------------------ */
	/* Draw methods to be called externally */
	/* ------------------------------------ */

	public void initOpenGLDefault() {
		Log.d(TAG, "Renderer.initOpenGL");

		// Set the background clear color
		GLES20.glClearColor(0.1f,0.3f,0.6f,1.0f);
		GLES20.glEnable(GLES20.GL_CULL_FACE);

	}

	public void drawSkybox(Skybox skybox) {

		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = new float[16];
		int shaderHandle = skybox.getShaderHandle();
		int textureHandle = skybox.getTextureHandle();
		
		GLES20.glDepthMask(false);

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, skybox.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, skybox.getPosSize(), GLES20.GL_FLOAT, false, skybox.getElementStride(), skybox.getPosOffset());

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.setIdentityM(viewMatrix, 0);
		float[] camRot = mCurrentCamera.getRotation();
		Matrix.rotateM(viewMatrix, 0, camRot[0], camRot[1], camRot[2], camRot[3]);
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, skybox.getModelMatrix(), 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw

		/* Draw the arrays as triangles */		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, skybox.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, skybox.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GLES20.glDepthMask(true);

		checkError();
	}

	public void drawGeometry(Drawable draw) {

		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();
		
		int shaderHandle = draw.getShaderHandle();
		int textureHandle = draw.getTextureHandle();

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(shaderHandle, "u_LightPos");
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

		if(a_nrm > -1) {
			GLES20.glEnableVertexAttribArray(a_nrm);
			GLES20.glVertexAttribPointer(a_nrm, draw.getNrmSize(), GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getNrmOffset());
		}

		if(a_txc > -1) {
			GLES20.glEnableVertexAttribArray(a_txc);
			GLES20.glVertexAttribPointer(a_txc, draw.getTxcSize(), GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getTxcOffset());
		}

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, draw.getModelMatrix(), 0);
		
		if(u_mv > -1) {
			//MVP matrix is *actually MV* at this point
			GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282
		}

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// --LightPos--

		/* Pass in the light position in eye space.	*/	
		//Switching to view space...
		//TODO: Handle multiple lights
		List<Light> lights = draw.getLights();
		if(lights != null) {
			Light light = lights.get(0);
			float[] lightPosWorldSpace = new float[4];
			float[] lightPosEyeSpace = new float[4];
			Matrix.multiplyMV(lightPosWorldSpace, 0, light.mModelMatrix, 0, light.mPosition, 0);
			Matrix.multiplyMV(lightPosEyeSpace, 0, viewMatrix, 0, lightPosWorldSpace, 0);   
			GLES20.glUniform3f(u_lightpos, lightPosEyeSpace[0], lightPosEyeSpace[1], lightPosEyeSpace[2]);
		}

		// Draw

		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, draw.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, draw.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
	public void renderTexture(ProceduralTexture_16_9 tex) {
		
		clearTexture(tex);
	
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();

		int textureHandle = tex.getTextureHandle();
		int shaderHandle = tex.getShaderHandle();

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(shaderHandle, "u_LightPos");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tex.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, tex.getPosSize(), GLES20.GL_FLOAT, false, tex.getElementStride(), tex.getPosOffset());

		if(a_nrm > -1) {
			GLES20.glEnableVertexAttribArray(a_nrm);
			GLES20.glVertexAttribPointer(a_nrm, tex.getNrmSize(), GLES20.GL_FLOAT, false, tex.getElementStride(), tex.getNrmOffset());
		}

		if(a_txc > -1) {
			GLES20.glEnableVertexAttribArray(a_txc);
			GLES20.glVertexAttribPointer(a_txc, tex.getTxcSize(), GLES20.GL_FLOAT, false, tex.getElementStride(), tex.getTxcOffset());
		}

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, tex.getModelMatrix(), 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// --LightPos--

		/* Pass in the light position in eye space.	*/	
		//Switching to view space...
		//TODO: Handle multiple lights
		List<Light> lights = tex.getLights();
		if(lights != null) {
			Light light = lights.get(0);
			float[] lightPosWorldSpace = new float[4];
			float[] lightPosEyeSpace = new float[4];
			Matrix.multiplyMV(lightPosWorldSpace, 0, light.mModelMatrix, 0, light.mPosition, 0);
			Matrix.multiplyMV(lightPosEyeSpace, 0, viewMatrix, 0, lightPosWorldSpace, 0);   
			GLES20.glUniform3f(u_lightpos, lightPosEyeSpace[0], lightPosEyeSpace[1], lightPosEyeSpace[2]);
		}

		// Draw

		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, tex.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, tex.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		checkError();
				
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//		resetViewportToWorld();
	}
	
	public void clearTexture(ProceduralTexture_16_9 tex) {
		//frame, depth, texture
		int[] buffers = tex.getBuffers();
		
		//Viewport should match texture size
//		int[] projection = new int[16];
//		Matrix.frustumM(projection, 0, -ratio, ratio, -1, 1, 0.5f, 10);
//		GLES20.glViewport(0, 0, this.texW, this.texH);

		//frame
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, buffers[0]);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, buffers[2], 0);

		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, buffers[1]);
		
		if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Frame buffer not ready.");
		}

		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
	}

//	public void waterOn() {
//		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
//		GLES20.glDisable(GLES20.GL_DITHER);
//		GLES20.glDisable(GLES20.GL_CULL_FACE);
//		GLES20.glEnable(GLES20.GL_BLEND);
//		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//	}
//
//	public void multiplyWaterOn() {
//		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
//		GLES20.glDisable(GLES20.GL_DITHER);
//		GLES20.glDisable(GLES20.GL_CULL_FACE);
//		GLES20.glDepthMask(false);
////		GLES20.glBlendFuncSeparate(GLES20.GL_CONSTANT_COLOR, GLES20.GL_SRC_COLOR, GLES20.GL_ONE, GLES20.GL_ZERO);
//		GLES20.glBlendFunc(GLES20.GL_ZERO, GLES20.GL_SRC_COLOR);
//	}
//
//	public void opaqueWaterOn() {
//		GLES20.glDisable(GLES20.GL_CULL_FACE);
//		GLES20.glDisable(GLES20.GL_BLEND);
//	}
//
//	public void addWaterOn() {
//		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
//		GLES20.glDisable(GLES20.GL_DITHER);
//		GLES20.glDisable(GLES20.GL_CULL_FACE);
//		GLES20.glDepthMask(true);
//		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE); 
//	}
//
//	public void waterOff() {
//		GLES20.glEnable(GLES20.GL_BLEND);
//		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//		GLES20.glDepthFunc(GLES20.GL_LESS);
//		GLES20.glDepthMask(true);
//		GLES20.glEnable(GLES20.GL_DITHER);
//		GLES20.glEnable(GLES20.GL_CULL_FACE);
//		GLES20.glDisable(GLES20.GL_STENCIL_TEST);
//		GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
//		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//	}
//
//	public void drawWater(Water water, List<Light> lights) {
//
//		Geometry geo = water.getGeometry();
//
//		float[] mvpMatrix = new float[16];
//		float[] projectionMatrix = GameWorld.inst().getProjectionMatrix();
//		float[] viewMatrix = GameWorld.inst().getAgentViewMatrix();
//
//		GLES20.glUseProgram(geo.mShaderHandle);
//
//		int u_mvp = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_MVPMatrix");
//		int u_mv = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_MVMatrix");
//		int u_nrm = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_NrmMatrix");		
//		int u_time = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_Time");
//		int u_w_col = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_WaterColor");
//		int u_numwaves = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_NumWaves");
//		int u_num_li = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_NumLights");
//		int u_eye_pos = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_EyePos");
//
//		int a_pos = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_Position");
//
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geo.mDatBufIndex);
//
//		GLES20.glEnableVertexAttribArray(a_pos);
//		GLES20.glVertexAttribPointer(a_pos, geo.mPosSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mPosOffset);
//
//		// --MV--
//
//		/* Get the MV Matrix: Multiply V * M  = MV */
//		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, geo.mModelMatrix, 0);
//		//MVP matrix is *actually MV* at this point
//		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282
//		
//		
//		
//		// -- Create the Normal Matrix --
//		float[] normalMatrix = new float[16];
//		if(!Matrix.invertM(normalMatrix, 0, mvpMatrix, 0)) {
//			Log.d(TAG,"Could not invert matrix.");
//		}
//		GLES20.glUniformMatrix4fv(u_nrm, 1, true, normalMatrix, 0); //Transpose
//
//		
//		
//		// --MVP--
//		
//		/* Get the MVP Matrix: Multiply P * MV = MVP*/
//		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
//		//MVP is MVP at this point
//		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
//		
//		//Camera Position
//		GLES20.glUniform3fv(u_eye_pos, 1, viewMatrix, 12);
//
//		//Lights
////		GLES20.glUniform1i(u_num_li, lights.size());
//		GLES20.glUniform1i(u_num_li, 1);
//		
//		for(int i = 0; i < lights.size(); i++) {
//			Light light = lights.get(i);
//			int u_lightpos = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_LightPositons[%s]",i));
//			float[] lightPosWorldSpace = new float[4];
//			float[] lightPosEyeSpace = new float[4];
//			Matrix.multiplyMV(lightPosWorldSpace, 0, light.mModelMatrix, 0, light.mPosition, 0);
//			Matrix.multiplyMV(lightPosEyeSpace, 0, viewMatrix, 0, lightPosWorldSpace, 0);   
//			//GLES20.glUniform3f(u_lightpos, lightPosEyeSpace[0], lightPosEyeSpace[1], lightPosEyeSpace[2]);
//			GLES20.glUniform3fv(u_lightpos, 1, lightPosEyeSpace, 0);
//		}
//
//		//Time
//		double time = ((double)SystemClock.uptimeMillis()) / 1000.0;
//		GLES20.glUniform1f(u_time, (float)time);
//
//		//Water Color
//		GLES20.glUniform4f(u_w_col, 0.1098f, 0.2549f, 0.3216f, 1.0f);
//		
//		//Waves
//		GLES20.glUniform1i(u_numwaves, water.getWaves().size());
//
//		for(int i = 0; i < water.getWaves().size(); i++) { 
//			Wave w = water.getWaves().get(i);			
//			int u_w_typ = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].wave_type",i));
//			int u_w_amp = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].amplitude",i));
//			int u_w_dir = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].direction",i));
//			int u_w_frq = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].frequency",i));
//			int u_w_spd = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].speed",i));
//			int u_w_phc = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].phase_const",i));
//			int u_w_tms = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].time_scale",i));
//			int u_w_phs = GLES20.glGetUniformLocation(geo.mShaderHandle, String.format("u_Waves[%s].phase_shift",i));
//
//			GLES20.glUniform1i(u_w_typ, w.getWaveType());
//			GLES20.glUniform1f(u_w_amp, w.getAmplitude());
//			GLES20.glUniform3f(u_w_dir, w.getDirection().getX(),w.getDirection().getY(),w.getDirection().getZ());
//			GLES20.glUniform1f(u_w_frq, w.getFrequency());
//			GLES20.glUniform1f(u_w_spd, w.getSpeed());
//			GLES20.glUniform1f(u_w_phc, w.getPhaseConst());
//			GLES20.glUniform1f(u_w_tms, w.getTimeScale());
//			GLES20.glUniform1f(u_w_phs, w.getPhaseShift());
//		}
//
//		// Draw	
////		opaqueWaterOn();
////		multiplyWaterOn();
//		waterOn();
//		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.mIdxBufIndex);
//		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.mNumElements, GLES20.GL_UNSIGNED_SHORT, 0);
//
//		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);		
//		waterOff();
//		
//		checkError();
//	}
//
//	private float[] getLowerOrderMatrix(float[] matrix) {
//		float[] newMat = null;
//		if(matrix.length == 16) {
//			newMat = new float[]{matrix[0],matrix[4],matrix[8],
//					             matrix[1],matrix[5],matrix[9],
//					             matrix[2],matrix[6],matrix[10]};
//		} else {
//			newMat = matrix;
//		}
//		return newMat;
//	}
//
//	public void drawUI(Geometry geo) {
//
//		float[] mvpMatrix = new float[16];
//		float[] uiMatrix = GameWorld.inst().getUiMatrix();
//		float[] viewMatrix = GameWorld.inst().getViewMatrix();
//
//		GLES20.glUseProgram(geo.mShaderHandle);
//
//		int u_mvp = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_MVPMatrix");
//		int u_texsampler = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_Texture");
//
//		int a_pos = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_Position");
//		int a_txc = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_TexCoordinate");
//
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, geo.mTextureHandle);
//		GLES20.glUniform1i(u_texsampler, 0);
//
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geo.mDatBufIndex);
//
//		GLES20.glEnableVertexAttribArray(a_pos);
//		GLES20.glVertexAttribPointer(a_pos, geo.mPosSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mPosOffset);
//
//		GLES20.glEnableVertexAttribArray(a_txc);
//		GLES20.glVertexAttribPointer(a_txc, geo.mTxcSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mTxcOffset);
//
//		// --MV--
//		//make mMVPMatrix MV
//		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, geo.mModelMatrix, 0);
//		//make mMVPMatrix MVP
//		Matrix.multiplyMM(mvpMatrix, 0, uiMatrix, 0, mvpMatrix, 0);
//		//MVP is MVP at this point
//		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
//
//		// Draw
//
//		/* Draw the arrays as triangles */
//		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.mIdxBufIndex);
//		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.mNumElements, GLES20.GL_UNSIGNED_SHORT, 0);
//
//		//unbind buffers
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
//
//		checkError();
//
//	}
//
//	public void drawLight(Light light) {
//
//		float[] mvpMatrix = new float[16];
//		float[] projectionMatrix = GameWorld.inst().getProjectionMatrix();
//		float[] viewMatrix = GameWorld.inst().getAgentViewMatrix();
//
//		GLES20.glUseProgram(light.mShaderHandle);  
//		final int u_mvp = GLES20.glGetUniformLocation(light.mShaderHandle, "u_MVPMatrix");
//		final int u_li_col = GLES20.glGetUniformLocation(light.mShaderHandle, "u_LightCol");
//		final int a_pos = GLES20.glGetAttribLocation(light.mShaderHandle, "a_Position");
//
//		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, light.mModelMatrix, 0);
//		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
//
//		GLES20.glUniform4fv(u_li_col, 1, light.mRGBAColor, 0);
//		GLES20.glVertexAttrib3f(a_pos, light.mPosition[0], light.mPosition[1], light.mPosition[2]);
//		GLES20.glDisableVertexAttribArray(a_pos);
//		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
//		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
//
//		checkError();
//	}
//
//	public void drawText(Cursor cursor) {
//		if(cursor == null || cursor.getFont() == null) return;
//
//		float[] mvpMatrix = new float[16];
//		float[] uiMatrix = GameWorld.inst().getUiMatrix();
//		float[] viewMatrix = GameWorld.inst().getViewMatrix();
//
//		Font font = cursor.getFont();
//
//		GLES20.glUseProgram(font.mShaderHandle);
//
//		int u_mvp = GLES20.glGetUniformLocation(font.mShaderHandle, "u_MVPMatrix");
//		int u_texsampler = GLES20.glGetUniformLocation(font.mShaderHandle, "u_Texture");
//
//		int a_pos = GLES20.glGetAttribLocation(font.mShaderHandle, "a_Position");
//		int a_txc = GLES20.glGetAttribLocation(font.mShaderHandle, "a_TexCoordinate");
//
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, font.mTextureHandle);
//		GLES20.glUniform1i(u_texsampler, 0);
//
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, font.mVboIndex);
//
//		GLES20.glEnableVertexAttribArray(a_pos);
//		GLES20.glVertexAttribPointer(a_pos, font.mPosSize, GLES20.GL_FLOAT, false, Font.ELEMENTS_STRIDE, Font.POS_OFFSET);
//
//		GLES20.glEnableVertexAttribArray(a_txc);
//		GLES20.glVertexAttribPointer(a_txc, font.mTxcSize, GLES20.GL_FLOAT, false, Font.ELEMENTS_STRIDE, Font.TXC_OFFSET);
//
//		// Draw
//		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, font.mIboIndex);
//
//		for(Cursor.CursorPosition cp : cursor) {
//			// --MV--
//			//make mMVPMatrix MV
//			Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, cp.mModelMatrix, 0);
//			//make mMVPMatrix MVP
//			Matrix.multiplyMM(mvpMatrix, 0, uiMatrix, 0, mvpMatrix, 0);
//			//MVP is MVP at this point
//			GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
//			GLES20.glDrawElements(GLES20.GL_TRIANGLES, Font.NUM_ELEMENTS, GLES20.GL_UNSIGNED_SHORT, cp.mGlyphIndex);
//		}
//
//		//Unbind buffers
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
//
//	}

//	/* ------------------------------ */
//	/*        Utility Methods         */
//	/* ------------------------------ */

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
