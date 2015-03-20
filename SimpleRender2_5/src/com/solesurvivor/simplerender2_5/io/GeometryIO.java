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
import org.apache.commons.lang.StringUtils;

import android.content.res.Resources;

import com.solesurvivor.simplerender2_5.commands.Command;
import com.solesurvivor.simplerender2_5.commands.CommandEnum;
import com.solesurvivor.simplerender2_5.game.GameGlobal;
import com.solesurvivor.simplerender2_5.game.GlobalKeysEnum;
import com.solesurvivor.simplerender2_5.input.InputUiElement;
import com.solesurvivor.simplerender2_5.input.Polygon2DInputArea;
import com.solesurvivor.simplerender2_5.rendering.BaseRenderer;
import com.solesurvivor.simplerender2_5.rendering.RendererManager;
import com.solesurvivor.simplerender2_5.rendering.ShaderManager;
import com.solesurvivor.simplerender2_5.rendering.TextureManager;
import com.solesurvivor.simplerender2_5.scene.Geometry;
import com.solesurvivor.simplerender2_5.scene.GeometryBones;
import com.solesurvivor.simplerender2_5.scene.TerrainClipmap;
import com.solesurvivor.simplerender2_5.scene.animation.Armature;
import com.solesurvivor.simplerender2_5.scene.animation.Bone;
import com.solesurvivor.simplerender2_5.scene.animation.Pose;
import com.solesurvivor.simplerender2_5.scene.animation.PoseLibrary;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.SSPropertyUtil;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.QuickHull;
import com.solesurvivor.util.math.Vec3;

public class GeometryIO {
	
	protected static final boolean DEBUG = false;
	
	
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
				//int texture = TextureManager.getTextureId(desc.get("TEXTURE"), "orient");
				int texture = TextureManager.getTextureId(desc.get("TEXTURE"), "water");
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
			boolean bones = Boolean.valueOf(desc.get("BONES"));

			byte[] vboBytes = ig.mFiles.get(s + ".v");
			byte[] iboBytes = ig.mFiles.get(s + ".i");

			Geometry geo = null;
			
			int idxBufHandle = ren.loadToIbo(iboBytes);
			int datBufHandle = ren.loadToVbo(vboBytes);
			
