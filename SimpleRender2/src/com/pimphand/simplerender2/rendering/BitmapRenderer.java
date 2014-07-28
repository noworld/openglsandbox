package com.pimphand.simplerender2.rendering;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.pimphand.simplerender2.game.GameWorld;
import com.pimphand.simplerender2.rendering.shaders.ShaderManager;
import com.pimphand.simplerender2.scene.Light;

public class BitmapRenderer extends BaseRenderer {
	
	private int mShader = 0;
	
	@SuppressWarnings("unused")
	private static final String TAG = BitmapRenderer.class.getSimpleName();
	
	public void drawBitmap(Bitmap bitmap, Light light) {
		
		if(mShader == 0) {
			mShader = ShaderManager.getShaderId("bitmap_experimental_shader");
		}
		
		float[] mvpMatrix = new float[16];
		float[] projectionMatrix = GameWorld.inst().getCamera().getProjectionMatrix();
		float[] viewMatrix = GameWorld.inst().getCamera().getViewMatrix();

		GLES20.glUseProgram(light.mShaderHandle);  
		final int u_mvp = GLES20.glGetUniformLocation(light.mShaderHandle, "u_MVPMatrix");
		final int a_pos = GLES20.glGetAttribLocation(light.mShaderHandle, "a_Position");

		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, light.mModelMatrix, 0);
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

		GLES20.glVertexAttrib3f(a_pos, light.mPosition[0], light.mPosition[1], light.mPosition[2]);
		GLES20.glDisableVertexAttribArray(a_pos);
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
		
		checkError();
	}
}
