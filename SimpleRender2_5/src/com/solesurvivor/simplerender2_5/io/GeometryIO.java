package com.solesurvivor.simplerender2_5.io;

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
import org.apache.commons.lang.ArrayUtils;

import android.content.res.Resources;

import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2_5.input.InputHandler;
import com.solesurvivor.simplerender2_5.input.InputUiElement;
import com.solesurvivor.simplerender2_5.input.Polygon2DInputArea;
import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.TerrainClipmap;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;

public class GeometryIO {
	
	
	@SuppressWarnings("unused")
	private static final String TAG = GeometryIO.class.getSimpleName();
	
	public static TerrainClipmap loadClipmap(int resId) throws IOException {
		List<Geometry> geoList = new ArrayList<Geometry>();

		IntermediateGeometry ig = parseIntermediateGeometry(resId);
		BaseRenderer ren = RendererManager.getRenderer();
		TerrainClipmap.ClipmapData cmapData = new TerrainClipmap.ClipmapData();
		
		for(String s : ig.mObjectNames) {
			Map<String,String> desc = ig.mDescriptors.get(s);
			if(desc.get("OBJECT_TYPE").equals("GEO_MIPMAP")) {
				String name = desc.get("OBJECT_NAME");
				int shader = ShaderManager.getShaderId(desc.get("SHADER"), "tex_shader");
				int texture = TextureManager.getTextureId(desc.get("TEXTURE"), "orient");
				int numElements = Integer.valueOf(desc.get("NUM_ELEMENTS"));
				int elementStride = Integer.valueOf(desc.get("ELEMENT_STRIDE"));
				int posSize = Integer.valueOf(desc.get("POS_SIZE"));
				int posOffset = Integer.valueOf(desc.get("POS_OFFSET"));
				int nrmSize = Integer.valueOf(desc.get("NRM_SIZE"));
				int nrmOffset = Integer.valueOf(desc.get("NRM_OFFSET"));
				int txcSize = Integer.valueOf(desc.get("TXC_SIZE"));
				int txcOffset = Integer.valueOf(desc.get("TXC_OFFSET"));

				cmapData.mResolution =  Integer.valueOf(desc.get("RESOLUTION"));
				cmapData.mSideLength =  Integer.valueOf(desc.get("SIDE_LENGTH"));

				byte[] vboBytes = ig.mFiles.get(s + ".v");
				byte[] iboBytes = ig.mFiles.get(s + ".i");

				int datBufHandle = ren.loadToVbo(vboBytes);
				int idxBufHandle = ren.loadToIbo(iboBytes);

				Geometry geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture);
				geoList.add(geo);
			}
		}
		
		//Assume only 1 clipmap object
		Geometry clipMesh = geoList.get(0);
		TerrainClipmap clipmap = new TerrainClipmap(clipMesh, cmapData);
		