			if(bones) {
				int bCountOffset = Integer.valueOf(desc.get("B_CT_OFFSET"));
				int bCountSize = Integer.valueOf(desc.get("B_CT_SIZE"));
				int bIndexOffset = Integer.valueOf(desc.get("B_ID_OFFSET"));
				int bIndexSize = Integer.valueOf(desc.get("B_ID_SIZE"));
				int bWeightOffset = Integer.valueOf(desc.get("B_WT_OFFSET"));
				int bWeightSize = Integer.valueOf(desc.get("B_WT_SIZE"));
				
				geo = new GeometryBones(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture, bones,
						bCountSize, bCountOffset,
						bIndexSize, bIndexOffset,
						bWeightSize, bWeightOffset);
			} else {
				geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture);
			}

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
			boolean bones = Boolean.valueOf(desc.get("BONES"));

			byte[] vboBytes = ig.mFiles.get(s + ".v");
			byte[] iboBytes = ig.mFiles.get(s + ".i");

			Geometry geo = null;
			
			int idxBufHandle = ren.loadToIbo(iboBytes);
			int datBufHandle = ren.loadToVbo(vboBytes);
			
			if(bones) {
				int bCountOffset = Integer.valueOf(desc.get("B_CT_OFFSET"));
				int bCountSize = Integer.valueOf(desc.get("B_CT_SIZE"));
				int bIndexOffset = Integer.valueOf(desc.get("B_ID_OFFSET"));
				int bIndexSize = Integer.valueOf(desc.get("B_ID_SIZE"));
				int bWeightOffset = Integer.valueOf(desc.get("B_WT_OFFSET"));
				int bWeightSize = Integer.valueOf(desc.get("B_WT_SIZE"));
				
				geo = new GeometryBones(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture, bones,
						bCountSize, bCountOffset,
						bIndexSize, bIndexOffset,
						bWeightSize, bWeightOffset);
			} else {
				geo = new Geometry(name, shader, datBufHandle, idxBufHandle,
						posSize, nrmSize, txcSize,
						posOffset, nrmOffset, txcOffset,
						numElements, elementStride, texture);
			}
			
			geoList.put(name,geo);
		}
		
		return geoList;
	}
	
	public static Map<String,PoseLibrary> lodePoseLibrary(int resId) throws IOException {
		
		Map<String,PoseLibrary> poseLibs = new HashMap<String,PoseLibrary>();
		Map<String,IntermediateArmature> interArms = parseIntermediateArmature(resId); 
		
		for(String key : interArms.keySet()) {
			IntermediateArmature interArm = interArms.get(key);
			Map<String,Pose> poses = new HashMap<String,Pose>();
			
			for(String poseKey : interArm.boneData.keySet()) {
				String data = interArm.boneData.get(poseKey);
				if(data != null) {
					float[] poseMatrix = SSArrayUtil.parseFloatArray(data, ",");
					Pose p = new Pose(poseKey, poseMatrix);
					poses.put(poseKey, p);
				}
			}
			
			Pose restPose = poses.get("Rest");
			poses.remove("Rest");
			Pose restPoseInv = poses.get("RestInv");
			poses.remove("RestInv");
			
			if(DEBUG) {
				SSLog.d(TAG, "\nBONE INV BIND MATRIX: %s", "REST POSE");
				for(int i = 0; i < restPose.getBones().length; i++) {
					if(i % 16 == 0) {
						SSLog.d(TAG, "----------------------------\n");
					}
					SSLog.d(TAG, "MATRIX INDEX %s: %s", i, restPose.getBones()[i]);					
				}
				SSLog.d(TAG, "----------------------------\n");
			}
			
			PoseLibrary poseLib = new PoseLibrary(interArm.name, restPose, restPoseInv);
			for(String poseKey : poses.keySet()) {
				Pose p = poses.get(poseKey);
				poseLib.addPose(poseKey, p);
			}
			poseLibs.put(key, poseLib);
		}
		
		return poseLibs;
	}
	
	public static Map<String,Armature> loadArmatureMap(int resId) throws IOException {
		Map<String,Armature> armList = new HashMap<String,Armature>();

		Map<String,IntermediateArmature> interArms = parseIntermediateArmature(resId);
		
		for(String key : interArms.keySet()) {
			IntermediateArmature interArm = interArms.get(key);
			
			Map<String,Bone> bones = new HashMap<String,Bone>();
			
			for(String boneKey : interArm.boneData.keySet()) {
				String[] boneValues = interArm.boneData.get(boneKey).split(",");
				String boneName = "";
				
				if(boneKey.endsWith("_MATRIX")) {
					boneName = boneKey.substring(0, boneKey.indexOf("_MATRIX"));
				} else if(boneKey.endsWith("_HEAD")) {
					boneName = boneKey.substring(0, boneKey.indexOf("_HEAD"));
				} else if(boneKey.endsWith("_BIND")) {
					boneName = boneKey.substring(0, boneKey.indexOf("_BIND"));
				} else if(boneKey.endsWith("_INVBIND")) {
					boneName = boneKey.substring(0, boneKey.indexOf("_INVBIND"));
				} else if(boneKey.endsWith("_IS_ROOT")) {
					boneName = boneKey.substring(0, boneKey.indexOf("_IS_ROOT"));
				} else {
					boneName= boneKey;
				}
				
				Bone b = bones.get(boneName);
				
				if(b == null) {
					b = new Bone(boneName);
					bones.put(boneName, b);
				}
				
				if(boneKey.endsWith("_MATRIX")) {
					float[] tempM = new float[boneValues.length];
					for(int i = 0; i < tempM.length; i++) {
						tempM[i] = Float.parseFloat(boneValues[i]);
					}
					

//					if(DEBUG) {
//						SSLog.d(TAG, "\nBONE MATRIX: %s", b.getName());
//						for(int i = 0; i < tempM.length; i++) {
//							SSLog.d(TAG, "MATRIX INDEX %s: %s", i, tempM[i]);
//						}
//						SSLog.d(TAG, "----------------------------\n");
//					}
					
					b.setMatrix(tempM);
			
				} else if(boneKey.endsWith("_HEAD")) {
					float[] loc = new float[boneValues.length];
					for(int i = 0; i < loc.length; i++) {
						loc[i] = Float.parseFloat(boneValues[i]);
					}
					
					Vec3 head = Vec3.fromFloatArray(loc);

//					if(DEBUG) {
//						SSLog.d(TAG, "\nBONE HEAD %s: %s", b.getName(), head.prettyString());
//					}
					
					b.setHead(head);
				}  else if(boneKey.endsWith("_BIND")) {
					float[] tempM = new float[boneValues.length];
					for(int i = 0; i < tempM.length; i++) {
						tempM[i] = Float.parseFloat(boneValues[i]);
					}
					

//					if(DEBUG) {
//						SSLog.d(TAG, "\nBONE BIND MATRIX: %s", b.getName());
//						for(int i = 0; i < tempM.length; i++) {
//							SSLog.d(TAG, "MATRIX INDEX %s: %s", i, tempM[i]);
//						}
//						SSLog.d(TAG, "----------------------------\n");
//					}
					
					b.setBindMatrix(tempM);
				} else if(boneKey.endsWith("_INVBIND")) {
					float[] tempM = new float[boneValues.length];
					for(int i = 0; i < tempM.length; i++) {
						tempM[i] = Float.parseFloat(boneValues[i]);
					}
					

					if(DEBUG) {
						SSLog.d(TAG, "\nBONE INV BIND MATRIX: %s", b.getName());
						for(int i = 0; i < tempM.length; i++) {
							SSLog.d(TAG, "MATRIX INDEX %s: %s", i, tempM[i]);
						}
						SSLog.d(TAG, "----------------------------\n");
					}
					
					b.setInvBindMatrix(tempM);
				} else if(boneKey.endsWith("_IS_ROOT")) {
					b.setRoot(Boolean.parseBoolean(boneValues[0]));
				} else {
					b.setIndex(Integer.parseInt(boneValues[0]));
					
					String parentName = boneValues[1];
					if(!parentName.equals("None")) {
						Bone parent = bones.get(parentName);
						
						if(parent == null) {
							parent = new Bone(parentName);
							bones.put(parentName, parent);
						}
						
						b.setParent(parent);
						parent.addChild(b);
					}
				}
			}
			
			Bone[] boneArray = convertToBoneArray(bones);
			
//			for(Bone b : boneArray) {
//				float[] testing = new float[16];
//				Matrix.setIdentityM(testing, 0);
//				b.setMatrix(testing);
////				if(b.getName().equals("I_Arm_Upper.L")) {
////					float[] transf = new float[]{
////							-0.0497f, -0.9981f,  0.0352f, 0.0000f,
////				             0.9884f, -0.0542f, -0.1418f, 0.0000f,
////				             0.1435f,  0.0277f,  0.9893f, 0.0000f,
////				             0.7275f,  0.0068f,  0.5818f, 1.0000f
////					};					
////					Matrix.translateM(transf, 0, 0.2f, 0.0f, 0.0f);
////					Matrix.rotateM(transf, 0, 45.0f, 0, 0, 1.0f);
////					Matrix.setIdentityM(transf, 0);
////					b.setMatrix(transf);
////				}
//			}
			
			Armature arm = new Armature(interArm.name, boneArray);
			armList.put(arm.getName(), arm);
			
		}
		
		return armList;
	}
	
