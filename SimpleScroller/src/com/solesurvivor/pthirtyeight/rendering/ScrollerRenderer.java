package com.solesurvivor.pthirtyeight.rendering;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.solesurvivor.pthirtyeight.scene.gameobjects.SpriteNode;
import com.solesurvivor.pthirtyeight.scene.water.Water;

public class ScrollerRenderer extends BaseRenderer {
	
	public void drawSprite(SpriteNode sprite) {
		
		float[] mvpMatrix = new float[16];
		float[] uiMatrix = currentCamera.getOrthoMatrix();
		float[] viewMatrix = currentCamera.getViewMatrix();

		GLES20.glUseProgram(sprite.getShaderHandle());

		int u_mvp = GLES20.glGetUniformLocation(sprite.getShaderHandle(), "u_MVPMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(sprite.getShaderHandle(), "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(sprite.getShaderHandle(), "a_Position");
		int a_txc = GLES20.glGetAttribLocation(sprite.getShaderHandle(), "a_TexCoordinate");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sprite.getTextureHandle());
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sprite.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, sprite.getPosSize(), GLES20.GL_FLOAT, false, sprite.getElementStride(), sprite.getPosOffset());

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, sprite.getTxcSize(), GLES20.GL_FLOAT, false, sprite.getElementStride(), sprite.getTxcOffset());

		// --MV--
		//make mMVPMatrix MV
		Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, sprite.getWorldMatrix(), 0);
		//make mMVPMatrix MVP
		Matrix.multiplyMM(mvpMatrix, 0, uiMatrix, 0, mvpMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);

		// Draw

		/* Draw the arrays as triangles */
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sprite.getIdxBufHandle());
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, sprite.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);

		//unbind buffers
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();
	}
	
	public void drawWater(Water water) {
			
		float[] tempMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		float[] uiMatrix = currentCamera.getOrthoMatrix();
		float[] viewMatrix = currentCamera.getViewMatrix();

		GLES20.glUseProgram(water.getShaderHandle());

		int u_mvp = GLES20.glGetUniformLocation(water.getShaderHandle(), "u_MVPMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(water.getShaderHandle(), "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(water.getShaderHandle(), "a_Position");
		int a_txc = GLES20.glGetAttribLocation(water.getShaderHandle(), "a_TexCoordinate");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, water.getTextureHandle());
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, water.getDatBufHandle());

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, water.getPosSize(), GLES20.GL_FLOAT, false, water.getElementStride(), water.getPosOffset());

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, water.getTxcSize(), GLES20.GL_FLOAT, false, water.getElementStride(), water.getTxcOffset());

		// Draw

		/* Draw the arrays as triangles */
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, water.getIdxBufHandle());
		
		float[] gridMatrix = water.getGridMatrix();
		for(int i = 0; i < gridMatrix.length; i += DrawingConstants.MATRIX_SIZE) {
			// --MV--
			//Multiply in each matrix's particular translation
			Matrix.multiplyMM(tempMatrix, 0, gridMatrix, i, water.getWorldMatrix(), 0);			
			//make mMVPMatrix MV
			Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, tempMatrix, 0);
			//make mMVPMatrix MVP
			Matrix.multiplyMM(mvpMatrix, 0, uiMatrix, 0, tempMatrix, 0);
			//MVP is MVP at this point
			GLES20.glUniformMatrix4fv(u_mvp, 1, false, mvpMatrix, 0);
			
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, water.getNumElements(), GLES20.GL_UNSIGNED_SHORT, 0);
		}

		//unbind buffers
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

		checkError();		
	}

}
