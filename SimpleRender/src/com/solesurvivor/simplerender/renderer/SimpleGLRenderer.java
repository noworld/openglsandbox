package com.solesurvivor.simplerender.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.simplerender.R;
import com.solesurvivor.util.SSArrayUtil;

public class SimpleGLRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = SimpleGLRenderer.class.getSimpleName();

	private static final String NEWLINE = "\n";
	private static final int V_DATA_SIZE = 3;
	private static final int TRIANGLE_NUM_SIDES = 3;

	protected float[] mProjectionMatrix = new float[16];
	protected float[] mMVPMatrix = new float[16]; 
	protected int mShaderHandle = -1;

	protected Context mContext;

	protected float[] mPositions;
	protected float[] mNormals;
	protected FloatBuffer mPosBuf;
	protected FloatBuffer mNrmBuf;
	protected float[] mModelMatrix = new float[16];
	protected float[] mViewMatrix = new float[16];

	protected float[] mLightPos = new float[]{1.0f, 1.0f, -0.25f};

	protected int mReportedError;

	public SimpleGLRenderer(Context context) {
		this.mContext = context;
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		clearOpenGL();
		drawScene();
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		Log.d(TAG, "Renderer.onSurfaceChanged");
		resizeViewport(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		initOpenGL();
		initCamera();
		loadScene();
	}

	protected void drawScene() {

		GLES20.glUseProgram(mShaderHandle);
		
		int u_mvp = GLES20.glGetUniformLocation(mShaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(mShaderHandle, "u_MVMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(mShaderHandle, "u_LightPos");

		int a_pos = GLES20.glGetAttribLocation(mShaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(mShaderHandle, "a_Normal");

		mPosBuf.position(0);
		mNrmBuf.position(0);

		/* Pass in the position information */
		GLES20.glVertexAttribPointer(a_pos, V_DATA_SIZE, GLES20.GL_FLOAT, false, 0, mPosBuf);        
		GLES20.glEnableVertexAttribArray(a_pos); 

		/* Pass in the normal information */
		GLES20.glVertexAttribPointer(a_nrm, V_DATA_SIZE, GLES20.GL_FLOAT, false, 0, mNrmBuf);        
		GLES20.glEnableVertexAttribArray(a_nrm); 

		/*Update the model matrix... animation could go here*/
		updateModelMatrix();

		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mMVPMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		float[] tempMatrix = new float[16];
		Matrix.multiplyMM(tempMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		System.arraycopy(tempMatrix, 0, mMVPMatrix, 0, 16);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mMVPMatrix, 0);

		// --LightPos--

		/* Pass in the light position in eye space.	*/	
		GLES20.glUniform3f(u_lightpos, mLightPos[0], mLightPos[1], mLightPos[2]);

		// Draw
		
		/* Draw the arrays as triangles */
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mPosBuf.capacity()  / TRIANGLE_NUM_SIDES);

		/* Check for errors */
		if(GLES20.glGetError() != GLES20.GL_NO_ERROR
				&& GLES20.glGetError() != mReportedError) {			
			Log.w(TAG, "OpenGL Error Encountered: " + interpretError(GLES20.glGetError()));
			mReportedError = GLES20.glGetError();
		}

	}

	private void updateModelMatrix() {
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.5f); 
	}

	protected void loadScene() {
		Log.d(TAG, "Loading scene.");
		loadShaders();
		loadModel();
	}

	protected void loadModel() {
		Log.d(TAG, "Loading models...");
		Resources res =  mContext.getResources();
		String positions = null;
		String normals = null;
		InputStream posIn = null;
		InputStream nrmIn = null;

		try {
			posIn = res.openRawResource(R.raw.quadsphere_position);
			positions = IOUtils.toString(posIn);
			nrmIn = res.openRawResource(R.raw.quadsphere_normal);
			normals = IOUtils.toString(nrmIn);
		} catch (IOException e) {
			Log.e(TAG, "Error loading model.", e);
		} finally {
			IOUtils.closeQuietly(posIn);
			IOUtils.closeQuietly(nrmIn);
		}

		Log.d(TAG, "Loading positions.");
		mPositions = stringToFloatArray(positions);
		Log.d(TAG, "Loading positions --> buffer...");
		mPosBuf = SSArrayUtil.arrayToFloatBuffer(mPositions);
		Log.d(TAG, "Loading normals.");
		mNormals = stringToFloatArray(normals);
		Log.d(TAG, "Loading normals --> buffer...");
		mNrmBuf = SSArrayUtil.arrayToFloatBuffer(mNormals);
		Log.d(TAG, "Models loaded.");
	}

	protected float[] stringToFloatArray(String data) {
		String[] values = data.split(NEWLINE);
		float[] floats = new float[Integer.parseInt(values[0])];
		int tenPct = floats.length / 10;
		for(int i = 0; i < floats.length; i++) {
			if(i % tenPct == 0) {
				Log.d(TAG, String.format("Percent complete: %s", (i/tenPct)*10));
			}
			floats[i] = Float.parseFloat(values[i + 1]);
		}
		return floats;
	}

	protected void loadShaders() {
		Log.d(TAG, "Loading shaders...");
		Resources res =  mContext.getResources();

		InputStream vShadIn = null;
		InputStream fShadIn = null;
		String vShadCode = null;
		String fShadCode = null;
		try {
			vShadIn = res.openRawResource(R.raw.v_model_diffuse);
			vShadCode = IOUtils.toString(vShadIn);
			fShadIn = res.openRawResource(R.raw.f_white);
			fShadCode = IOUtils.toString(fShadIn);
		} catch (IOException e) {
			Log.e(TAG, "Error loading shaders.", e);
		} finally {
			IOUtils.closeQuietly(vShadIn);
			IOUtils.closeQuietly(fShadIn);
		}

		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vShadCode);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fShadCode);

		mShaderHandle = createAndLinkProgram(vShadHand, fShadHand, new String[]{"a_Position", "a_Normal"});
		Log.d(TAG, "Shaders loaded.");

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

	protected int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Bind attributes
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; ++i)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}

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

	protected void initOpenGL() {
		Log.d(TAG, "Renderer.initOpenGL");

		// Set the background clear color
		GLES20.glClearColor(0.4f, 0.6f, 0.0f, 0.0f);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable Z-buffer?
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

	protected void resizeViewport(int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 200.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}
	
	private void initCamera() {
		Log.d("initCamera", "START");

		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);	

	}

	protected void clearOpenGL() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
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
