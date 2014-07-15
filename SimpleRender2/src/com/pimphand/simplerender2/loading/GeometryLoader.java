package com.pimphand.simplerender2.loading;

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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.Log;

import com.pimphand.simplerender2.game.GameGlobal;
import com.pimphand.simplerender2.game.GlobalKeysEnum;
import com.pimphand.simplerender2.rendering.BaseRenderer;
import com.pimphand.simplerender2.rendering.GameObjectLibrary;
import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.rendering.PositionTypeEnum;
import com.pimphand.simplerender2.rendering.RendererManager;
import com.pimphand.simplerender2.scene.GameEntity;
import com.pimphand.simplerender2.ui.CircleInputArea;
import com.pimphand.simplerender2.ui.InputArea;
import com.pimphand.simplerender2.ui.InputShapeEnum;
import com.pimphand.simplerender2.ui.Polygon2DInputArea;
import com.pimphand.simplerender2.ui.InputUiElement;
import com.pimphand.simplerender2.ui.UiElement;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;

public class GeometryLoader {
	
	private static final String TAG = GeometryLoader.class.getSimpleName();
	private static final String NEWLINE = "\r\n";
	private static final String PLUS = "+";
	private static final String COMMA = ",";

	public static GameObjectLibrary loadGameObjects(Context context, TypedArray modelArray) {
		GameObjectLibrary library = new GameObjectLibrary();
		
		Resources res =  context.getResources();
		
		for(int i = 0; i < modelArray.length(); i++) {			
			
			//Get the data from resources
			int resourceId = modelArray.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);			
			InputStream is = res.openRawResource(resourceId);
			
			Log.d(TAG, String.format("Loading resource: %s.", resourceName));
			
			//Parse the zip file
			IntermediateGeometry ig = null;
			try {
				ig = parseIntermediateGeometry(is);				
			} catch (IOException e) {
				Log.e(TAG, String.format("Error loading resource %s.", resourceName), e);
			} finally {
				IOUtils.closeQuietly(is);
			}
			
			//Parse the game objects according to type
			for(String s : ig.mObjectNames) {
				DescriptorKeysEnum objectType = DescriptorKeysEnum.valueOf(ig.mDescriptors.get(s).get(DescriptorKeysEnum.OBJECT_TYPE)); 
				if(objectType.equals(ObjectTypeEnum.INPUT_AREA)) {
					library.mInputElements.add(parseInputUiElement(s, ig));
				} else if(objectType.equals(ObjectTypeEnum.UI_ELEMENT)) {
					library.mDisplayElements.add(parseUiElement(s, ig));
				} else if(objectType.equals(ObjectTypeEnum.GAME_ENTITY)) {
					library.mEntities.add(parseEntity(s, ig));
				}
			}
		}
		
