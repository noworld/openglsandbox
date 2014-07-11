package com.solesurvivor.simplerender.model;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.solesurvivor.simplerender.Geometry;
import com.solesurvivor.simplerender.animui.BetterAnim;
import com.solesurvivor.simplerender.animui.HAlignType;
import com.solesurvivor.simplerender.animui.VAlignType;
import com.solesurvivor.simplerender.renderer.BetterUiGLTextureRenderer;
import com.solesurvivor.simplerender.renderer.RendererManager;
import com.solesurvivor.simplerender.ui.CircleInputArea;
import com.solesurvivor.simplerender.ui.Polygon2DInputArea;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;

public class GeometryLoader {
	
	private static final String TAG = GeometryLoader.class.getSimpleName();

	public static List<Geometry> loadGeometries(Context context, TypedArray modelArray) {
		List<Geometry> geos = new ArrayList<Geometry>();
		
		Resources res =  context.getResources();
		
		for(int i = 0; i < modelArray.length(); i++) {			
			int resourceId = modelArray.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);
			Log.d(TAG, String.format("Loading resource: %s.", resourceName));
			InputStream is = res.openRawResource(resourceId);
			try {
				geos.addAll(parseGeometry(is));
			} catch (IOException e) {
				Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
		
		}
		
		return geos;
		
	}
	
	@SuppressLint("DefaultLocale")
	private static List<Geometry> parseGeometry(InputStream is) throws IOException {
		List<Geometry> mGeos = new ArrayList<Geometry>();
		
		Map<String,byte[]> zipFiles = new HashMap<String,byte[]>();	
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));	
		
		BetterUiGLTextureRenderer renderer = null;
		GLSurfaceView.Renderer r = RendererManager.getInstance().getRenderer();
		
		if(r instanceof BetterUiGLTextureRenderer) {
			renderer = (BetterUiGLTextureRenderer)r;
		}

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
					geo.mTextureHandle = renderer.getTexture(properties.get("texture_name"));
				}
				
				if(StringUtils.isBlank(properties.get("shader_name"))) {
					geo.mShaderHandle = 1;
				} else {
					Log.d(TAG, String.format("Attempting to load shader %s", properties.get("shader_name")));
					geo.mShaderHandle = renderer.getShader(properties.get("shader_name"));
				}
				
				if(properties.get("obj_type").equals("input_area")) {
					if(properties.get("input_shape").equals("circle")) {
						
						float[] center = SSArrayUtil.parseFloatArray(properties.get("input_center"), ",");
						float radius = Float.parseFloat(properties.get("input_radius"));
						
						geo.mInputArea = new CircleInputArea(new PointF(center[0], center[1]), radius);
						
					} else if(properties.get("input_shape").equals("polygon2d")) {
						
						Float[] hullArray = ArrayUtils.toObject(SSArrayUtil.parseFloatArray(properties.get("input_hull"), ","));
						
						List<Float[]> hull = new ArrayList<Float[]>(hullArray.length/geo.mPosSize);
						for(int i = 0; i < hullArray.length; i += geo.mPosSize) {
							Float[] point = new Float[geo.mPosSize];
							System.arraycopy(hullArray, i, point, 0, geo.mPosSize);	
							hull.add(point);
						}
						
						Log.d(TAG, String.format("HULL SIZE: %s", hull.size()));
						
						geo.mInputArea = new Polygon2DInputArea(hull);
					}
				}
				
				
				/*Parse VBO*/
				String vboName = name + ".v";
				geo.mDatBufIndex = renderer.loadToVbo(zipFiles.get(vboName));
				
				/*Parse IBO*/				
				String iboName = name + ".i";
				geo.mIdxBufIndex = renderer.loadToIbo(zipFiles.get(iboName));
				
				/*Parse Asset*/
				geo.mAssetXml = new String(zipFiles.get("asset.xml"));

				mGeos.add(geo);
				
			}
						
		}
		
		return mGeos;
				
	}
}
