package com.solesurvivor.simplerender2_5.rendering;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.opengl.ETC1;
import android.opengl.ETC1Util.ETC1Texture;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;

import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.game.states.GameStateEnum;
import com.solesurvivor.simplerender2_5.game.states.GameStateManager;
import com.solesurvivor.simplerender2_5.scene.Camera;
import com.solesurvivor.simplerender2_5.scene.Drawable;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.Light;
import com.solesurvivor.simplerender2_5.scene.ProceduralTexture2D;
import com.solesurvivor.simplerender2_5.scene.ProceduralWater2D;
import com.solesurvivor.simplerender2_5.scene.RandomEllipse;
import com.solesurvivor.simplerender2_5.scene.Skybox;
import com.solesurvivor.simplerender2_5.scene.Wave;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class BaseRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = BaseRenderer.class.getSimpleName();

	protected Camera mCurrentCamera;
	protected float[] clearColor = new float[]{0.1f,0.3f,0.6f,1.0f};
	protected int pointShader = -1;

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
		
		this.pointShader = ShaderManager.getShaderId("point_shader");

		GameStateManager.init();
		GameWorld.inst().changeState(GameStateManager.getState(GameStateEnum.GRID_MAP));

	}


	/* ----------------------------------------------------- */
	/* Methods for loading drawables to be called externally */
	/* ----------------------------------------------------- */

	public void setCurrentCamera(Camera cam) {
		this.mCurrentCamera = cam;
	}
	
	public Camera getCurrentCamera() {
		return mCurrentCamera;
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

	public int loadToIbo(byte[] iboBytes) {
		return loadGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes, GLES20.GL_STATIC_DRAW);
	}

	public int loadToVbo(byte[] vboBbytes) {
		return loadGLBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_FLOAT, vboBbytes, GLES20.GL_STATIC_DRAW);
	}
	
	public int loadMixedDataToVbo(byte[] vboBbytes) {
		return loadGLBuffer(GLES20.GL_ARRAY_BUFFER, -1, vboBbytes, GLES20.GL_STATIC_DRAW);
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

		} else {
			ByteBuffer byteBuf = SSArrayUtil.bytesToByteBufBigEndian(bytes);
			buf = byteBuf;
			dataSize = 1;
		}

		GLES20.glBindBuffer(glTarget, bufIdx);
		GLES20.glBufferData(glTarget, buf.capacity() * dataSize, buf, usage);
		GLES20.glBindBuffer(glTarget, 0);

		return bufIdx;
	}

	public int loadShaderProgram(String[] vertCode, String[] fragCode) {

		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vertCode[0]);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fragCode[0]);

		int shaderHandle = createAndLinkProgram(vShadHand, fShadHand);

		return shaderHandle;
	}
	
	public int loadCubeMapEtc1(InputStream[] imageStreams, int[] imageOrder, String[] resourceNames) {
		int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle[0]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			for(int i = 0; i < imageStreams.length; i++) {
				try {
					ETC1Util.loadTexture(imageOrder[i], 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, imageStreams[i]);
				} catch (IOException e) {
					GLES20.glDeleteTextures(1, textureHandle, 0);
					textureHandle[0] = 0;
					SSLog.w(TAG, String.format("Exception encountered trying to load PKM cube map image: %s", resourceNames[i]), e);
					break;
				}
			}
			
			//Unbind
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);

		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public int loadCubeMap(Bitmap[] cubemap, int[] imageOrder) {
		int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle[0]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			for(int i = 0; i < cubemap.length; i++) {
				GLUtils.texImage2D(imageOrder[i], 0, cubemap[i], 0);
			}
			
			//Unbind
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);

		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}
	
	public int loadTextureEtc1(InputStream pkmStream, String resourceName) {

		int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

			try {
				ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, pkmStream);
			} catch (IOException e) {
				GLES20.glDeleteTextures(1, textureHandle, 0);
				textureHandle[0] = 0;
				SSLog.w(TAG, String.format("Exception encountered trying to load PKM image: %s", resourceName), e);
			}
			
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);			
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

			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);		
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
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
		GLES20.glClearColor(clearColor[0],clearColor[1],clearColor[2],clearColor[3]);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		GLES20.glDepthMask( true );

	}

	public void drawSkybox(Skybox skybox) {

		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getAgentViewMatrix();
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
		float[] tempView = new float[16];
		Matrix.setIdentityM(tempView, 0);
		// Do not copy camera translation. It should always be 0,0 in the skybox
		System.arraycopy(viewMatrix, 0, tempView, 0, 12);
		Matrix.multiplyMM(mvpMatrix, 0, tempView, 0, skybox.getWorldMatrix(), 0);
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
	
	public void drawSkydome(Drawable dome) {

		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getAgentViewMatrix();
		int shaderHandle = dome.getShaderHandle();
		int textureHandle = dome.getTextureHandle();

		GLES20.glDepthMask(false);

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

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, dome.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, dome.getPosSize(), GLES20.GL_FLOAT, false, dome.getElementStride(), dome.getPosOffset());
		

		if(a_nrm > -1 && dome.getNrmOffset() > -1 && dome.getNrmSize() > -1) {
			GLES20.glEnableVertexAttribArray(a_nrm);
			GLES20.glVertexAttribPointer(a_nrm, dome.getNrmSize(), GLES20.GL_FLOAT, false, dome.getElementStride(), dome.getNrmOffset());
		}

		if(a_txc > -1 && dome.getTxcOffset() > -1 && dome.getTxcSize() > -1) {
			GLES20.glEnableVertexAttribArray(a_txc);
			GLES20.glVertexAttribPointer(a_txc, dome.getTxcSize(), GLES20.GL_FLOAT, false, dome.getElementStride(), dome.getTxcOffset());
		}


		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		float[] tempView = new float[16];
		Matrix.setIdentityM(tempView, 0);
		// Do not copy camera translation. It should always be 0,0 in the skybox
		System.arraycopy(viewMatrix, 0, tempView, 0, 12);
		Matrix.multiplyMM(mvpMatrix, 0, tempView, 0, dome.getWorldMatrix(), 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw

		/* Draw the arrays as triangles */		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, dome.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, dome.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		GLES20.glDepthMask(true);

		checkError();
	}
	
	public void drawClipMap(Drawable draw, float[] modelMatrix, float mipMult, int xPos, int zPos) {

		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getAgentViewMatrix();
		Vec3 eyePos = mCurrentCamera.getAgentTranslation();

		int shaderHandle = draw.getShaderHandle();
		int textureHandle = draw.getTextureHandle();

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_m = GLES20.glGetUniformLocation(shaderHandle, "u_MMatrix");
		int u_v = GLES20.glGetUniformLocation(shaderHandle, "u_VMatrix");
		int u_vp = GLES20.glGetUniformLocation(shaderHandle, "u_VPMatrix");		
		int u_nrm = GLES20.glGetUniformLocation(shaderHandle, "u_NrmMatrix");
		int u_eye_pos = GLES20.glGetUniformLocation(shaderHandle, "u_EyePos");
		int u_lightdir = GLES20.glGetUniformLocation(shaderHandle, "u_LightDir");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");
		int u_xpos = GLES20.glGetUniformLocation(shaderHandle, "u_XPos");
		int u_zpos = GLES20.glGetUniformLocation(shaderHandle, "u_ZPos");
		int u_mipmult = GLES20.glGetUniformLocation(shaderHandle, "u_MipMult");
		
		if(u_xpos > -1) {
			GLES20.glUniform1f(u_xpos, (float)xPos);
		}
		
		if(u_zpos > -1) {
			GLES20.glUniform1f(u_zpos, (float)zPos);
		}
		
		if(u_mipmult > -1) {
			GLES20.glUniform1f(u_mipmult, mipMult);
		}

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
		
		// --V--
		if(u_v > -1) {
			GLES20.glUniformMatrix4fv(u_v, 1, false, viewMatrix, 0); //1282
		}
		
		// --M--
		float[] temp = new float[16];
		Matrix.setIdentityM(temp, 0);
		Matrix.translateM(temp, 0, eyePos.getX(), 0.0f, eyePos.getZ());
		Matrix.scaleM(temp, 0, mipMult, 1.0f, mipMult);
		float[] newmm = new float[16];		
		Matrix.multiplyMM(newmm, 0, temp, 0, modelMatrix, 0);
		
		if(u_m > -1) {
			GLES20.glUniformMatrix4fv(u_m, 1, false, newmm, 0); //1282
		}
		
		// --VP--
		if(u_vp > -1) {
			float[] vpMatrix = new float[16];
			Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
			GLES20.glUniformMatrix4fv(u_vp, 1, false, vpMatrix, 0); //1282
		}

		// --MV--		
		
		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, newmm, 0);

		if(u_mv > -1) {
			GLES20.glUniformMatrix4fv(u_mv, 1, false, mvMatrix, 0); //1282
		}
		
		//Camera Position
		GLES20.glUniform3fv(u_eye_pos, 1, viewMatrix, 12);
		
		// -- Create the Normal Matrix --
		float[] normalMatrix = new float[16];
		if(!Matrix.invertM(normalMatrix, 0, mvMatrix, 0)) {
			Log.d(TAG,"Could not invert matrix.");
		}
		GLES20.glUniformMatrix4fv(u_nrm, 1, true, normalMatrix, 0); //Transpose

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
		
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// --LightPos--

		/* Pass in the light position in eye space.	*/	
		//Switching to view space...	
		Vec3 lightDir = new Vec3(0.0f, -1.0f, 1.0f);
		lightDir.normalize();
		GLES20.glUniform3f(u_lightdir, lightDir.getX(), lightDir.getY(), lightDir.getZ());
		
		// -- WAVE --
		int u_time = GLES20.glGetUniformLocation(shaderHandle, "u_Time");
		double time = ((double)SystemClock.uptimeMillis()) / 1000.0;
		GLES20.glUniform1f(u_time, (float)time);
		
		int u_watercol = GLES20.glGetUniformLocation(shaderHandle, "u_WaterColor");
		GLES20.glUniform4f(u_watercol, 0.0f, 0.5411764705882353f, 0.90196078431372549019607843137255f, 1.0f);
		
		int u_numwaves = GLES20.glGetUniformLocation(shaderHandle, "u_NumWaves");
		GLES20.glUniform1i(u_numwaves, 1);
		
		int u_wave_type = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.wave_type");
		int u_wave_amp = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.amplitude");
		int u_wave_dir = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.direction");
		int u_wave_wlen = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.wavelength");
		int u_wave_freq = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.frequency");
		int u_wave_speed = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.speed");
		int u_wave_phsco = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.phase_const");
		int u_wave_tims = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.time_scale");
		int u_wave_phssh = GLES20.glGetUniformLocation(shaderHandle, "u_Wave.phase_shift");
		
		float wavelen = 100.0f;
		float freq = (float)Math.sqrt(DrawingConstants.GRAV * (DrawingConstants.TWO_PI/wavelen));
		float speed = (wavelen / freq) + 0.25f;
		float phaseco = wavelen * speed; 
		
		GLES20.glUniform1i(u_wave_type, 0);
		GLES20.glUniform1f(u_wave_amp, 1.5f);
		GLES20.glUniform3f(u_wave_dir, 0.0f, 0.0f, -1.0f);
		GLES20.glUniform1f(u_wave_wlen, wavelen);
		GLES20.glUniform1f(u_wave_freq, freq);
		GLES20.glUniform1f(u_wave_speed, speed);
		GLES20.glUniform1f(u_wave_phsco, phaseco);
		GLES20.glUniform1f(u_wave_tims, 1.0f);
		GLES20.glUniform1f(u_wave_phssh, 0.0f);

		// Draw

		GLES20.glFrontFace(GLES20.GL_CW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, draw.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, draw.getNumElements(), GLES20.GL_UNSIGNED_SHORT, draw.getElementOffset());

		GLES20.glFrontFace(GLES20.GL_CCW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
	public void drawGeometry(Drawable draw) {
		drawGeometry(draw, draw.getWorldMatrix(), GLES20.GL_TRIANGLES);
	}
	
	public void drawGeometry(Drawable draw, float[] modelMatrix) {
		drawGeometry(draw, modelMatrix, GLES20.GL_TRIANGLES);
	}
	
	public void drawGeometryTristrips(Drawable draw, float[] modelMatrix) {
		drawGeometry(draw, modelMatrix, GLES20.GL_TRIANGLE_STRIP);
	}

	public void drawGeometry(Drawable draw, float[] modelMatrix, int primType) {

		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getAgentViewMatrix();

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

		// --LightPos--

		/* Pass in the light position in eye space.	*/	
		//Switching to view space...
		//TODO: Handle multiple lights
		List<Light> lights = draw.getLights();
		if(lights != null && lights.size() > 0) {
			Light light = lights.get(0);
			float[] lightPosWorldSpace = new float[4];
			float[] lightPosEyeSpace = new float[4];
			Matrix.multiplyMV(lightPosWorldSpace, 0, light.mModelMatrix, 0, light.mPosition, 0);
			Matrix.multiplyMV(lightPosEyeSpace, 0, viewMatrix, 0, lightPosWorldSpace, 0);   
			GLES20.glUniform3f(u_lightpos, lightPosEyeSpace[0], lightPosEyeSpace[1], lightPosEyeSpace[2]);
		}

		// Draw

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, draw.getIdxBufHandle());
		GLES20.glDrawElements(primType, draw.getNumElements(), GLES20.GL_UNSIGNED_SHORT, draw.getElementOffset());

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
	public void drawWater(Drawable geo, float[] modelMatrix, List<Wave> waves) {
		
//		Geometry geo = water.getGeometry();

		float[] mvpMatrix = new float[16];
		float[] mvMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getAgentViewMatrix();

		GLES20.glUseProgram(geo.getShaderHandle());

		int u_mvp = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_MVMatrix");
		int u_nrm = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_NrmMatrix");		
		int u_time = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_Time");
		int u_w_col = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_WaterColor");
		int u_s_col = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_SkyColor");
//		int u_numwaves = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_NumWaves");
		int u_num_li = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_NumLights");
		int u_eye_pos = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_EyePos");
		int u_texsampler = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(geo.getShaderHandle(), "a_Position");
		int a_txc = GLES20.glGetAttribLocation(geo.getShaderHandle(), "a_TexCoordinate");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.getTextureId("wavenormals"));
		GLES20.glUniform1i(u_texsampler, 1);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geo.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, geo.getPosSize(), GLES20.GL_FLOAT, false, geo.getElementStride(), geo.getPosOffset());
		
		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, geo.getTxcSize(), GLES20.GL_FLOAT, false, geo.getElementStride(), geo.getTxcOffset());

		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvMatrix, 0); //1282
		
		
		
		// -- Create the Normal Matrix --
		float[] normalMatrix = new float[16];
		if(!Matrix.invertM(normalMatrix, 0, mvMatrix, 0)) {
			Log.d(TAG,"Could not invert matrix.");
		}
		GLES20.glUniformMatrix4fv(u_nrm, 1, true, normalMatrix, 0); //Transpose

		
		
		// --MVP--
		
		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
		
		//Camera Position
		GLES20.glUniform3fv(u_eye_pos, 1, viewMatrix, 12);

		//Lights