		return library;
		
	}

	private static IntermediateGeometry parseIntermediateGeometry(InputStream is) throws IOException {
		IntermediateGeometry ig = new IntermediateGeometry();
		
		ig.mFiles = parseFiles(is);
		
		ig.mObjectNames = parseObjectNames(ig);
		
		ig.mDescriptors = parseDescriptors(ig);
		
		return ig;
	}

	private static List<String> parseObjectNames(IntermediateGeometry ig) {
		List<String> objNames = new ArrayList<String>();
		
		String indexFileName = GameGlobal.instance().getVal(GlobalKeysEnum.INDEX_FILE_NAME);
		String index = new String(ig.mFiles.get(indexFileName));
		String[] files = index.split(NEWLINE);
		
		for(String s : files) {			
			if(s.startsWith(PLUS)) {
				objNames.add(s.substring(1)); //chop off the +
			}
		}
		
		return objNames;
	}

	private static Map<String, Map<String, String>> parseDescriptors(IntermediateGeometry ig) {
		Map<String,Map<String,String>> descriptors = new HashMap<String, Map<String,String>>();
		
		String descExt = GameGlobal.instance().getVal(GlobalKeysEnum.DESCRIPTOR_FILE_EXT);
		
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
	
	private static InputUiElement parseInputUiElement(String name, IntermediateGeometry ig) {
		Geometry geo = parseGeometry(name, ig);
		InputArea area = parseInputArea(name, ig);
		InputUiElement element = new InputUiElement(geo, area);

		String meshSuffix = GameGlobal.instance().getVal(GlobalKeysEnum.MESH_SUFFIX);
		if(name.endsWith(meshSuffix)) {
			name = name.substring(0, name.indexOf(meshSuffix));
		}
		
		Context ctx = GameGlobal.instance().getContext();
		Resources res = ctx.getResources();
		//TODO: fix these hard-coded strings assuming they work
		String[] inputSettingsArray = res.getStringArray(res.getIdentifier(name, "array", "com.pimphand.simplerender2"));
		
		if(inputSettingsArray != null) {
			Map<String,String> settings = SSPropertyUtil.parseFromStringArray(inputSettingsArray, GameGlobal.SEPARATOR);
			float xPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X));
			float yPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Y));
			PositionTypeEnum posType = PositionTypeEnum.fromSuffix(settings.get(DescriptorKeysEnum.POS_TYPE));
			float scaleX = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X));
			float scaleY = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X));
			PointF position = new PointF(xPos,yPos);
			PointF scale = new PointF(scaleX,scaleY);
			element.setPosition(position, posType);
			element.setScale(scale);
		}
		
		return element;
	}
	
	private static GameEntity parseEntity(String name, IntermediateGeometry ig) {
		Geometry geo = parseGeometry(name, ig);
		GameEntity entity = new GameEntity(geo);
		return entity;
	}

	private static Geometry parseGeometry(String name, IntermediateGeometry ig) {		
		
		Geometry geo = new Geometry();
		geo.mName = name;
		String assetFileName = GameGlobal.instance().getVal(GlobalKeysEnum.ASSET_FILE_NAME);
		geo.mAssetXml = new String(ig.mFiles.get(assetFileName));
		
		
		StringBuilder vboFile = new StringBuilder(name);
		vboFile.append(GameGlobal.instance().getVal(GlobalKeysEnum.VBO_FILE_EXT));
		
		StringBuilder iboFile = new StringBuilder(name);
		iboFile.append(GameGlobal.instance().getVal(GlobalKeysEnum.IBO_FILE_EXT));
		
		BaseRenderer rend = RendererManager.instance().getRenderer();
		
		geo.mDatBufIndex = rend.loadToVbo(ig.mFiles.get(vboFile));
		geo.mIdxBufIndex = rend.loadToIbo(ig.mFiles.get(iboFile));

		Map<String,String> desc = ig.mDescriptors.get(name);
		
		geo.mPosSize = Integer.valueOf(desc.get(DescriptorKeysEnum.POS_SIZE));
		geo.mPosOffset = Integer.valueOf(desc.get(DescriptorKeysEnum.POS_OFFSET));

		geo.mNrmSize = Integer.valueOf(desc.get(DescriptorKeysEnum.NRM_SIZE));
		geo.mNrmOffset = Integer.valueOf(desc.get(DescriptorKeysEnum.NRM_OFFSET));

		geo.mTxcSize = Integer.valueOf(desc.get(DescriptorKeysEnum.TXC_SIZE));
		geo.mTxcOffset = Integer.valueOf(desc.get(DescriptorKeysEnum.TXC_OFFSET));

		geo.mNumElements = Integer.valueOf(desc.get(DescriptorKeysEnum.NUM_ELEMENTS));
		geo.mElementStride = Integer.valueOf(desc.get(DescriptorKeysEnum.ELEMENT_STRIDE));

		return geo;
				
	}
	
	private static UiElement parseUiElement(String name, IntermediateGeometry ig) {
		Geometry geometry = parseGeometry(name, ig);
		UiElement element = new UiElement(geometry);
		
		Context ctx = GameGlobal.instance().getContext();
		Resources res = ctx.getResources();
		//TODO: fix these hard-coded strings assuming they work
		String[] inputSettingsArray = res.getStringArray(res.getIdentifier(name, "array", "com.pimphand.simplerender2"));
		
		if(inputSettingsArray != null) {
			Map<String,String> settings = SSPropertyUtil.parseFromStringArray(inputSettingsArray, GameGlobal.SEPARATOR);
			float xPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X));
			float yPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Y));
			PositionTypeEnum posType = PositionTypeEnum.fromSuffix(settings.get(DescriptorKeysEnum.POS_TYPE));
			float scaleX = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X));
			float scaleY = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X));
			PointF position = new PointF(xPos,yPos);
			PointF scale = new PointF(scaleX,scaleY);
			element.setPosition(position, posType);
			element.setScale(scale);
		}
		
		return element;
	}
	
	private static InputArea parseInputArea(String name, IntermediateGeometry ig) {
		InputArea area = null;
		
		Map<String,String> desc = ig.mDescriptors.get(name);
		
		InputShapeEnum inputShape = InputShapeEnum.valueOf(desc.get(DescriptorKeysEnum.INPUT_SHAPE));
		
		if(inputShape.equals(InputShapeEnum.CIRCLE)) {
			area = parseCircleArea(name, ig);
		} else if(inputShape.equals(InputShapeEnum.POLYGON)) {
			area = parsePolygonArea(name, ig);
		}
		
		return area;
	}
	
	private static CircleInputArea parseCircleArea(String name, IntermediateGeometry ig) {

		Map<String,String> desc = ig.mDescriptors.get(name);
		
		float[] center = SSArrayUtil.parseFloatArray(desc.get(DescriptorKeysEnum.INPUT_CENTER), COMMA);
		float radius = Float.parseFloat(desc.get(DescriptorKeysEnum.INPUT_RADIUS));

		CircleInputArea circleArea = new CircleInputArea(new PointF(center[0], center[1]), radius);
		
		return circleArea;
	}

	private static Polygon2DInputArea parsePolygonArea(String name,IntermediateGeometry ig) {
		
		Map<String,String> desc = ig.mDescriptors.get(name);
		
		float[] hullPoints = SSArrayUtil.parseFloatArray(desc.get(DescriptorKeysEnum.INPUT_CENTER), COMMA);
		Float[] hullArray = ArrayUtils.toObject(hullPoints);
		
		int posSize = Integer.parseInt(desc.get(DescriptorKeysEnum.POS_SIZE));

		List<Float[]> hull = new ArrayList<Float[]>(hullArray.length/posSize);
		for(int i = 0; i < hullArray.length; i += posSize) {
			Float[] point = new Float[posSize];
			System.arraycopy(hullArray, i, point, 0, posSize);	
			hull.add(point);
		}

		Polygon2DInputArea polyArea = new Polygon2DInputArea(hull);
		return polyArea;
	}

	private void parseSomething() {
		
		//TODO: Finish object initialization by
		//bringing in other descriptors
		
//		geo.mXSize = Float.valueOf(properties.get("x_size"));
//		geo.mYSize = Float.valueOf(properties.get("y_size"));
//		geo.mZSize = Float.valueOf(properties.get("z_size"));
//
//		if(StringUtils.isNotBlank(properties.get("priority"))) {
//			geo.mPriority = Integer.valueOf(properties.get("priority"));
//		}
//
//		if(StringUtils.isNotBlank(properties.get("h_align"))) {
//			geo.mHAlign = HAlignType.valueOf(properties.get("h_align").toUpperCase());
//		}
//
//		if(StringUtils.isNotBlank(properties.get("v_align"))) {
//			geo.mVAlign = VAlignType.valueOf(properties.get("v_align").toUpperCase());
//		}
//
//		if(StringUtils.isBlank(properties.get("texture_name"))) {
//			geo.mTextureHandle = 1;
//		} else {
//			geo.mTextureHandle = renderer.getTexture(properties.get("texture_name"));
//		}
//
//		if(StringUtils.isBlank(properties.get("shader_name"))) {
//			geo.mShaderHandle = 1;
//		} else {
//			Log.d(TAG, String.format("Attempting to load shader %s", properties.get("shader_name")));
//			geo.mShaderHandle = renderer.getShader(properties.get("shader_name"));
//		}
//
//		if(properties.get("obj_type").equals("input_area")) {
//			if(properties.get("input_shape").equals("circle")) {
//
//				float[] center = SSArrayUtil.parseFloatArray(properties.get("input_center"), ",");
//				float radius = Float.parseFloat(properties.get("input_radius"));
//
//				geo.mInputArea = new CircleInputArea(new PointF(center[0], center[1]), radius);
//
//			} else if(properties.get("input_shape").equals("polygon2d")) {
//
//				Float[] hullArray = ArrayUtils.toObject(SSArrayUtil.parseFloatArray(properties.get("input_hull"), ","));
//
//				List<Float[]> hull = new ArrayList<Float[]>(hullArray.length/geo.mPosSize);
//				for(int i = 0; i < hullArray.length; i += geo.mPosSize) {
//					Float[] point = new Float[geo.mPosSize];
//					System.arraycopy(hullArray, i, point, 0, geo.mPosSize);	
//					hull.add(point);
//				}
//
//				Log.d(TAG, String.format("HULL SIZE: %s", hull.size()));
//
//				geo.mInputArea = new Polygon2DInputArea(hull);
//			}
//		}
	}
	
	private static class IntermediateGeometry {
		public List<String> mObjectNames;
		public Map<String,byte[]> mFiles;
		public Map<String,Map<String,String>> mDescriptors;
	}
}
