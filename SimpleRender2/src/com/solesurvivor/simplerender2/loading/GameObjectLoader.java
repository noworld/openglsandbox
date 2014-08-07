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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;

import com.pimphand.simplerender2.R;
import com.solesurvivor.simplerender2.commands.CommandEnum;
import com.solesurvivor.simplerender2.game.GameGlobal;
import com.solesurvivor.simplerender2.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2.input.CircleInputArea;
import com.solesurvivor.simplerender2.input.InputArea;
import com.solesurvivor.simplerender2.input.InputShapeEnum;
import com.solesurvivor.simplerender2.input.Polygon2DInputArea;
import com.solesurvivor.simplerender2.rendering.BaseRenderer;
import com.solesurvivor.simplerender2.rendering.Geometry;
import com.solesurvivor.simplerender2.rendering.RendererManager;
import com.solesurvivor.simplerender2.rendering.shaders.ShaderManager;
import com.solesurvivor.simplerender2.rendering.textures.TextureManager;
import com.solesurvivor.simplerender2.rendering.water.Wave;
import com.solesurvivor.simplerender2.rendering.water.WaveDescriptorEnum;
import com.solesurvivor.simplerender2.scene.GameEntity;
import com.solesurvivor.simplerender2.scene.GameObjectLibrary;
import com.solesurvivor.simplerender2.scene.InputUiElement;
import com.solesurvivor.simplerender2.scene.Light;
import com.solesurvivor.simplerender2.scene.UiElement;
import com.solesurvivor.simplerender2.scene.Water;
import com.solesurvivor.simplerender2.text.Cursor;
import com.solesurvivor.simplerender2.text.Font;
import com.solesurvivor.simplerender2.text.FontManager;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class GameObjectLoader {

	private static final String TAG = GameObjectLoader.class.getSimpleName();
	private static final String NEWLINE = "\r\n";
	private static final String PLUS = "+";
	private static final String COMMA = ",";
	private static final String ARRAY_RESOURCE_TYPE = "array";
	private static final String RESOURCE_PACKAGE = "com.pimphand.simplerender2";
	
	private static Map<String, Geometry> mGeometries = null;
	private static GameObjectLibrary mLibrary = null;
	
	public static GameObjectLibrary loadGameObjects(Context context, TypedArray modelArray) {
		mLibrary = new GameObjectLibrary();
		mGeometries = new HashMap<String, Geometry>();

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
					mLibrary.mInputHandlers.add(parseInputUiElement(name, ig));
				} else if(objectType.equals(ObjectTypeEnum.UI_ELEMENT)) {
					mLibrary.mDisplayElements.add(parseUiElement(name, ig));
				} else if(objectType.equals(ObjectTypeEnum.GAME_ENTITY)) {
					mLibrary.mEntities.add(parseGameEntity(name, ig));
				}
			}
		}
		
		//XXX Hack: Adding a light to get started
		Light light = new Light();
		Matrix.setIdentityM(light.mModelMatrix, 0);
		light.mShaderHandle = ShaderManager.getShaderId("point_shader");
		mLibrary.mLights.add(light);
		
		mLibrary.mWaters.addAll(loadWater());

		return mLibrary;

	}

	private static List<Water> loadWater() {
		List<Water> waters = new ArrayList<Water>();
		
		Resources res = GameGlobal.inst().getContext().getResources();
		
		TypedArray waterArray = res.obtainTypedArray(R.array.water);
		
		for(int i = 0; i < waterArray.length(); i++) {
			int resourceId = waterArray.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);	
			Log.d(TAG,String.format("Loading water: %s",resourceName));
			String[] propArray = res.getStringArray(resourceId);
			//TODO: make the key a DescriptorKeyEnum using SSPropertyUtil.descriptorFromStringArray(...)
			Map<String,String> properties = SSPropertyUtil.parseFromStringArray(propArray, GameGlobal.SEPARATOR);
			String modelName = properties.get(DescriptorKeysEnum.MODEL.toString());
			Geometry geo = mGeometries.get(modelName);
			if(geo != null) { //If the geometry for this water exists.

				String waveArrayName = properties.get(DescriptorKeysEnum.WAVES.toString());
				int waveId = res.getIdentifier(waveArrayName, ARRAY_RESOURCE_TYPE, RESOURCE_PACKAGE);

				TypedArray waveArray = res.obtainTypedArray(waveId);
				List<Wave> waves = loadWaves(waveArray);
				waveArray.recycle();

				String texName = properties.get(DescriptorKeysEnum.TEXTURE_NAME.toString());
				String shadName = properties.get(DescriptorKeysEnum.SHADER_NAME.toString());

				int texHandle = TextureManager.getTextureId(texName);
				int shadHandle = ShaderManager.getShaderId(shadName);

				Water w = new Water(waves, geo, texHandle, shadHandle);

				String transparency = properties.get(DescriptorKeysEnum.TRANSPARENCY.toString());
				w.setTransparency(Float.valueOf(transparency));

				waters.add(w);				
			}
		}
		
		waterArray.recycle();
		
		//Remove water objects from game entities
		List<GameEntity> waterEntities = new ArrayList<GameEntity>();
		for(Water w : waters) {
			for(GameEntity ge : mLibrary.mEntities) {
				if(ge.getGeometry().mName.equals(w.getGeometry().mName)) {
					waterEntities.add(ge);
				}
			}
		}
		mLibrary.mEntities.removeAll(waterEntities);
		
		return waters;
	}

	private static List<Wave> loadWaves(TypedArray waveArray) {
		List<Wave> waves = new ArrayList<Wave>(waveArray.length());
		
		Resources res = GameGlobal.inst().getContext().getResources();
		
		for(int i = 0; i < waveArray.length(); i++) {
			int resourceId = waveArray.getResourceId(i, 0);
			String resourceName = res.getResourceEntryName(resourceId);	
			Log.d(TAG,String.format("Loading wave: %s",resourceName));
			
			String[] propArray = res.getStringArray(resourceId);
			Map<String,String> properties = SSPropertyUtil.parseFromStringArray(propArray, GameGlobal.SEPARATOR);
			
			float amp = Float.valueOf(properties.get(WaveDescriptorEnum.AMPLITUDE.toString()));
		    Vec3 dir = 	Vec3.valueOf(properties.get(WaveDescriptorEnum.DIRECTION.toString()));
		    float len = Float.valueOf(properties.get(WaveDescriptorEnum.WAVELENGTH.toString()));
		    float spd = Float.valueOf(properties.get(WaveDescriptorEnum.SPEED.toString()));
		    float tsc = Float.valueOf(properties.get(WaveDescriptorEnum.TIME_SCALE.toString()));
		    float phs = Float.valueOf(properties.get(WaveDescriptorEnum.PHASE_SHIFT.toString()));
		    
		    Wave w = new Wave(amp, dir, len, spd);
		    w.setTimeScale(tsc);
		    w.setPhaseShift(phs);
		    waves.add(w);
		}
		
		return waves;
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
		mGeometries.put(geo.mName, geo);
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
			float zScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_Z.toString()));
			
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
			float zScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_Z.toString()));
			
			element.setCursor(loadCursor(settings));
			
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
		InputUiElement element = new InputUiElement(name, geo, area);

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
			float zScale = Float.valueOf(settings.get(DescriptorKeysEnum.SCALE_Z.toString()));
			
			element.setCursor(loadCursor(settings));
			
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
		
		
		//XXX DEBUGGING