//		GLES20.glUniform1i(u_num_li, lights.size());
//		GLES20.glUniform1i(u_num_li, 1);
//		
//		List<Light> lights = geo.getLights();
//		
//		for(int i = 0; i < lights.size(); i++) {
//			Light light = lights.get(i);
//			int u_lightpos = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_LightPositons[%s]",i));
//			float[] lightPosWorldSpace = new float[4];
//			float[] lightPosEyeSpace = new float[4];
//			Matrix.multiplyMV(lightPosWorldSpace, 0, light.mModelMatrix, 0, light.mPosition, 0);
//			Matrix.multiplyMV(lightPosEyeSpace, 0, viewMatrix, 0, lightPosWorldSpace, 0);   
//			lightPosEyeSpace[3] = 0.0f;
//			//GLES20.glUniform3f(u_lightpos, lightPosEyeSpace[0], lightPosEyeSpace[1], lightPosEyeSpace[2]);
//			GLES20.glUniform4fv(u_lightpos, 1, lightPosEyeSpace, 0);
//		}
		
		int u_lightvec = GLES20.glGetUniformLocation(geo.getShaderHandle(), "u_LightVec");
		GLES20.glUniform3f(u_lightvec, 0.0f, -1.0f, -1.0f);

		//Time
		double time = ((double)SystemClock.uptimeMillis()) / 1000.0;
		GLES20.glUniform1f(u_time, (float)time);

		//Water & Sky Color
		GLES20.glUniform4f(u_w_col, 0.3f, 0.34f, 0.33f, 1.0f);
		GLES20.glUniform4f(u_s_col, 0.6f, 0.8f, 1.0f, 1.0f);
		
		
		//Waves
