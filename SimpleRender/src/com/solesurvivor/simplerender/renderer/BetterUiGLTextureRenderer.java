package com.solesurvivor.simplerender.renderer;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.solesurvivor.simplerender.Font;
import com.solesurvivor.simplerender.Geometry;
import com.solesurvivor.simplerender.InputHandler;
import com.solesurvivor.simplerender.R;
import com.solesurvivor.simplerender.animui.BetterAnim;
import com.solesurvivor.simplerender.animui.HAlignType;
import com.solesurvivor.simplerender.animui.VAlignType;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;

@SuppressLint("DefaultLocale")
public class BetterUiGLTextureRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = BetterUiGLTextureRenderer.class.getSimpleName();
	
	private static final boolean DRAW_LIGHT = true;
	private static final boolean DRAW_GLYPH = true;

	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	
	private static final Pattern mGlslAttrPattern = Pattern.compile("attribute\\s+[a-zA-Z0-9]+\\s+([a-zA-Z0-9_]+);");

	protected float[] mProjectionMatrix = new float[16];
	protected float[] mUIMatrix = new float[16];
	protected float[] mMVPMatrix = new float[16]; 
	protected int mLightShaderHandle = -1;

	protected Context mContext;

	/*New - Components in an object*/
	private List<Geometry> mGeos = new ArrayList<Geometry>();
	private List<Geometry> mUis = new ArrayList<Geometry>();
	private Map<String,Integer> mTextures = new HashMap<String,Integer>();
	private Map<String,Integer> mShaders = new HashMap<String,Integer>();
	
	/* New - a Font */
	private Map<String,Font> mFonts = new HashMap<String,Font>();

	protected float[] mViewMatrix = new float[16];	

	protected float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	protected final float[] mLightPosInWorldSpace = new float[4];
	protected final float[] mLightPosInEyeSpace = new float[4];
	protected float[] mLightModelMatrix = new float[16];	

	protected int mReportedError;

	public BetterUiGLTextureRenderer(Context context) {
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
		BetterAnim.mScreenWidth = width;
		BetterAnim.mScreenHeight = height;
		resizeViewport(width, height);
		InputHandler.mInputs.clear();
		for(Geometry ui : mUis) {
			BetterAnim.positionUI(ui);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		initOpenGL();
		initCamera();
		loadScene();
	}

	protected void drawScene() {
		
		//Draw light position for debugging
		if(DRAW_LIGHT) {
			drawLightAsPoint();
		}

		//Draw loaded geometry
		for(Geometry geo : mGeos) {
			drawGeometry(geo);

			/* Check for errors */
			if(GLES20.glGetError() != GLES20.GL_NO_ERROR
					&& GLES20.glGetError() != mReportedError) {			
				Log.w(TAG, "OpenGL Error Encountered: " + interpretError(GLES20.glGetError()));
				mReportedError = GLES20.glGetError();
			}
		}
		
		//Always draw UI last
		for(Geometry ui : mUis) {
			drawUI(ui);
			
			/* Check for errors */
			if(GLES20.glGetError() != GLES20.GL_NO_ERROR
					&& GLES20.glGetError() != mReportedError) {			
				Log.w(TAG, "OpenGL Error Encountered: " + interpretError(GLES20.glGetError()));
				mReportedError = GLES20.glGetError();
			}
		}
		
		//Text Drawing
		if(DRAW_GLYPH) {
			Font font = mFonts.get("Praetorium BB Regular");
			drawGlyph(font, 'b');
		}

	}
	
	private void drawLightAsPoint() {

		GLES20.glUseProgram(mLightShaderHandle);  
		final int u_mvp = GLES20.glGetUniformLocation(mLightShaderHandle, "u_MVPMatrix");
		final int a_pos = GLES20.glGetAttribLocation(mLightShaderHandle, "a_Position");

		//TODO: This goes in the scene graph or something
		// Calculate position of the light. Rotate and then push into the distance.
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);      
		Matrix.rotateM(mLightModelMatrix, 0, 20, 0.0f, 1.0f, 0.0f);
		Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
		//Build the MVP matrix
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);   
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

		GLES20.glVertexAttrib3f(a_pos, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
		GLES20.glDisableVertexAttribArray(a_pos);  
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
	}

	public void drawGlyph(Font font, char glyph) {

		GLES20.glUseProgram(font.mShaderHandle);
		
		int u_mvp = GLES20.glGetUniformLocation(font.mShaderHandle, "u_MVPMatrix");
		int u_texsampler = GLES20.glGetUniformLocation(font.mShaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(font.mShaderHandle, "a_Position");
		int a_txc = GLES20.glGetAttribLocation(font.mShaderHandle, "a_TexCoordinate");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, font.mTextureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, font.mVboIndex);

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, font.mPosSize, GLES20.GL_FLOAT, false, Font.ELEMENTS_STRIDE, Font.POS_OFFSET);

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, font.mTxcSize, GLES20.GL_FLOAT, false, Font.ELEMENTS_STRIDE, Font.TXC_OFFSET);
		
		//TODO: Move this logic to the Cursor class
		Matrix.setIdentityM(font.mModelMatrix, 0);
		Matrix.translateM(font.mModelMatrix, 0, 100.0f, 0.0f, -4.0f);
		Matrix.scaleM(font.mModelMatrix, 0, 10.0f, 10.0f, 0.0f);
		
		// --MV--
		//make mMVPMatrix MV
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, font.mModelMatrix, 0);
		//make mMVPMatrix MVP
		Matrix.multiplyMM(mMVPMatrix, 0, mUIMatrix, 0, mMVPMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mMVPMatrix, 0);

		// Draw
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, font.mIboIndex);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, Font.NUM_ELEMENTS, GLES20.GL_UNSIGNED_SHORT, BYTES_PER_SHORT * font.getGlyphIndex(glyph));
		
		//Unbind buffers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
	}

	private void drawUI(Geometry geo) {

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
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, geo.mModelMatrix, 0);
		//make mMVPMatrix MVP
		Matrix.multiplyMM(mMVPMatrix, 0, mUIMatrix, 0, mMVPMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mMVPMatrix, 0);

		// Draw
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.mIdxBufIndex);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.mNumElements, GLES20.GL_UNSIGNED_SHORT, 0);

		//unbind budders
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
	}

	private void drawGeometry(Geometry geo) {
				
		GLES20.glUseProgram(geo.mShaderHandle);
		
		int u_mvp = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_MVPMatrix");
		int u_mv = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_MVMatrix");
		int u_lightpos = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_LightPos");
		int u_texsampler = GLES20.glGetUniformLocation(geo.mShaderHandle, "u_Texture");

		int a_pos = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_Position");
		int a_nrm = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_Normal");
		int a_txc = GLES20.glGetAttribLocation(geo.mShaderHandle, "a_TexCoordinate");
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, geo.mTextureHandle);
		GLES20.glUniform1i(u_texsampler, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geo.mDatBufIndex);

		GLES20.glEnableVertexAttribArray(a_pos);
		GLES20.glVertexAttribPointer(a_pos, geo.mPosSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mPosOffset);

		GLES20.glEnableVertexAttribArray(a_nrm);
		GLES20.glVertexAttribPointer(a_nrm, geo.mNrmSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mNrmOffset);

		GLES20.glEnableVertexAttribArray(a_txc);
		GLES20.glVertexAttribPointer(a_txc, geo.mTxcSize, GLES20.GL_FLOAT, false, geo.mElementStride, geo.mTxcOffset);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		geo = BetterAnim.updateSprite(geo);		
		
		// --MV--

		/* Get the MV Matrix: Multiply V * M  = MV */
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, geo.mModelMatrix, 0);
		//MVP matrix is *actually MV* at this point
		GLES20.glUniformMatrix4fv(u_mv, 1, false, mMVPMatrix, 0); //1282

		// --MVP--

		/* Get the MVP Matrix: Multiply P * MV = MVP*/
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		//MVP is MVP at this point
		GLES20.glUniformMatrix4fv(u_mvp, 1, false, mMVPMatrix, 0);

		// --LightPos--

		/* Pass in the light position in eye space.	*/	
		//Switching to view space...
		GLES20.glUniform3f(u_lightpos, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

		// Draw
		
		/* Draw the arrays as triangles */
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, geo.mIdxBufIndex);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, geo.mNumElements, GLES20.GL_UNSIGNED_SHORT, 0);

		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
	}	

	protected void loadScene() {
		Log.d(TAG, "Loading scene.");
		loadShaders();
		loadTextures();
		loadModel();	
		loadFonts();
	}

	private void loadFonts() {
		
		
		Resources res =  mContext.getResources();

		TypedArray fonts = res.obtainTypedArray(R.array.fonts);
		
		for(int i = 0; i < fonts.length(); i++) {			
			int resourceId = fonts.getResourceId(i, 0);
			Font f = loadFont(resourceId);
			mFonts.put(f.mName, f);			
		}
		
		fonts.recycle();				
	}

	private Font loadFont(int resourceId) {
		
		Resources res =  mContext.getResources();
		
		InputStream is = null;
		String resourceName = res.getResourceEntryName(resourceId);
		Font font = null;
		 
		try {
			
			is = res.openRawResource(resourceId);
			String fontDescriptor = IOUtils.toString(is);
			Map<String,String> props = SSPropertyUtil.parseFromString(fontDescriptor);

			font = new Font(props);
			font.mShaderHandle = mShaders.get(props.get("shader"));
			font.mTextureHandle = mTextures.get(props.get("texture"));
			font.mIboIndex = loadToIbo(font.getIbo());
			font.mVboIndex = loadToVbo(font.getVbo());
			
		} catch (IOException e) {
			Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		
		return font;
	}

	private void loadTextures() {
		
		Log.i(TAG, String.format("Supports ETC1 Textures: %s", ETC1Util.isETC1Supported()));
		
		Resources res =  mContext.getResources();

		TypedArray textures = res.obtainTypedArray(R.array.textures);
		
		for(int i = 0; i < textures.length(); i++) {			
			int resourceId = textures.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);
			mTextures.put(resourceName, loadTexture(resourceId));			
		}
		
		textures.recycle();
	}

	protected void loadModel() {
		Log.d(TAG, "Loading models...");
		Resources res =  mContext.getResources();

		TypedArray models = res.obtainTypedArray(R.array.betterUiModels);
		
		for(int i = 0; i < models.length(); i++) {			
			int resourceId = models.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);
			Log.d(TAG, String.format("Loading resource: %s.", resourceName));
			InputStream is = res.openRawResource(resourceId);
			try {
				parseGeometry(is);
			} catch (IOException e) {
				Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		
		}
			
		models.recycle();
	}

	private void parseGeometry(InputStream is) throws IOException {
		Map<String,byte[]> zipFiles = new HashMap<String,byte[]>();	
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));	

		//Pretending like each zip has only one model
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String fileName = ze.getName();
			byte[] contents = IOUtils.toByteArray(zis);
			zipFiles.put(fileName, contents);			
		}
		
		String index = new String(zipFiles.get("index"));
		if(StringUtils.isBlank(index)) {
			throw new FileNotFoundException("Model index not in geometry zip.");
		}
		
		String[] files = index.split("\r\n");
		
		if(files.length < 1) {
			files = index.split("\n");
		}
		
		for(String s : files) {			
			
			if(s.startsWith("+")) {
				/* New - for loading multiple models */
				Geometry geo = new Geometry();
				geo.mModelMatrix = BetterAnim.initModelMatrix();
				
				String name = s.substring(1);
				
				/*Parse Descriptor*/
				String descriptorName = name + ".dsc";
				
				if(zipFiles.get(descriptorName) == null) {
					descriptorName = name + ".desc";
				}
				
				String descriptor = new String(zipFiles.get(descriptorName));
				
				Map<String,String> properties = SSPropertyUtil.parseFromString(descriptor);
				
				geo.mName = properties.get("name");
				
				geo.mPosSize = Integer.valueOf(properties.get("pos_size"));
				geo.mPosOffset = Integer.valueOf(properties.get("pos_offset"));
				
				geo.mNrmSize = Integer.valueOf(properties.get("nrm_size"));
				geo.mNrmOffset = Integer.valueOf(properties.get("nrm_offset"));
				
				geo.mTxcSize = Integer.valueOf(properties.get("txc_size"));
				geo.mTxcOffset = Integer.valueOf(properties.get("txc_offset"));
				
				geo.mNumElements = Integer.valueOf(properties.get("num_elements"));
				geo.mElementStride = Integer.valueOf(properties.get("element_stride"));
				
				geo.mXSize = Float.valueOf(properties.get("x_size"));
				geo.mYSize = Float.valueOf(properties.get("y_size"));
				geo.mZSize = Float.valueOf(properties.get("z_size"));
				
				if(StringUtils.isNotBlank(properties.get("priority"))) {
					geo.mPriority = Integer.valueOf(properties.get("priority"));
				}
				
				if(StringUtils.isNotBlank(properties.get("h_align"))) {
					geo.mHAlign = HAlignType.valueOf(properties.get("h_align").toUpperCase());
				}
				
				if(StringUtils.isNotBlank(properties.get("v_align"))) {
					geo.mVAlign = VAlignType.valueOf(properties.get("v_align").toUpperCase());
				}
				
				if(StringUtils.isBlank(properties.get("texture_name"))) {
					geo.mTextureHandle = 1;
				} else {
					geo.mTextureHandle = mTextures.get(properties.get("texture_name"));
				}
				
				if(StringUtils.isBlank(properties.get("shader_name"))) {
					geo.mShaderHandle = 1;
				} else {
					Log.d(TAG, String.format("Attempting to load shader %s", properties.get("shader_name")));
					geo.mShaderHandle = mShaders.get(properties.get("shader_name"));
				}
				
				
				/*Parse VBO*/
				String vboName = name + ".v";
				geo.mDatBufIndex = loadToVbo(zipFiles.get(vboName));
				
				/*Parse IBO*/				
				String iboName = name + ".i";
				geo.mIdxBufIndex = loadToIbo(zipFiles.get(iboName));
				
				/*Parse Asset*/
				geo.mAssetXml = new String(zipFiles.get("asset.xml"));
				
				if(StringUtils.isNotBlank(properties.get("obj_type"))
						&& properties.get("obj_type").equals("ui_element")) {
					mUis.add(geo);
				} else {
					mGeos.add(geo);
				}
			}
						
		}
				
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
		
		TypedArray shaderPrograms = res.obtainTypedArray(R.array.shaderPrograms);		
		
		for(int i = 0; i < shaderPrograms.length(); i++) {			
			int resourceId = shaderPrograms.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);
			Log.d(TAG, String.format("Loading resource: %s.", resourceName));			
			
			mShaders.put(resourceName, loadShaderProgram(resourceId));
		
		}
			
		shaderPrograms.recycle();
		
		mLightShaderHandle = mShaders.get("point_shader");
		
		Log.d(TAG, "Shaders loaded.");

	}

	private int loadShaderProgram(int resourceId) {
		
		InputStream vShadIn = null;
		InputStream fShadIn = null;
		String vShadCode = null;
		String fShadCode = null;
		
		Resources res =  mContext.getResources();
		
		TypedArray shaders = res.obtainTypedArray(resourceId);
		
		//HACK: For now, assume only 1 v and f shader
		
		int vShadId = shaders.getResourceId(0, 0);		
		int fShadId = shaders.getResourceId(1, 0);
		int shaderHandle;

		try {
			vShadIn = res.openRawResource(vShadId);
			vShadCode = IOUtils.toString(vShadIn);
			fShadIn = res.openRawResource(fShadId);
			fShadCode = IOUtils.toString(fShadIn);
		} catch (IOException e) {
			Log.e(TAG, "Error loading shaders.", e);
		} finally {
			IOUtils.closeQuietly(vShadIn);
			IOUtils.closeQuietly(fShadIn);
		}

		int vShadHand = compileShader(GLES20.GL_VERTEX_SHADER, vShadCode);
		int fShadHand = compileShader(GLES20.GL_FRAGMENT_SHADER, fShadCode);

		shaderHandle = createAndLinkProgram(vShadHand, fShadHand, parseAttributes(vShadCode));
		
		shaders.recycle();
		return shaderHandle;
	}

	private String[] parseAttributes(String vShadCode) {
		Matcher m = mGlslAttrPattern.matcher(vShadCode);
		List<String> attrs = new ArrayList<String>();
		
		while(m.find()) {
			attrs.add(m.group(1));
		}
		
		return attrs.toArray(new String[attrs.size()]);
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
			//TODO: Find out how to use this properly...
//			if (attributes != null)
//			{
//				final int size = attributes.length;
//				for (int i = 0; i < size; ++i)
//				{
//					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
//				}						
//			}

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
		GLES20.glClearColor(0.2f, 0.4f, 0.6f, 1.0f);
//		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable Z-buffer?
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		//Enable alpha channels
		GLES20.glEnable(GLES20.GL_BLEND);
//		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	
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
		
		final float left_ortho = -(width/2);
		final float right_ortho = (width/2);
		final float bottom_ortho = -(height/2);
		final float top_ortho = (height/2);
		final float near_ortho = 1.0f;
		final float far_ortho = 200.0f;

		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		Matrix.orthoM(mUIMatrix, 0, left_ortho, right_ortho, bottom_ortho, top_ortho, near_ortho, far_ortho);
		
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
