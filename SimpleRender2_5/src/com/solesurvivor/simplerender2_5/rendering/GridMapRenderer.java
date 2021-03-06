package com.solesurvivor.simplerender2_5.rendering;

import java.nio.Buffer;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.simplerender2_5.scene.Curve;
import com.solesurvivor.simplerender2_5.scene.Drawable;
import com.solesurvivor.simplerender2_5.scene.DrawableBones;
import com.solesurvivor.simplerender2_5.scene.GeometryBones;
import com.solesurvivor.simplerender2_5.scene.Light;
import com.solesurvivor.simplerender2_5.scene.Skybox;
import com.solesurvivor.simplerender2_5.scene.WaterNode;
import com.solesurvivor.util.SSArrayUtil;

public class GridMapRenderer extends BaseRenderer {
	
	private static final String TAG = GridMapRenderer.class.getSimpleName();
	
	protected Stack<float[]> matrixStack;
	protected boolean writePic = true;
	protected int reflectionShader = -1;
	protected boolean bonesUninit = true;
	
	public GridMapRenderer() {
		clearColor = new float[]{0.5f,0.5f,0.5f,1.0f};		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.matrixStack = new Stack<float[]>();
		super.onSurfaceCreated(gl, config);
	}
	
	public void pushMatrix(float[] matrix) {
		this.matrixStack.push(matrix);
	}
	
	public float[] popMatrix(float[] matrix) {
		return this.matrixStack.pop();
	}
	
	public float[] peekMatrix(float[] matrix) {
		return this.matrixStack.peek();
	}
	
	public void drawGeometry(Drawable draw, float[] modelMatrix) {
		//XXX Need to make a proper dispatcher
		if(draw instanceof GeometryBones
				&& ((GeometryBones)draw).getBones()) {
			drawGeometryBones((DrawableBones)draw, modelMatrix, GLES20.GL_TRIANGLES);
		} else {
			drawGeometry(draw, modelMatrix, GLES20.GL_TRIANGLES);
		}
		
	}
	
	public void drawGeometryTransparent(Drawable draw, float[] modelMatrix) {
		//XXX Need to make a proper dispatcher
		
		GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
		GLES20.glBlendFunc(GLES20.GL_ZERO, GLES20.GL_SRC_COLOR);
		GLES20.glEnable(GLES20.GL_BLEND);
		if(draw instanceof GeometryBones
				&& ((GeometryBones)draw).getBones()) {
			drawGeometryBones((DrawableBones)draw, modelMatrix, GLES20.GL_TRIANGLES);
		} else {
			drawGeometry(draw, modelMatrix, GLES20.GL_TRIANGLES);
		}
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	public void drawWaterPlane(WaterNode wNode) {
		renderSkyboxReflection(wNode.getGeometry(), wNode.getSkybox());
//		renderSkyboxReflection(wNode);
//		drawGeometry(wNode.getGeometry(), wNode.getReflectionTexture().getBuffers(), wNode.getGeometry().getWorldMatrix(), GLES20.GL_TRIANGLES);
	}
	
	public void renderSkyboxReflection(Drawable draw, Skybox skybox) {
		
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();
		
		if(reflectionShader < 0) {
			reflectionShader = ShaderManager.getShaderId("clearwater_shader");
		}
		
		int shaderHandle = reflectionShader;

		GLES20.glUseProgram(shaderHandle);

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");
		int u_iv = GLES20.glGetUniformLocation(shaderHandle, "u_IVMatrix");
		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, skybox.getTextureHandle());
		GLES20.glUniform1i(u_texsampler, 1);

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
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, draw.getIdxBufHandle());
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		// --IV--
		float[] tempViewMatrix = new float[16];
		float[] invertedViewMatrix = new float[16];
		Matrix.setIdentityM(tempViewMatrix, 0);
		System.arraycopy(viewMatrix, 0, tempViewMatrix, 0, 16); /*Do not copy position*/
		Matrix.invertM(invertedViewMatrix, 0, tempViewMatrix, 0);
		GLES20.glUniformMatrix4fv(u_iv, 1, false, invertedViewMatrix, 0);
		
		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvpMatrix, 0, tempViewMatrix, 0, draw.getWorldMatrix(), 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, draw.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkError();
		