//		GLES20.glUniform1i(u_numwaves, waves.size());
//
//		
//		for(int i = 0; i < waves.size(); i++) { 
//			
//			Wave w = waves.get(i);
//			
//			int u_w_typ = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].wave_type",i));
//			int u_w_amp = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].amplitude",i));
//			int u_w_dir = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].direction",i));
//			int u_w_frq = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].frequency",i));
//			int u_w_spd = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].speed",i));
//			int u_w_phc = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].phase_const",i));
//			int u_w_tms = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].time_scale",i));
//			int u_w_phs = GLES20.glGetUniformLocation(geo.getShaderHandle(), String.format("u_Waves[%s].phase_shift",i));
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

		// Draw	
//		opaqueWaterOn();
//		multiplyWaterOn();
		waterOn();
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);		
		waterOff();
		
		checkError();
		
	}
	
	public void waterOn() {
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDisable(GLES20.GL_DITHER);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void waterOff() {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glDepthMask(true);
		GLES20.glEnable(GLES20.GL_DITHER);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDisable(GLES20.GL_STENCIL_TEST);
		GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void renderHeightmapOld(ProceduralTexture2D tex, int numIterations, float maxHeight, float minHeight, int seed) {

		int[] tempBuf = genTextureBuffer(GameWorld.inst().getViewport());
		clearTexture(tempBuf);
		clearTexture(tex.getBuffers());

		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getOrthoMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();

		int textureHandle = tex.getTextureHandle();
		int shaderHandle = tex.getShaderHandle();

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_dim = GLES20.glGetUniformLocation(shaderHandle, "u_Dimensions");
		int u_height = GLES20.glGetUniformLocation(shaderHandle, "u_Height");
		int u_line_vec0 = GLES20.glGetUniformLocation(shaderHandle, "u_LineVec[0]");
		int u_line_vec1 = GLES20.glGetUniformLocation(shaderHandle, "u_LineVec[1]");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");

		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		
		GLES20.glUniform2f(u_dim, tex.getDimension().x, tex.getDimension().y);


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
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, tex.getWorldMatrix(), 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw

		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, tex.getIdxBufHandle());
		
		float aspect = ((float)tex.getDimension().x) / ((float)tex.getDimension().y);
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
//		float height = 1.0f/((float)numIterations);
//		float height = 0.5f;
//		GLES20.glUniform1f(u_height, height);
		
//		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tex.getBuffers()[1]);
//		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tex.getBuffers()[0]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getBuffers()[2]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tempBuf[2]);
		
		Random randy = new Random(11);
		numIterations = 3;
		for(int i = 0; i < numIterations; i++) {
			
			float height = maxHeight - ((maxHeight - minHeight) * ((float)i)) / numIterations;
			SSLog.d(TAG, "Hieght at iter %s is %.2f", i, height);
			GLES20.glUniform1f(u_height, height);
			
			float x1 = randy.nextFloat();
			float y1 = randy.nextFloat();
			float x2 = randy.nextFloat();
			float y2 = randy.nextFloat();
			//This gives range between -1 and 1
			float dx1 = x2 - x1;
			float dy1 = y2 - y1;
			//Assuming landscape
			dx1 = dx1 * aspect;
			
			x1 = randy.nextFloat();
			y1 = randy.nextFloat();
			x2 = randy.nextFloat();
			y2 = randy.nextFloat();
			float dx2 = x2 - x1;
			float dy2 = y2 - y1;
			//Assuming landscape
			dx2 = dx2 * aspect;
			GLES20.glUniform2f(u_line_vec0, dx1, dy1);
			GLES20.glUniform2f(u_line_vec1, dx2-dx1, dy2-dy1);
			
			SSLog.d(TAG, "Input vectors: (%.2f,%.2f), (%.2f,%.2f)", dx1, dy1, dx2-dx1, dy2-dy1);
			
			if(i % 2 == 0) {
				//On Even counts, render from TEXTURE0 to TEXTURE1
				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
				
				//From texture 0
				GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tempBuf[1]);
				GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tempBuf[0]);
				GLES20.glUniform1i(u_texsampler, 1);
			} else {
				//On Odd counts, render from TEXTURE1 to TEXTURE0
				GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
				GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tex.getBuffers()[1]);
				GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tex.getBuffers()[0]);
				
				//From texture 0
				GLES20.glUniform1i(u_texsampler, 0);				
			}
			