//	private static Map<String, Bone> rotateBones(Map<String, Bone> bones) {
//	
//		for(String s: bones.keySet()) {
//			Bone b = bones.get(s);
//			
//			if(b.getMatrix() == null) {
//				SSLog.e(TAG, "NULL MATRIX FOUND FOR BONE %s", b.getName());
//			} else if(b.getHead() == null) {
//				SSLog.e(TAG, "NULL HEAD FOUND FOR BONE %s", b.getName());
//			}
//			
//			float[] tempM = new float[16];			
//			Matrix.setIdentityM(tempM, 0);
//			Matrix.translateM(tempM, 0, b.getHead().getX(), b.getHead().getY(), b.getHead().getZ());
//			
//			float[] matrix = new float[16];
//			Matrix.multiplyMM(matrix, 0, tempM, 0, b.getMatrix(), 0);
//			b.setMatrix(matrix);
//		}
//		
//		return bones;
//	}

	private static Bone[] convertToBoneArray(Map<String, Bone> bones) {
		Bone[] boneArray = new Bone[bones.size()];
		
		for(String key : bones.keySet()) {
			Bone b = bones.get(key);
			boneArray[b.getIndex()] = b;
		}
		
		return boneArray;
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
	
//	private static Map<String,IntermediateArmature> parseIntermediateArmature(int resId) throws IOException {
//		Map<String,IntermediateArmature> interArms = new HashMap<String,IntermediateArmature>();
//		Resources res = GameGlobal.inst().getContext().getResources();
////		String resourceName = res.getResourceEntryName(resId);			
//		InputStream is = res.openRawResource(resId);
//		Map<String, byte[]> files = parseFiles(is);
//		
//		for(String key : files.keySet()) {
//			if(key.endsWith(".arm")) {
//				Map<String,String> boneData = SSPropertyUtil.parseFromString(new String(files.get(key)));
//				String name = boneData.get("ARMATURE");
//				boneData.remove("ARMATURE");
//				IntermediateArmature ia = new IntermediateArmature();
//				ia.name = name;
//				ia.boneData = boneData;
//				interArms.put(name, ia);
//			}
//		}
//		
//		return interArms;
//	}
	
	private static Map<String,IntermediateArmature> parseIntermediateArmature(int resId) throws IOException {
		Map<String,IntermediateArmature> interArms = new HashMap<String,IntermediateArmature>();
		Resources res = GameGlobal.inst().getContext().getResources();	
		InputStream is = res.openRawResource(resId);
		Map<String, byte[]> files = parseFiles(is);
		
		for(String key : files.keySet()) {
			if(key.endsWith(".arm")) {
				Map<String,String> boneData = SSPropertyUtil.parseFromString(new String(files.get(key)));
				String name = boneData.get("ARMATURE");
				boneData.remove("ARMATURE");
				IntermediateArmature ia = new IntermediateArmature();
				ia.name = name;
				ia.boneData = boneData;
				interArms.put(name, ia);
			}
		}
		
		return interArms;
	}
	
	private static class IntermediateArmature {
		public String name;
		public Map<String,String> boneData;
	}
	
	private static class IntermediateGeometry {
		public List<String> mObjectNames;
		public Map<String,byte[]> mFiles;
		public Map<String,Map<String,String>> mDescriptors;
	}
}