		Point viewport = GameWorld.inst().getViewport();
		GLES20.glViewport(0, 0,viewport.x, viewport.y);	
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void renderSkyboxReflection(WaterNode wNode) {
		
		int[] buffers = wNode.getReflectionTexture().getBuffers();
		Skybox skybox = wNode.getSkybox();
		Point size = wNode.getSize();
		Drawable textureTarget = wNode.getReflectionTexture();
		
		clearTexture(buffers);
		
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();
		
		int shaderHandle = ShaderManager.getShaderId("skybox_shader");;

		GLES20.glUseProgram(shaderHandle);

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");
		int u_iv = GLES20.glGetUniformLocation(shaderHandle, "u_IVMatrix");
		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, skybox.getTextureHandle());
		GLES20.glUniform1i(u_texsampler, 1);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureTarget.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, textureTarget.getPosSize(), GLES20.GL_FLOAT, false, textureTarget.getElementStride(), textureTarget.getPosOffset());

		if(a_nrm > -1) {
			GLES20.glEnableVertexAttribArray(a_nrm);
			GLES20.glVertexAttribPointer(a_nrm, textureTarget.getNrmSize(), GLES20.GL_FLOAT, false, textureTarget.getElementStride(), textureTarget.getNrmOffset());
		}

		if(a_txc > -1) {
			GLES20.glEnableVertexAttribArray(a_txc);
			GLES20.glVertexAttribPointer(a_txc, textureTarget.getTxcSize(), GLES20.GL_FLOAT, false, textureTarget.getElementStride(), textureTarget.getTxcOffset());
		}

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

