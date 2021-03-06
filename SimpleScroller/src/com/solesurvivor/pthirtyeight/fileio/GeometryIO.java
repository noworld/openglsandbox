package com.solesurvivor.pthirtyeight.fileio;

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
import org.apache.commons.lang.StringUtils;

import android.content.res.Resources;

import com.solesurvivor.pthirtyeight.commands.Command;
import com.solesurvivor.pthirtyeight.commands.CommandEnum;
import com.solesurvivor.pthirtyeight.game.GameGlobal;
import com.solesurvivor.pthirtyeight.game.GlobalKeysEnum;
import com.solesurvivor.pthirtyeight.input.InputUiElement;
import com.solesurvivor.pthirtyeight.input.Polygon2DInputArea;
import com.solesurvivor.pthirtyeight.rendering.BaseRenderer;
import com.solesurvivor.pthirtyeight.rendering.RendererManager;
import com.solesurvivor.pthirtyeight.rendering.ShaderManager;
import com.solesurvivor.pthirtyeight.rendering.TextureManager;
import com.solesurvivor.pthirtyeight.scene.Geometry;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.math.QuickHull;

public class GeometryIO {
	
	protected static final boolean DEBUG = true;
	
	@SuppressWarnings("unused")
	private static final String TAG = GeometryIO.class.getSimpleName();
	
	public static List<InputUiElement> loadInputUiElements(int resId) throws IOException {
		List<InputUiElement> inputList = new ArrayList<InputUiElement>();

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
				
				if(StringUtils.isNotBlank(desc.get("COMMAND"))) {
					Command command = CommandEnum.valueOf(desc.get("COMMAND")).getCommand();
					iue.registerCommand(command);
				}
				
				inputList.add(iue);
			}
		}
		
		return inputList;
	}
	
	private static List<Float[]> parseHull(String hullPointsString, int posSize) {
		float[] hullPoints = SSArrayUtil.parseFloatArray(hullPointsString, ",");
		Float[] hullArray = ArrayUtils.toObject(hullPoints);

		List<Float[]> points = new ArrayList<Float[]>(hullArray.length/posSize);
		for(int i = 0; i < hullArray.length; i += posSize) {
			Float[] point = new Float[posSize];
			System.arraycopy(hullArray, i, point, 0, posSize);	
			points.add(point);
		}
		
		//XXX TODO: Pre-compute the hull when exporting
		List<Float[]> hull = QuickHull.QuickHull2d(points);
		
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

			Geometry geo = null;
			
			int idxBufHandle = ren.loadToIbo(iboBytes);
			int datBufHandle = ren.loadToVbo(vboBytes);

			geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
					posSize, nrmSize, txcSize,
					posOffset, nrmOffset, txcOffset,
					numElements, elementStride, texture);

			geoList.add(geo);
		}

		return geoList;
	}
	
	public static Map<String,Geometry> loadGeometryMap(int resId) throws IOException {
		Map<String,Geometry> geoList = new HashMap<String,Geometry>();

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

			Geometry geo = null;
			
			int idxBufHandle = ren.loadToIbo(iboBytes);
			int datBufHandle = ren.loadToVbo(vboBytes);
			
			geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
					posSize, nrmSize, txcSize,
					posOffset, nrmOffset, txcOffset,
					numElements, elementStride, texture);


			geoList.put(name,geo);
		}
		
		return geoList;
	}

	private static IntermediateGeometry parseIntermediateGeometry(int resId) throws IOException {
		
		Resources res = RendererManager.getContext().getResources();
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

		String indexFileName = GameGlobal.getVal(GlobalKeysEnum.INDEX_FILE_NAME);
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

		String descExt = GameGlobal.getVal(GlobalKeysEnum.DESCRIPTOR_FILE_EXT);

		for(String s : ig.mObjectNames) {						
			String descName = s + descExt;
			byte[] file = ig.mFiles.get(descName);
			if(file != null) {
				Map<String,String> desc = SSPropertyUtil.parseFromString(new String(file));
				descriptors.put(s, desc);
			}
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