//		if(geo.mName.equals("plane")) { 
//			float[] vbo = SSArrayUtil.byteToFloatArray(ig.mFiles.get(vboFile.toString()));
//			short[] ibo = SSArrayUtil.byteToShortArray(ig.mFiles.get(iboFile.toString()));
//			SSLog.d(TAG, " -*-*-*- LOGGING BLENDER WATER -*-*-*- ");
//			for(int i = 0; i < ibo.length-3; i += 3) {
//				short idx0 = ibo[i];
//				short idx1 = ibo[i+1];
//				short idx2 = ibo[i+2];
//				Vec3 v0 = new Vec3(vbo[idx0], vbo[idx0+1], vbo[idx0+2]);
//				Vec3 v1 = new Vec3(vbo[idx1], vbo[idx1+1], vbo[idx1+2]);
//				Vec3 v2 = new Vec3(vbo[idx2], vbo[idx2+1], vbo[idx2+2]);
//				SSLog.d(TAG, "TRIANGLE %s: %s->%s->%s ", String.format("(%s,%s,%s)", idx0,idx1,idx2), v0.prettyString(), v1.prettyString(), v2.prettyString());
//			}
//		}
		
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
	
	private static Cursor loadCursor(Map<String, String> settings) {
		Cursor cursor = null;
		if(StringUtils.isNotBlank(settings.get(DescriptorKeysEnum.TEXT_FONT.toString()))) {
			Font font = FontManager.getFont(settings.get(DescriptorKeysEnum.TEXT_FONT.toString()));
			
			float textXPos = Float.valueOf(settings.get(DescriptorKeysEnum.TEXT_POS_X.toString()));
			float textYPos = Float.valueOf(settings.get(DescriptorKeysEnum.TEXT_POS_Y.toString()));
			float textZPos = Float.valueOf(settings.get(DescriptorKeysEnum.TEXT_POS_Z.toString()));
			float[] textPos = {textXPos, textYPos, textZPos, 1.0f};
			
			float textXscale = Float.valueOf(settings.get(DescriptorKeysEnum.TEXT_SCALE_X.toString()));
			float textYscale = Float.valueOf(settings.get(DescriptorKeysEnum.TEXT_SCALE_Y.toString()));
			float[] textScale = {textXscale, textYscale, 1.0f};
					
			String text = settings.get(DescriptorKeysEnum.TEXT.toString());
			
			cursor = new Cursor(font, textScale, textPos, 0.0f, text);

			String lineLenVal = settings.get(DescriptorKeysEnum.TEXT_LINE_LENGTH.toString());
			if(StringUtils.isNotBlank(lineLenVal)) {				
				cursor.setLineLength(Float.valueOf(lineLenVal));
			}

		}
		
		return cursor;
	}

	private static class IntermediateGeometry {
		public List<String> mObjectNames;
		public Map<String,byte[]> mFiles;
		public Map<String,Map<String,String>> mDescriptors;
	}
}