		return clipmap;
	}
	
	public static List<InputHandler> loadInputHandlers(int resId) throws IOException {
		List<InputHandler> inputList = new ArrayList<InputHandler>();

		IntermediateGeometry ig = parseIntermediateGeometry(resId);
		BaseRenderer ren = RendererManager.getRenderer();

		for(String s : ig.mObjectNames) {
			Map<String,String> desc = ig.mDescriptors.get(s);
			String name = desc.get("OBJECT_NAME");
			if(desc.get("OBJECT_TYPE").equals("INPUT_AREA")) {
				int shader = ShaderManager.getShaderId(desc.get("SHADER"), "ui_shader");
				int texture = TextureManager.getTextureId(desc.get("TEXTURE"), "uvgrid");
				int numElements = Integer.valueOf(desc.get("NUM_ELEMENTS"));
				int elementStride = Integer.valueOf(desc.get("ELEMENT_STRIDE"));
				int posSize = Integer.valueOf(desc.get("POS_SIZE"));
				int posOffset = Integer.valueOf(desc.get("POS_OFFSET"));
				int nrmSize = Integer.valueOf(desc.get("NRM_SIZE"));
				int nrmOffset = Integer.valueOf(desc.get("NRM_OFFSET"));
				int txcSize = Integer.valueOf(desc.get("TXC_SIZE"));
				int txcOffset = Integer.valueOf(desc.get("TXC_OFFSET"));
				Integer.valueOf(desc.get("TXC_OFFSET"));

				byte[] vboBytes = ig.mFiles.get(s + ".v");
				byte[] iboBytes = ig.mFiles.get(s + ".i");

				int datBufHandle = ren.loadToVbo(vboBytes);
				int idxBufHandle = ren.loadToIbo(iboBytes);
				
				Geometry geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture);

				Polygon2DInputArea polyArea = new Polygon2DInputArea(parseHull(desc.get("HULL"), posSize));
				
				InputUiElement iue = new InputUiElement(name, geo, polyArea);
				
				inputList.add(iue);
			}
		}
		
		return inputList;
	}
	
	private static List<Float[]> parseHull(String hullPointsString, int posSize) {
		float[] hullPoints = SSArrayUtil.parseFloatArray(hullPointsString, ",");
		Float[] hullArray = ArrayUtils.toObject(hullPoints);

//		int posSize = Integer.parseInt(desc.get(DescriptorKeysEnum.POS_SIZE.toString()));

		List<Float[]> hull = new ArrayList<Float[]>(hullArray.length/posSize);
		for(int i = 0; i < hullArray.length; i += posSize) {
			Float[] point = new Float[posSize];
			System.arraycopy(hullArray, i, point, 0, posSize);	
			hull.add(point);
		}
		
		return hull;
	}
	
	public static List<Geometry> loadUiElements(int resId) throws IOException {
		List<Geometry> geoList = new ArrayList<Geometry>();

		IntermediateGeometry ig = parseIntermediateGeometry(resId);
		BaseRenderer ren = RendererManager.getRenderer();

		for(String s : ig.mObjectNames) {
			Map<String,String> desc = ig.mDescriptors.get(s);
			String name = desc.get("OBJECT_NAME");
			if(desc.get("OBJECT_TYPE").equals("UI_ELEMENT")) {
				int shader = ShaderManager.getShaderId(desc.get("SHADER"), "ui_shader");
				int texture = TextureManager.getTextureId(desc.get("TEXTURE"), "uvgrid");
				int numElements = Integer.valueOf(desc.get("NUM_ELEMENTS"));
				int elementStride = Integer.valueOf(desc.get("ELEMENT_STRIDE"));
				int posSize = Integer.valueOf(desc.get("POS_SIZE"));
				int posOffset = Integer.valueOf(desc.get("POS_OFFSET"));
				int nrmSize = Integer.valueOf(desc.get("NRM_SIZE"));
				int nrmOffset = Integer.valueOf(desc.get("NRM_OFFSET"));
				int txcSize = Integer.valueOf(desc.get("TXC_SIZE"));
				int txcOffset = Integer.valueOf(desc.get("TXC_OFFSET"));

				byte[] vboBytes = ig.mFiles.get(s + ".v");
				byte[] iboBytes = ig.mFiles.get(s + ".i");

				int datBufHandle = ren.loadToVbo(vboBytes);
				int idxBufHandle = ren.loadToIbo(iboBytes);

				Geometry geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture);
				geoList.add(geo);
			}
		}
		
		return geoList;
	}

	public static List<Geometry> loadGeometry(int resId) throws IOException {
		List<Geometry> geoList = new ArrayList<Geometry>();

		IntermediateGeometry ig = parseIntermediateGeometry(resId);
		BaseRenderer ren = RendererManager.getRenderer();

		for(String s : ig.mObjectNames) {
			Map<String,String> desc = ig.mDescriptors.get(s);
			String name = desc.get("OBJECT_NAME");
			int shader = ShaderManager.getShaderId(desc.get("SHADER"), "tex_shader");
			int texture = TextureManager.getTextureId(desc.get("TEXTURE"), "uvgrid");
			int numElements = Integer.valueOf(desc.get("NUM_ELEMENTS"));
			int elementStride = Integer.valueOf(desc.get("ELEMENT_STRIDE"));
			int posSize = Integer.valueOf(desc.get("POS_SIZE"));
			int posOffset = Integer.valueOf(desc.get("POS_OFFSET"));
			int nrmSize = Integer.valueOf(desc.get("NRM_SIZE"));
			int nrmOffset = Integer.valueOf(desc.get("NRM_OFFSET"));
			int txcSize = Integer.valueOf(desc.get("TXC_SIZE"));
			int txcOffset = Integer.valueOf(desc.get("TXC_OFFSET"));

			byte[] vboBytes = ig.mFiles.get(s + ".v");
			byte[] iboBytes = ig.mFiles.get(s + ".i");
			
			int datBufHandle = ren.loadToVbo(vboBytes);
			int idxBufHandle = ren.loadToIbo(iboBytes);
			
			Geometry geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
					posSize, nrmSize, txcSize,
					posOffset, nrmOffset, txcOffset,
					numElements, elementStride, texture);
			geoList.add(geo);
		}
		
		return geoList;
	}
	
	private static IntermediateGeometry parseIntermediateGeometry(int resId) throws IOException {
		
		Resources res = GameGlobal.inst().getContext().getResources();
		String resourceName = res.getResourceEntryName(resId);			
		InputStream is = res.openRawResource(resId);
		
		//Parse the zip file
		IntermediateGeometry ig = null;
		try {
			ig = new IntermediateGeometry();
			ig.mFiles = parseFiles(is);
			ig.mObjectNames = parseObjectNames(ig);
			ig.mDescriptors = parseDescriptors(ig);			
		} catch (IOException e) {
			//Add in the resource name
			throw new IOException(String.format("IOException loading resource %s.", resourceName), e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return ig;
	}
	
	private static List<String> parseObjectNames(IntermediateGeometry ig) {
		List<String> objNames = new ArrayList<String>();

		String indexFileName = GameGlobal.inst().getVal(GlobalKeysEnum.INDEX_FILE_NAME);
		String index = new String(ig.mFiles.get(indexFileName));
		String[] files = index.split(GameGlobal.NEWLINE);

		for(String s : files) {			
			if(s.startsWith(GameGlobal.PLUS)) {
				objNames.add(s.substring(1)); //chop off the +
			}
		}

		return objNames;
	}

	private static Map<String, Map<String, String>> parseDescriptors(IntermediateGeometry ig) {
		Map<String,Map<String,String>> descriptors = new HashMap<String, Map<String,String>>();

		String descExt = GameGlobal.inst().getVal(GlobalKeysEnum.DESCRIPTOR_FILE_EXT);

		for(String s : ig.mObjectNames) {						
			String descName = s + descExt;
			Map<String,String> desc = SSPropertyUtil.parseFromString(new String(ig.mFiles.get(descName)));
			descriptors.put(s, desc);
		}

		return descriptors;
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
	
	private static class IntermediateGeometry {
		public List<String> mObjectNames;
		public Map<String,byte[]> mFiles;
		public Map<String,Map<String,String>> mDescriptors;
	}
}
