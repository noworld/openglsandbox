package com.solesurvivor.simplerender2.loading;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.Geometry;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.util.SSPropertyUtil;

public class GeometryManager {

	private static final String TAG = GeometryManager.class.getSimpleName();
	private static final String NEWLINE = "\r\n";
	private static final String PLUS = "+";
	
	private static Map<String, Geometry> mGeometries = new HashMap<String, Geometry>();
	
	private GeometryManager() {
		
	}
	
	public static Geometry addGeometry(String name, Geometry geometry) {
		return mGeometries.put(name, geometry);
	}
	
	public static Geometry getGeometry(String name) {
		return mGeometries.get(name);
	}
	
	public static Geometry removeGeometry(String name) {
		return mGeometries.remove(name);
	}
	
	public static void init() {

		loadModelArray(R.array.models);
	}
	
	public static void loadModelArray(int arrayId) {
		Resources res =  GameGlobal.inst().getContext().getResources();
		TypedArray modelArray = res.obtainTypedArray(arrayId);
		
		for(int i = 0; i < modelArray.length(); i++) {			

			//Get the data from resources
			int resourceId = modelArray.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);			
			InputStream is = res.openRawResource(resourceId);
			
			try {
				loadGeometries(resourceId, resourceName, is);
			} finally {
				IOUtils.closeQuietly(is);
			}

		}
		
		modelArray.recycle();
	}

	private static void loadGeometries(int resourceId, String resourceName, InputStream is) {

		Log.d(TAG, String.format("Loading resource: %s.", resourceName));

		//Parse the zip file
		RawGeometryBundle ig = null;
		try {
			ig = parseRawGeometry(is);				
		} catch (IOException e) {
			Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		//Parse the game objects according to type
		for(String name : ig.mObjectNames) {
			Geometry g = parseGeometry(name, ig);
			mGeometries.put(name, g);
		}
		
	}

	private static RawGeometryBundle parseRawGeometry(InputStream is) throws IOException {
		RawGeometryBundle rawGeo = new RawGeometryBundle();

		rawGeo.mFiles = parseFiles(is);

		rawGeo.mObjectNames = parseObjectNames(rawGeo);

		rawGeo.mDescriptors = parseDescriptors(rawGeo);

		return rawGeo;
	}
	
	private static Map<String, byte[]> parseFiles(InputStream is) throws IOException {
		Map<String,byte[]> zipFiles = new HashMap<String,byte[]>();	
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));

		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String fileName = ze.getName();
			byte[] contents = IOUtils.toByteArray(zis);
			zipFiles.put(fileName, contents);			
		}
		return zipFiles;
	}
	
	private static List<String> parseObjectNames(RawGeometryBundle rawGeo) {
		List<String> objNames = new ArrayList<String>();

		String indexFileName = GameGlobal.inst().getVal(GlobalKeysEnum.INDEX_FILE_NAME);
		String index = new String(rawGeo.mFiles.get(indexFileName));
		String[] files = index.split(NEWLINE);

		for(String s : files) {			
			if(s.startsWith(PLUS)) {
				objNames.add(s.substring(1)); //chop off the +
			}
		}

		return objNames;
	}

	private static Map<String, Map<String, String>> parseDescriptors(RawGeometryBundle rawGeo) {
		Map<String,Map<String,String>> descriptors = new HashMap<String, Map<String,String>>();

		String descExt = GameGlobal.inst().getVal(GlobalKeysEnum.DESCRIPTOR_FILE_EXT);

		for(String s : rawGeo.mObjectNames) {						
			String descName = s + descExt;
			Map<String,String> desc = SSPropertyUtil.parseFromString(new String(rawGeo.mFiles.get(descName)));
			descriptors.put(s, desc);
		}

		return descriptors;
	}
	
	private static Geometry parseGeometry(String name, RawGeometryBundle ig) {		

		Geometry geo = new Geometry();
		geo.mName = name;
		String assetFileName = GameGlobal.inst().getVal(GlobalKeysEnum.ASSET_FILE_NAME);
		geo.mAssetXml = new String(ig.mFiles.get(assetFileName));


		StringBuilder vboFile = new StringBuilder(name);
		vboFile.append(GameGlobal.inst().getVal(GlobalKeysEnum.VBO_FILE_EXT));

		StringBuilder iboFile = new StringBuilder(name);
		iboFile.append(GameGlobal.inst().getVal(GlobalKeysEnum.IBO_FILE_EXT));

		BaseRenderer rend = RendererManager.inst().getRenderer();

		geo.mDatBufIndex = rend.loadToVbo(ig.mFiles.get(vboFile.toString()));
		geo.mIdxBufIndex = rend.loadToIbo(ig.mFiles.get(iboFile.toString()));

		Map<String,String> desc = ig.mDescriptors.get(name);

		geo.mPosSize = Integer.valueOf(desc.get(DescriptorKeysEnum.POS_SIZE.toString()));
		geo.mPosOffset = Integer.valueOf(desc.get(DescriptorKeysEnum.POS_OFFSET.toString()));

		geo.mNrmSize = Integer.valueOf(desc.get(DescriptorKeysEnum.NRM_SIZE.toString()));
		geo.mNrmOffset = Integer.valueOf(desc.get(DescriptorKeysEnum.NRM_OFFSET.toString()));

		geo.mTxcSize = Integer.valueOf(desc.get(DescriptorKeysEnum.TXC_SIZE.toString()));
		geo.mTxcOffset = Integer.valueOf(desc.get(DescriptorKeysEnum.TXC_OFFSET.toString()));

		geo.mNumElements = Integer.valueOf(desc.get(DescriptorKeysEnum.NUM_ELEMENTS.toString()));
		geo.mElementStride = Integer.valueOf(desc.get(DescriptorKeysEnum.ELEMENT_STRIDE.toString()));
		
		return geo;

	}
	
	private static class RawGeometryBundle {
		public List<String> mObjectNames;
		public Map<String,byte[]> mFiles;
		public Map<String,Map<String,String>> mDescriptors;
	}
}
