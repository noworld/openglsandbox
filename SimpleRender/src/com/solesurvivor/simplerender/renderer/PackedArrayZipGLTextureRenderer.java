package com.solesurvivor.simplerender.renderer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.simplerender.Geometry;
import com.solesurvivor.simplerender.R;
import com.solesurvivor.simplerender.R.drawable;
import com.solesurvivor.simplerender.R.raw;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;

public class PackedArrayZipGLTextureRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = PackedArrayZipGLTextureRenderer.class.getSimpleName();
	
	private static final boolean DRAW_LIGHT = true;

	private static final String NEWLINE = "\n";
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;

	protected float[] mProjectionMatrix = new float[16];
	protected float[] mMVPMatrix = new float[16]; 
	protected int mLightShaderHandle = -1;
	protected int mShaderHandle = -1;
	protected int mTextureHandle = -1;

	protected Context mContext;

	/*New - Components in an object*/
	Geometry mGeo = new Geometry();
	
	protected float[] mModelMatrix = new float[16];
	protected float[] mViewMatrix = new float[16];	

	protected float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	protected final float[] mLightPosInWorldSpace = new float[4];
	protected final float[] mLightPosInEyeSpace = new float[4];
	protected float[] mLightModelMatrix = new float[16];	

	protected int mReportedError;
	protected String mModelName;

	public PackedArrayZipGLTextureRenderer(Context context, String model) {
		this.mContext = context;
		this.mModelName = model;
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
		
		// Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);      
        Matrix.rotateM(mLightModelMatrix, 0, 20, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
               
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);   
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        
		
		if(DRAW_LIGHT) {
			/*
			 * DRAW LIGHT FOR DEBUGGING
			 * */
			
			GLES20.glUseProgram(mLightShaderHandle);  
			final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mLightShaderHandle, "u_MVPMatrix");
	        final int pointPositionHandle = GLES20.glGetAttribLocation(mLightShaderHandle, "a_Position");
	        
			// Pass in the position.
			GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

			// Since we are not using a buffer object, disable vertex arrays for this attribute.
	        GLES20.glDisableVertexAttribArray(pointPositionHandle);  
			
			// Pass in the transformation matrix.
			GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			// Draw the point.
			GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
		}

		GLES20.glUseProgram(mShaderHandle);
		
		int u_mvp = GLES20.glGetUniformLocation(mShaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(mShaderHandle, "u_MVMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(mShaderHandle, "u_LightPos");
		int u_texsampler = GLES20.glGetUniformLocation(mShaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(mShaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(mShaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(mShaderHandle, "a_TexCoordinate");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		/* New - Only 1 call to bind buffer for vertex attribs*/
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mGeo.mDatBufIndex);

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, mGeo.mPosSize, GLES20.GL_FLOAT, false, mGeo.mElementStride, mGeo.mPosOffset);

		GLES20.glEnableVertexAttribArray(a_nrm);
		GLES20.glVertexAttribPointer(a_nrm, mGeo.mNrmSize, GLES20.GL_FLOAT, false, mGeo.mElementStride, mGeo.mNrmOffset);

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, mGeo.mTxcSize, GLES20.GL_FLOAT, false, mGeo.mElementStride, mGeo.mTxcOffset);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		//Rotate the ball
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
		//Switching to view space...
		GLES20.glUniform3f(u_lightpos, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

		// Draw
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mGeo.mIdxBufIndex);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mGeo.mNumElements, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
				

		/* Check for errors */
		if(GLES20.glGetError() != GLES20.GL_NO_ERROR
				&& GLES20.glGetError() != mReportedError) {			
			Log.w(TAG, "OpenGL Error Encountered: " + interpretError(GLES20.glGetError()));
			mReportedError = GLES20.glGetError();
		}

	}

	private void initModelMatrix() {
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -7.0f); 
	}

	private void updateModelMatrix() {
		Matrix.rotateM(mModelMatrix, 0, 0.5f, 0.5f, 0.5f, 0.0f);
	}

	protected void loadScene() {
		Log.d(TAG, "Loading scene.");
		loadShaders();
		loadTextures();
		loadModel();
		initModelMatrix();
	}

	private void loadTextures() {
		
		int texId = -1;
		
		if(mModelName.equals("monkey")) {
			texId = R.drawable.monkeytex;
		} else if(mModelName.equals("ncfb")) {
			texId = R.drawable.ncfb;
		} else if(mModelName.equals("toruscone")) {
			texId = R.drawable.uvgrid;
		} else if(mModelName.equals("sphere")) {
			texId = R.drawable.spheretex;
		}
		
		mTextureHandle = loadTexture(texId);
	}

	protected void loadModel() {
		Log.d(TAG, "Loading models...");
		Resources res =  mContext.getResources();
		
		int rid = -1;
		
		if(mModelName.equals("monkey")) {
			rid = R.raw.monkey;
		} else if(mModelName.equals("ncfb")) {
			rid = R.raw.ncfb;
		} else if(mModelName.equals("toruscone")) {
			rid = R.raw.toruscone;
		} else if(mModelName.equals("sphere")) {
			rid = R.raw.sphere;
		}
		
//		String resourceName = models.getString(i);
//		int resourceId = models.getResourceId(i, 0);
//		Log.d(TAG, String.format("Loading resource: %s.", resourceName));
		InputStream is = res.openRawResource(rid);
		try {
			parseGeometry(is);
		} catch (IOException e) {
			Log.e(TAG,"Error loading resource.", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		
//		TypedArray models = res.obtainTypedArray(R.array.models);
//		
////		for(int i = 0; i < models.length(); i++) {
//		//TODO: Handle more than 1 model
//		for(int i = 0; i < 1; i++) {
//			String resourceName = models.getString(i);
//			int resourceId = models.getResourceId(i, 0);
//			Log.d(TAG, String.format("Loading resource: %s.", resourceName));
//			InputStream is = res.openRawResource(resourceId);
//			try {
//				parseGeometry(is);
//			} catch (IOException e) {
//				Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
//			} finally {
//				IOUtils.closeQuietly(is);
//			}
//		
//		}
			
//		models.recycle();
	}

	private Geometry parseGeometry(InputStream is) throws IOException {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));	

		//Pretending like each zip has only one model
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String filename = ze.getName();
			
			if(filename.endsWith(".desc") || filename.endsWith(".dsc")){
				String descriptor = IOUtils.toString(zis);
				Map<String,String> properties = SSPropertyUtil.parseFromString(descriptor);
				
				mGeo.mName = properties.get("pos_size");
				
				mGeo.mPosSize = Integer.valueOf(properties.get("pos_size"));
				mGeo.mPosOffset = Integer.valueOf(properties.get("pos_offset"));
				
				mGeo.mNrmSize = Integer.valueOf(properties.get("nrm_size"));
				mGeo.mNrmOffset = Integer.valueOf(properties.get("nrm_offset"));
				
				mGeo.mTxcSize = Integer.valueOf(properties.get("txc_size"));
				mGeo.mTxcOffset = Integer.valueOf(properties.get("txc_offset"));
				
				mGeo.mNumElements = Integer.valueOf(properties.get("num_elements"));
				mGeo.mElementStride = Integer.valueOf(properties.get("element_stride"));
				
			} else if(filename.endsWith(".ibo") || filename.endsWith(".i")) {
				byte[] iboBytes = IOUtils.toByteArray(zis);
				mGeo.mIdxBufIndex = loadToIbo(iboBytes); 
			} else if(filename.endsWith(".vbo") || filename.endsWith(".v")) {
				byte[] vboBbytes = IOUtils.toByteArray(zis);
				mGeo.mDatBufIndex = loadToVbo(vboBbytes); 
			} else if(filename.equals("asset.xml")) {
				String asset = IOUtils.toString(zis);
				mGeo.mAssetXml = asset;
			}
			
		}
		
		return mGeo;
	}

	private int loadToIbo(byte[] iboBytes) {
		return loadGLBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLES20.GL_UNSIGNED_SHORT, iboBytes);
	}

	private int loadToVbo(byte[] vboBbytes) {
		return loadGLBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_FLOAT, vboBbytes);
	}
	
	private int loadGLBuffer(int glTarget, int glType, byte[] bytes) {
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
			dataSize = BYTES_PER_SHORT;

		} else if(glType == GLES20.GL_FLOAT) {

			FloatBuffer vboBuf = SSArrayUtil.bytesToFloatBufBigEndian(bytes);
			buf = vboBuf;
			dataSize = BYTES_PER_FLOAT;
			
		}
		
		GLES20.glBindBuffer(glTarget, bufIdx);
		GLES20.glBufferData(glTarget, buf.capacity() * dataSize, buf, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(glTarget, 0);
				
		return bufIdx;
	}
	
	protected void loadShaders() {
		Log.d(TAG, "Loading shaders...");
		Resources res =  mContext.getResources();

		InputStream vShadIn = null;
		InputStream fShadIn = null;
		String vShadCode = null;
		String fShadCode = null;
		
		try {
			vShadIn = res.openRawResource(R.raw.v_tex_and_light);
			vShadCode = IOUtils.toString(vShadIn);
			fShadIn = res.openRawResource(R.raw.f_tex_and_light);
			fShadCode = IOUtils.toString(fShadIn);
		} catch (IOException e) {
			Log.e(TAG, "Error loading shaders.", e);
		} finally {
			IOUtils.closeQuietly(vShadIn);
			IOUtils.closeQuietly(fShadIn);
		}

		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vShadCode);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fShadCode);

		mShaderHandle = createAndLinkProgram(vShadHand, fShadHand, new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});
		
		
		InputStream vPointShadIn = null;
		InputStream fPointShadIn = null;
		String vPointShadCode = null;
		String fPointShadCode = null;
		
		try {
			vPointShadIn = res.openRawResource(R.raw.point_vertex_shader);
			vPointShadCode = IOUtils.toString(vPointShadIn);
			fPointShadIn = res.openRawResource(R.raw.point_fragment_shader);
			fPointShadCode = IOUtils.toString(fPointShadIn);
		} catch (IOException e) {
			Log.e(TAG, "Error loading point shaders.", e);
		} finally {
			IOUtils.closeQuietly(vPointShadIn);
			IOUtils.closeQuietly(fPointShadIn);
		}

		int vPointShaderHand = compileShader(GLES20.GL_VERTEX_SHADER, vPointShadCode);
		int fPointShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fPointShadCode);

		mLightShaderHandle = createAndLinkProgram(vPointShaderHand, fPointShadHand, new String[]{"a_Position"});
		
		
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
	
	protected int loadTexture(final int resourceId) {
		final int[] textureHandle = new int[1];
		
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;	// No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId, options);
						
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
		
		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}

	protected void initOpenGL() {
		Log.d(TAG, "Renderer.initOpenGL");

		// Set the background clear color
		GLES20.glClearColor(0.6f, 0.4f, 0.0f, 0.0f);

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