//		GLES20.glViewport(0, 0, size.x, size.y);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, textureTarget.getIdxBufHandle());
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		// --IV--
		float[] tempViewMatrix = new float[16];
		float[] invertedViewMatrix = new float[16];
		Matrix.setIdentityM(tempViewMatrix, 0);
		System.arraycopy(viewMatrix, 0, tempViewMatrix, 0, 16); /*Do not copy position*/
		Matrix.invertM(invertedViewMatrix, 0, tempViewMatrix, 0);
		GLES20.glUniformMatrix4fv(u_iv, 1, false, invertedViewMatrix, 0);
		
		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvpMatrix, 0, tempViewMatrix, 0, textureTarget.getWorldMatrix(), 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mvpMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buffers[2]);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, buffers[1]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, buffers[0]);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, textureTarget.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
		checkError();
		
		Point viewport = GameWorld.inst().getViewport();
		GLES20.glViewport(0, 0,viewport.x, viewport.y);	
		
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	//TODO: Doesn't work
//	private void bufferToImageFile(Point dimensions) {
//		int size = dimensions.x * dimensions.y * DrawingConstants.BYTES_PER_FLOAT;
//		ByteBuffer pixelBuf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
//		GLES20.glReadPixels(0, 0, dimensions.x, dimensions.y, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, pixelBuf);
//		
//		SSLog.d(TAG, "Output image is valid: %s", ETC1.isValid(pixelBuf));
//		ETC1Texture texture = new ETC1Texture(dimensions.x, dimensions.y, pixelBuf);
//				
//		byte[] pixels = new byte[size];
//		pixelBuf.get(pixels);
//		
//		SSLog.d(TAG, "Writing file to directory: %s", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
//		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"testimage.bmp");
//		FileOutputStream out = null;
//
//		try {
//			
//			if(!file.createNewFile()) {
//				try {
//					file.delete();
//				} catch(Exception e){
//					SSLog.d(TAG, "Tried to delete file.", e);
//				}
//				
//				if(!file.createNewFile()) {
//					SSLog.w(TAG, "Could not create file: %s", file.toString());
//				}
//			}
//			
//			out = new FileOutputStream(file);
//			
//			out.write(pixels);
//			//out.write(SSArrayUtil.intToByteArray(pixelBuf.array()));
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			IOUtils.closeQuietly(out);
//		}
//	}
	
	public void drawCurve(Curve c) {
		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();

		GLES20.glUseProgram(pointShader);

		int u_mvp = GLES20.glGetUniformLocation(pointShader, "u_MVPMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(pointShader, "u_LightCol");
		int a_pos = GLES20.glGetAttribLocation(pointShader, "a_Position");

		// --MV--		
		float[] modelMatrix = c.getWorldMatrix();
		
		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
		
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw
		Buffer b = SSArrayUtil.arrayToFloatBuffer(c.getPoints());
		GLES20.glVertexAttribPointer(a_pos, 3, GLES20.GL_FLOAT, false, 3*4, b);      
		GLES20.glEnableVertexAttribArray(a_pos); 
		GLES20.glUniform4f(u_lightpos, 1.0f, 0.179f, 0.009f, 1.0f);
		
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, b.capacity() / 3);

		checkError();
	}
	
	public void drawGeometry(Drawable draw, int[] textureBuffers, float[] modelMatrix, int primType) {

		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = mCurrentCamera.getProjectionMatrix();
		float[] viewMatrix = mCurrentCamera.getViewMatrix();

		int shaderHandle = draw.getShaderHandle();
		int textureHandle = textureBuffers[2];

		GLES20.glUseProgram(shaderHandle);

		int u_mvp = GLES20.glGetUniformLocation(shaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(shaderHandle, "u_MVMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(shaderHandle, "u_LightPos");
		int u_texsampler = GLES20.glGetUniformLocation(shaderHandle, "u_Texture");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, draw.getDatBufHandle());

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");

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
		if(lights != null && lights.size() > 0 && u_lightpos > -1) {
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
	
	public void drawGeometry(Drawable draw, float[] modelMatrix, int primType) {

		float[] mvMatrix = new float[16];
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

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, draw.getDatBufHandle());

		int a_pos = GLES20.glGetAttribLocation(shaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(shaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(shaderHandle, "a_TexCoordinate");

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
		if(lights != null && lights.size() > 0 && u_lightpos > -1) {
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
		
//		if(writePic) {
//			Point viewport = GameWorld.inst().getViewport();
//			bufferToImageFile(viewport);
//			writePic = false;
//		}

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
	public void drawGeometryBones(DrawableBones draw, float[] modelMatrix, int primType) {

		float[] mvMatrix = new float[16];
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
		
		int a_bon_ct = GLES20.glGetAttribLocation(shaderHandle, "a_BonesCount");
		int a_bon_id1 = GLES20.glGetAttribLocation(shaderHandle, "a_BoneIndexOne");
		int a_bon_id2 = GLES20.glGetAttribLocation(shaderHandle, "a_BoneIndexTwo");
		int a_bon_wt1 = GLES20.glGetAttribLocation(shaderHandle, "a_BoneWeightsOne");
		int a_bon_wt2 = GLES20.glGetAttribLocation(shaderHandle, "a_BoneWeightsTwo");

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
		
		if(a_bon_ct > -1 && draw.getBoneCountOffset() > -1 && draw.getBoneCountSize() > -1) {
			//Count
			GLES20.glEnableVertexAttribArray(a_bon_ct);
			GLES20.glVertexAttribPointer(a_bon_ct, draw.getBoneCountSize(), GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getBoneCountOffset());
			
			//Index1
			int boneIndexsize = 3;
			GLES20.glEnableVertexAttribArray(a_bon_id1);
			GLES20.glVertexAttribPointer(a_bon_id1, boneIndexsize, GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getBoneIndexOffset());
			
			//Index2
			GLES20.glEnableVertexAttribArray(a_bon_id2);
			GLES20.glVertexAttribPointer(a_bon_id2, boneIndexsize, GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getBoneIndexOffset() + (boneIndexsize * 4));
			
			//Weight1..
			int boneWeightSize = 3;
			GLES20.glEnableVertexAttribArray(a_bon_wt1);
			GLES20.glVertexAttribPointer(a_bon_wt1, boneWeightSize, GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getBoneWeightOffset());
			
			//Weight2
			GLES20.glEnableVertexAttribArray(a_bon_wt2);
			GLES20.glVertexAttribPointer(a_bon_wt2, boneWeightSize, GLES20.GL_FLOAT, false, draw.getElementStride(), draw.getBoneWeightOffset() + (boneWeightSize * 4));

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
		
		// --Bones--
		
		if(draw.getPose() != null && draw.getRestPoseInv() != null) {
			for(int i = 0; i < draw.getPose().getBones().length; i += DrawingConstants.FOURX_MATRIX_SIZE) {
				int u_Bone = GLES20.glGetUniformLocation(shaderHandle, String.format("u_Bones[%s]",i / DrawingConstants.FOURX_MATRIX_SIZE));
				float[] animatedBone = new float[16];
				Matrix.multiplyMM(animatedBone, 0, draw.getPose().getBones(), i, draw.getRestPoseInv().getBones(), i);
				GLES20.glUniformMatrix4fv(u_Bone, 1, false, animatedBone, 0);
			}
		}

		// --Draw--

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, draw.getIdxBufHandle());
		GLES20.glDrawElements(primType, draw.getNumElements(), GLES20.GL_UNSIGNED_SHORT, draw.getElementOffset());
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();

	}
	
}