//			if(i % 2 == 0) {
//				GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//				GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tempBuffers[1]);
//				GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tempBuffers[0]);
//				GLES20.glUniform1i(u_texsampler, 1);
//			} else {
//				GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//				GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tex.getBuffers()[1]);
//				GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tex.getBuffers()[0]);
//				GLES20.glUniform1i(u_texsampler, 0);
//			}
			
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, tex.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
			checkError();
		}
		
		//If we did an even number of renderings
		//Then the final rendering is to the temp buffer
		//So copy it over
		if(numIterations % 2 == 0) {
			//Delete the old texture
			GLES20.glDeleteFramebuffers(1, tex.getBuffers(), 0);
			GLES20.glDeleteRenderbuffers(1, tex.getBuffers(), 1);
			GLES20.glDeleteTextures(1, tex.getBuffers(), 2);
			tex.setBuffers(tempBuf);
			
		} else {
			//Delete the temp texture
			GLES20.glDeleteFramebuffers(1, tempBuf, 0);
			GLES20.glDeleteRenderbuffers(1, tempBuf, 1);
			GLES20.glDeleteTextures(1, tempBuf, 2);
		}
		
		
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		//		resetViewportToWorld();
	}
	

	public void renderTexture(ProceduralTexture2D tex) {
		clearTexture(tex.getBuffers());

//		int textureHandle = tex.getTextureHandle();
		int shaderHandle = tex.getShaderHandle();

		GLES20.glUseProgram(shaderHandle);

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.getTextureId("uvgrid"));
		GLES20.glUniform1i(u_texsampler, 1);

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

		// Draw
		GLES20.glViewport(0, 0, tex.getDimension().x, tex.getDimension().y);		
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, tex.getIdxBufHandle());
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getBuffers()[2]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tex.getBuffers()[1]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tex.getBuffers()[0]);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, tex.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkError();

		Point viewport = GameWorld.inst().getViewport();
		GLES20.glViewport(0, 0,viewport.x, viewport.y);	
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	public void renderWaterTexture(ProceduralWater2D tex) {
		clearTexture(tex.getBuffers());

		float[] viewMatrix = new float[16];
		Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
		float[] projectionMatrix = mCurrentCamera.getOrthoMatrix();
		float[] mvpMatrix = new float[16];
		float[] modelMatrix = new float[16];
		Matrix.setIdentityM(modelMatrix, 0);
		int shaderHandle = tex.getShaderHandle();

		GLES20.glUseProgram(shaderHandle);

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");
		int u_mvp = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_MVMatrix");
		int u_nrm = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_NrmMatrix");		
		int u_time = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_Time");
		int u_w_col = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_WaterColor");
		int u_numwaves = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_NumWaves");
		int u_num_li = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_NumLights");
		int u_eye_pos = GLES20.glGetUniformLocation(tex.getShaderHandle(), "u_EyePos");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureManager.getTextureId("uvgrid"));
		GLES20.glUniform1i(u_texsampler, 1);

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

		// Draw
		GLES20.glViewport(0, 0, tex.getDimension().x, tex.getDimension().y);		
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, tex.getIdxBufHandle());
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282
		
		
		
		// -- Create the Normal Matrix --
		float[] normalMatrix = new float[16];
		if(!Matrix.invertM(normalMatrix, 0, mvpMatrix, 0)) {
			Log.d(TAG,"Could not invert matrix.");
		}
		GLES20.glUniformMatrix4fv(u_nrm, 1, true, normalMatrix, 0); //Transpose

		
		
		// --MVP--
		
		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
		
		//Camera Position
		GLES20.glUniform3fv(u_eye_pos, 1, viewMatrix, 12);
		
		GLES20.glUniform1i(u_num_li, 1);
		
		List<Light> lights = tex.getLights();
		
		for(int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			int u_lightpos = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_LightPositons[%s]",i));
			float[] lightPosWorldSpace = new float[4];
			float[] lightPosEyeSpace = new float[4];
			Matrix.multiplyMV(lightPosWorldSpace, 0, light.mModelMatrix, 0, light.mPosition, 0);
			Matrix.multiplyMV(lightPosEyeSpace, 0, viewMatrix, 0, lightPosWorldSpace, 0);   
			//GLES20.glUniform3f(u_lightpos, lightPosEyeSpace[0], lightPosEyeSpace[1], lightPosEyeSpace[2]);
			GLES20.glUniform3fv(u_lightpos, 1, lightPosEyeSpace, 0);
		}
		
		List<Wave> waves = tex.getWaves();
		
		//Waves
		double time = ((double)SystemClock.uptimeMillis()) / 1000.0;
		GLES20.glUniform1f(u_time, (float)time);
		
		GLES20.glUniform4f(u_w_col, 0.0f, 0.5411764705882353f, 0.90196078431372549019607843137255f, 1.0f);
		
		GLES20.glUniform1i(u_numwaves, waves.size());


		for(int i = 0; i < waves.size(); i++) { 

			Wave w = waves.get(i);

			int u_w_typ = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].wave_type",i));
			int u_w_amp = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].amplitude",i));
			int u_w_dir = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].direction",i));
			int u_w_frq = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].frequency",i));
			int u_w_spd = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].speed",i));
			int u_w_phc = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].phase_const",i));
			int u_w_tms = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].time_scale",i));
			int u_w_phs = GLES20.glGetUniformLocation(tex.getShaderHandle(), String.format("u_Waves[%s].phase_shift",i));

			GLES20.glUniform1i(u_w_typ, w.getWaveType());
			GLES20.glUniform1f(u_w_amp, w.getAmplitude());
			GLES20.glUniform3f(u_w_dir, w.getDirection().getX(),w.getDirection().getY(),w.getDirection().getZ());
			GLES20.glUniform1f(u_w_frq, w.getFrequency());
			GLES20.glUniform1f(u_w_spd, w.getSpeed());
			GLES20.glUniform1f(u_w_phc, w.getPhaseConst());
			GLES20.glUniform1f(u_w_tms, w.getTimeScale());
			GLES20.glUniform1f(u_w_phs, w.getPhaseShift());
		}
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getBuffers()[2]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tex.getBuffers()[1]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tex.getBuffers()[0]);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, tex.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkError();

		Point viewport = GameWorld.inst().getViewport();
		GLES20.glViewport(0, 0,viewport.x, viewport.y);	
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	public void renderEllipse(ProceduralTexture2D tex) {

		clearTexture(tex.getBuffers());

		int textureHandle = tex.getTextureHandle();
		int shaderHandle = tex.getShaderHandle();

		GLES20.glUseProgram(shaderHandle);
		
		int u_water = GLES20.glGetUniformLocation(shaderHandle, "u_WaterColor");
		int u_land = GLES20.glGetUniformLocation(shaderHandle, "u_LandColor");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");
		int u_num_ell = GLES20.glGetUniformLocation(shaderHandle, "u_NumEllipses");

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

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

		// Draw
		GLES20.glViewport(0, 0, tex.getDimension().x, tex.getDimension().y);		
		
		//Colors
		GLES20.glUniform4f(u_water, 0.0f, 0.0f, 1.0f, 1.0f);
		GLES20.glUniform4f(u_land, 0.0f, 1.0f, 0.0f, 1.0f);
		
		//Ellipses		
		
		Random randy = new Random(13);
		
		float[] levels = {0.5f, 0.25f};		
		RandomEllipse elli = new RandomEllipse(randy, levels);
		
		int numEllipses = 1;
		
		if(elli.getEllipses() != null) {
			numEllipses += elli.getEllipses().length;
		}
		
		RandomEllipse[] ellipses = new RandomEllipse[numEllipses];
						
		if(elli.getEllipses() != null) {
			System.arraycopy(elli.getEllipses(), 0, ellipses, 1, elli.getEllipses().length);
		}
		
		ellipses[0] = elli;
		
		GLES20.glUniform1i(u_num_ell, numEllipses);
		
		for(int i = 0; i < numEllipses; i++) {			
			
			RandomEllipse elli2 = ellipses[i];
			
			int u_rmin = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].min", i));
			int u_rmaj = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].maj", i));
			int u_rminsq = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].minsq", i));
			int u_rmajsq = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].majsq", i));
			int u_rfocsq = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].rfocsq", i));
			int u_loc = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].loc", i));
			int u_oper = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Ellipses[%s].oper", i));
			
			GLES20.glUniform1f(u_rmin, elli2.getAxes().x);
			GLES20.glUniform1f(u_rminsq, elli2.getAxesSq().x);
			GLES20.glUniform1f(u_rmaj, elli2.getAxes().y);
			GLES20.glUniform1f(u_rmajsq, elli2.getAxesSq().y);
			GLES20.glUniform1f(u_rfocsq, elli2.getRFocSq());
			GLES20.glUniform1i(u_oper, elli2.getOper());
			GLES20.glUniform2f(u_loc, elli2.getLocation().x, elli2.getLocation().y);
		}
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, tex.getIdxBufHandle());
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getBuffers()[2]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tex.getBuffers()[1]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, tex.getBuffers()[0]);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, tex.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkError();

		Point viewport = GameWorld.inst().getViewport();
		GLES20.glViewport(0, 0,viewport.x, viewport.y);	
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		//		resetViewportToWorld();
	}

	public void clearTexture(int[] buffers) {
		//frame, depth, texture

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

		GLES20.glClearColor(0.0f, 0.2f, 0.6f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}

	public void drawUI(Geometry geo) {

		float[] mvpMatrix = new float[16];
		float[] uiMatrix = mCurrentCamera.getOrthoMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();

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
