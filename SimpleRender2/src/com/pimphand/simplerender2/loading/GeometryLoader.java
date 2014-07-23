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
import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;

import com.pimphand.simplerender2.commands.CommandEnum;
import com.pimphand.simplerender2.game.GameGlobal;
import com.pimphand.simplerender2.game.GlobalKeysEnum;
import com.pimphand.simplerender2.input.CircleInputArea;
import com.pimphand.simplerender2.input.InputArea;
import com.pimphand.simplerender2.input.InputShapeEnum;
import com.pimphand.simplerender2.input.InputUiElement;
import com.pimphand.simplerender2.input.Polygon2DInputArea;
import com.pimphand.simplerender2.rendering.BaseRenderer;
import com.pimphand.simplerender2.rendering.Geometry;
import com.pimphand.simplerender2.rendering.RendererManager;
import com.pimphand.simplerender2.rendering.shaders.ShaderManager;
import com.pimphand.simplerender2.rendering.textures.TextureManager;
import com.pimphand.simplerender2.scene.GameEntity;
import com.pimphand.simplerender2.scene.GameObjectLibrary;
import com.pimphand.simplerender2.scene.Light;
import com.pimphand.simplerender2.scene.UiElement;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;

public class GeometryLoader {

	private static final String TAG = GeometryLoader.class.getSimpleName();
	private static final String NEWLINE = "\r\n";
	private static final String PLUS = "+";
	private static final String COMMA = ",";
	private static final String ARRAY_RESOURCE_TYPE = "array";
	private static final String RESOURCE_PACKAGE = "com.pimphand.simplerender2";
	
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
			for(String name : ig.mObjectNames) {
				ObjectTypeEnum objectType = ObjectTypeEnum.valueOf(ig.mDescriptors.get(name).get(DescriptorKeysEnum.OBJECT_TYPE.toString()));
				if(objectType.equals(ObjectTypeEnum.INPUT_AREA)) {
					library.mInputHandlers.add(parseInputUiElement(name, ig));
				} else if(objectType.equals(ObjectTypeEnum.UI_ELEMENT)) {
					library.mDisplayElements.add(parseUiElement(name, ig));
				} else if(objectType.equals(ObjectTypeEnum.GAME_ENTITY)) {
					library.mEntities.add(parseGameEntity(name, ig));
				}
			}
		}
		
		//XXX Hack: Adding a light to get started
		Light light = new Light();
		Matrix.setIdentityM(light.mModelMatrix, 0);
		light.mShaderHandle = ShaderManager.getShaderId("point_shader");
		library.mLights.add(light);

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

		String indexFileName = GameGlobal.inst().getVal(GlobalKeysEnum.INDEX_FILE_NAME);
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
	
	private static GameEntity parseGameEntity(String name, IntermediateGeometry ig) {
		Geometry geo = parseGeometry(name, ig);
		GameEntity entity = new GameEntity(geo);
		
		Context ctx = GameGlobal.inst().getContext();
		Resources res = ctx.getResources();
		String[] inputSettingsArray = res.getStringArray(res.getIdentifier(name, ARRAY_RESOURCE_TYPE, RESOURCE_PACKAGE));

		if(inputSettingsArray != null) {
			Map<String,String> settings = SSPropertyUtil.parseFromStringArray(inputSettingsArray, GameGlobal.SEPARATOR);
			float xPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X.toString()));
			float yPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Y.toString()));
			float zPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Z.toString()));
			float xScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_X.toString()));
			float yScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_Y.toString()));
			float zScale = 1.0f; //TODO: Add a Scale-Z
			
			entity.translate(xPos, yPos, zPos);
			entity.scale(xScale, yScale, zScale);
			
			geo.mShaderHandle = ShaderManager.getShaderId(settings.get(DescriptorKeysEnum.SHADER_NAME.toString()));
			geo.mTextureHandle = TextureManager.getTextureId(settings.get(DescriptorKeysEnum.TEXTURE_NAME.toString()));
		}
		
		return entity;
	}

	private static UiElement parseUiElement(String name, IntermediateGeometry ig) {
		
		Geometry geo = parseGeometry(name, ig);
		UiElement element = new UiElement(geo);

		Context ctx = GameGlobal.inst().getContext();
		Resources res = ctx.getResources();
		String[] inputSettingsArray = res.getStringArray(res.getIdentifier(name, ARRAY_RESOURCE_TYPE, RESOURCE_PACKAGE));

		if(inputSettingsArray != null) {
			Map<String,String> settings = SSPropertyUtil.parseFromStringArray(inputSettingsArray, GameGlobal.SEPARATOR);
			float xPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X.toString()));
			float yPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Y.toString()));
			float zPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Z.toString()));
			float xScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_X.toString()));
			float yScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_Y.toString()));
			float zScale = 1.0f; //TODO: Add a Scale-Z
			
			element.translate(xPos, yPos, zPos);
			element.scale(xScale, yScale, zScale);
			
			geo.mShaderHandle = ShaderManager.getShaderId(settings.get(DescriptorKeysEnum.SHADER_NAME.toString()));
			geo.mTextureHandle = TextureManager.getTextureId(settings.get(DescriptorKeysEnum.TEXTURE_NAME.toString()));
		}

		return element;
	}
	
	private static InputUiElement parseInputUiElement(String name, IntermediateGeometry ig) {

		Geometry geo = parseGeometry(name, ig);
		InputArea area = parseInputArea(name, ig);
		InputUiElement element = new InputUiElement(geo, area);

		Context ctx = GameGlobal.inst().getContext();
		Resources res = ctx.getResources();
		String[] inputSettingsArray = res.getStringArray(res.getIdentifier(name, ARRAY_RESOURCE_TYPE, RESOURCE_PACKAGE));

		if(inputSettingsArray != null) {
			Map<String,String> settings = SSPropertyUtil.parseFromStringArray(inputSettingsArray, GameGlobal.SEPARATOR);
			float xPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_X.toString()));
			float yPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Y.toString()));
			float zPos = Float.valueOf(settings.get(DescriptorKeysEnum.POS_Z.toString()));
			float xScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_X.toString()));
			float yScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_Y.toString()));
			float zScale = 1.0f; //TODO: Add a Scale-Z
			
			element.translate(xPos, yPos, zPos);
			element.scale(xScale, yScale, zScale);
			
			geo.mShaderHandle = ShaderManager.getShaderId(settings.get(DescriptorKeysEnum.SHADER_NAME.toString()));
			geo.mTextureHandle = TextureManager.getTextureId(settings.get(DescriptorKeysEnum.TEXTURE_NAME.toString()));
			
			//TODO: Allow comma-separated command names in config or whatever
			String commands = settings.get(DescriptorKeysEnum.COMMAND.toString());
			if(StringUtils.isNotBlank(commands)) {
				element.registerCommand(CommandEnum.valueOf(commands).getCommand());
			}
			
		}
		
		return element;
	}

	private static InputArea parseInputArea(String name, IntermediateGeometry ig) {
		InputArea area = null;

		Map<String,String> desc = ig.mDescriptors.get(name);

		InputShapeEnum inputShape = InputShapeEnum.valueOf(desc.get(DescriptorKeysEnum.INPUT_SHAPE.toString()));

		if(inputShape.equals(InputShapeEnum.CIRCLE)) {
			area = parseCircleArea(name, ig);
		} else if(inputShape.equals(InputShapeEnum.POLYGON)) {
			area = parsePolygonArea(name, ig);
		}

		return area;
	}
	
	private static Geometry parseGeometry(String name, IntermediateGeometry ig) {		

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
		
		//XXX Hack: push the model back a bit on loading
		Matrix.translateM(geo.mModelMatrix, 0, 0.0f, 0.0f, -5.0f);

		return geo;

	}

	private static CircleInputArea parseCircleArea(String name, IntermediateGeometry ig) {

		Map<String,String> desc = ig.mDescriptors.get(name);

		float[] center = SSArrayUtil.parseFloatArray(desc.get(DescriptorKeysEnum.INPUT_CENTER.toString()), COMMA);
		float radius = Float.parseFloat(desc.get(DescriptorKeysEnum.INPUT_RADIUS.toString()));

		CircleInputArea circleArea = new CircleInputArea(new PointF(center[0], center[1]), radius);

		return circleArea;
	}

	private static Polygon2DInputArea parsePolygonArea(String name,IntermediateGeometry ig) {

		Map<String,String> desc = ig.mDescriptors.get(name);

		float[] hullPoints = SSArrayUtil.parseFloatArray(desc.get(DescriptorKeysEnum.INPUT_HULL.toString()), COMMA);
		Float[] hullArray = ArrayUtils.toObject(hullPoints);

		int posSize = Integer.parseInt(desc.get(DescriptorKeysEnum.POS_SIZE.toString()));

		List<Float[]> hull = new ArrayList<Float[]>(hullArray.length/posSize);
		for(int i = 0; i < hullArray.length; i += posSize) {
			Float[] point = new Float[posSize];
			System.arraycopy(hullArray, i, point, 0, posSize);	
			hull.add(point);
		}

		Polygon2DInputArea polyArea = new Polygon2DInputArea(hull);
		return polyArea;
	}

	private static class IntermediateGeometry {
		public List<String> mObjectNames;
		public Map<String,byte[]> mFiles;
		public Map<String,Map<String,String>> mDescriptors;
	}
}
