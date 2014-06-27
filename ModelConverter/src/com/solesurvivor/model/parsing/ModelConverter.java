package com.solesurvivor.model.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.solesurvivor.collada.Asset;
import com.solesurvivor.collada.COLLADA;
import com.solesurvivor.collada.Geometry;
import com.solesurvivor.collada.InputLocal;
import com.solesurvivor.collada.InputLocalOffset;
import com.solesurvivor.collada.InstanceGeometry;
import com.solesurvivor.collada.Lines;
import com.solesurvivor.collada.Linestrips;
import com.solesurvivor.collada.Mesh;
import com.solesurvivor.collada.Node;
import com.solesurvivor.collada.Polygons;
import com.solesurvivor.collada.Polylist;
import com.solesurvivor.collada.Source;
import com.solesurvivor.collada.Triangles;
import com.solesurvivor.collada.Trifans;
import com.solesurvivor.collada.Tristrips;
import com.solesurvivor.collada.Vertices;
import com.solesurvivor.collada.VisualScene;
import com.solesurvivor.drawing.DrawingConstants;
import com.solesurvivor.drawing.GeometryFormatConstants;
import com.solesurvivor.model.exceptions.SizeLimitExceededException;
import com.solesurvivor.model.exceptions.UnsupportedDrawableException;
import com.solesurvivor.model.parsing.RawGeometry.VertexData;
import com.solesurvivor.model.util.ConversionUtils;
import com.solesurvivor.model.util.Log4JLogUtil;

/**
 * (C)2014 Nicholas Waun. All rights reserved.
 * Program to parse models from Collada DAE format and
 * export relevant data to encapsulating zip file.
 * @author nicholas.waun
 */
public class ModelConverter implements DrawingConstants, GeometryFormatConstants {

	private static final boolean PARSE_ON = true;
	private static final boolean FLIP_TEXTURE_Y = true;
	private static final boolean LOAD_DEFAULT_DESCRIPTORS = false;
	private static final boolean WRITE_TEXCOORD_FILE = false;

	private static final String DATA_DIR = "data/";
	private static final String UNDERSCORE = "_";
	private static final String DOT_REGEX = "[.]";
	private static final String EQUALS = "=";
	private static final String STRID_IND = "";
	private static final String FLOAT_IND = "f";
	private static final String INPUT_DIRECTORY_ARG = "-i";
	private static final String OUTPUT_DIRECTORY_ARG = "-o";

	private static final Log4JLogUtil LOG = new Log4JLogUtil(ModelConverter.class);	

	private static final String[] mMethodNames = new String[] {
		"getName",
		"getId",
		"getSid",
		"getUrl",
		"getSemantic",
		"getSource"
	};

	private Map<Class<?>,Method> mMethods = null;
	private ProgramArgs mProgArgs = null;

	public ModelConverter(ProgramArgs args) {
		mMethods = new HashMap<Class<?>,Method>();
		mProgArgs = args;
	}

	public static void main(String[] args) {
		
		LOG.i("Begin conversion.");

		ProgramArgs pArgs = new ProgramArgs();

		for(int i = 0; i < args.length; i++) {
			if(args[i].equals(INPUT_DIRECTORY_ARG)) {
				pArgs.mInputDir = args[++i];
			} else if(args[i].equals(OUTPUT_DIRECTORY_ARG)){
				pArgs.mOutputDir = args[++i];
			}

		}

		if(StringUtils.isBlank(pArgs.mInputDir)) {
			System.out.println("Must supply input directory with -i.");	
			return;
		}

		if(StringUtils.isBlank(pArgs.mOutputDir)) {
			System.out.println("Must supply output directory with -o.");	
			return;
		}

		File folder = new File(pArgs.mInputDir);
		List<File> inputFiles = new ArrayList<File>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()) {
				inputFiles.add(fileEntry);
			}
		}

		ModelConverter mc = new ModelConverter(pArgs);
		
		LOG.d(String.format("Java byte order: %s", ByteOrder.nativeOrder().toString()));

		for(File f : inputFiles) {
			String[] fileNameParts = f.getName().split(DOT_REGEX);
			String modelName = fileNameParts[0];
			String fileExt = fileNameParts[fileNameParts.length - 1];

			if(!StringUtils.equalsIgnoreCase(fileExt, DAE_EXT)) {
				continue;
			}

			try {
				LOG.d("Parsing Collada...");
				COLLADA c = parseFromFile(f);
				LOG.d("Parsing Scene...");
				VboIboObject s = parseGeometryFromCollada(c, modelName);
				LOG.d("Writing %s zip...", modelName);
				mc.writeVboIboGeometryToZip(s, modelName);
			} catch (FileNotFoundException e) {
				LOG.e("Could not write zip for %s.", e, f.getName());
			} catch (IOException e) {
				LOG.e("Could not write zip for %s.", e, f.getName());
			} catch (SizeLimitExceededException e) {
				LOG.e("Could not buld geometry for %s.", e, f.getName());
			}
		}
		
		LOG.i("End conversion.");
	}

	public static COLLADA parseFromFile(File f) throws FileNotFoundException {

		LOG.i("Parsing file: %s", f.getName());

		InputStream is = new FileInputStream(f);

		COLLADA collada = null;

		try {
			if(PARSE_ON) {
				collada = parseCollada(is);
			}
		} catch (JAXBException e) {
			LOG.e("Exception encountered parsing Collada DAE source.", e);
		} finally {
			if(is != null){try {is.close();} catch (IOException e) {}}
		}

		return collada;
	}

	public static VboIboObject parseGeometryFromCollada(COLLADA collada, String modelName) throws SizeLimitExceededException {

		//This is the object we are populating
		VboIboObject geometryObject = new VboIboObject();

		//Save off the Asset element as String xml.
		//We will include this in the file for copyright purposes.
		try {
			geometryObject.mAssetXml = serializeAssetsXml(collada.getAsset());
		} catch (JAXBException e) {
			LOG.e(String.format("Error serializing Asset for %s.", modelName), e);
		}

		//Build a nav map for getting references
		ModelNavMap mnm = new ModelNavMap(collada);

		//Pull the scene instance
		VisualScene visualScene = mnm.getObject(collada.getScene().getInstanceVisualScene().getUrl());
		geometryObject.mName = visualScene.getName();

		//Make a list for storing the nodes
		List<VboIboPackedGeometry> nodes = new ArrayList<VboIboPackedGeometry>();

		//For each node in the scene
		for(Node node : visualScene.getNodes()) {

			//We only care if the node has geometry
			for(InstanceGeometry ig : node.getInstanceGeometries()) {
				RawGeometry raw = parseRawGeometry(ig, mnm);

				if(raw == null) {
					LOG.i("No Geometry/Mesh found for instance %s.", ig.getName());
					continue;
				}
				
//				/*DEBUG CODE*/
//				FileOutputStream fos = null;								
//				try {
//					fos = new FileOutputStream("C:\\Users\\nicholas.waun\\git\\openglsandbox\\ModelConverter\\res\\out\\texcoords.txt");
//					float[] tc = raw.vertexData[2].data;
//					fos.write(String.format("%s\n",tc.length).getBytes());
//					for(int i = 0; i < tc.length; i++) {
//						fos.write(String.format("%sf\n",tc[i]).getBytes());
//					}
//					
//					
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					IOUtils.closeQuietly(fos);
//				}
//				/*DEBUG CODE*/
				

				VboIboPackedGeometry gNode = parseVboIboGeometry(raw);
				nodes.add(gNode);

			}
		}

		geometryObject.mGeometries = nodes;

		LOG.d(String.format("Visual scene: %s",visualScene.getName()));

		return geometryObject;
	}

	public static VboIboPackedGeometry parseVboIboGeometry(RawGeometry raw) throws SizeLimitExceededException {
		VboIboPackedGeometry vbpg = new VboIboPackedGeometry();

		if(StringUtils.isBlank(raw.name)) {
			LOG.w("RawGeometry name was null in parseGeometry(RawGeometry).");
		}
		vbpg.mName = raw.name;


		//Counts the total number
		//of vertices that will be drawn.
		//This is equal to vcount.length *3
		//when all polygons are triangles
		int totalVCount = 0;
		if(raw.vcount != null) {			
			for(int i = 0; i < raw.vcount.length; ++i) {
				totalVCount += raw.vcount[i];
			}
			LOG.d("(%s) Total VCount: %s", raw.name, totalVCount);
			vbpg.mNumElements = totalVCount; //Total number of vertices
			vbpg.mNumPolys = raw.vcount.length; //Number of polygons
		} else {
			LOG.d("raw.vcount was null.");
		}

		//Just to check if my meshes get big enough
		//to start using ints
		if(vbpg.mNumElements > Short.MAX_VALUE) {
			String message = String.format("Number of elements to be drawn exceeds Short.MAX_VALUE: %s", 
					vbpg.mNumElements);
			throw new SizeLimitExceededException(message);
		}

		int currentPIndex = 0; //Index of current Vertex Position 
		int currentNIndex = 0; //Index of current Vertex Normal
		int currentTIndex = 0; //Index of current Vertex Texture Coordinate
		short currentDataIndex = 0;
		List<Float> vboData = new ArrayList<Float>();
		List<Short> iboData = new ArrayList<Short>();		
		
		vbpg.mPosSize = raw.vertexData[0].stride;
		vbpg.mNrmSize = raw.vertexData[1].stride;
		vbpg.mTxcSize = raw.vertexData[2].stride;
		
		vbpg.mTotalStride = (vbpg.mPosSize + vbpg.mNrmSize + vbpg.mTxcSize) * BYTES_PER_FLOAT;
		vbpg.mPosOffset = 0;
		vbpg.mNrmOffset = vbpg.mPosSize * BYTES_PER_FLOAT;
		vbpg.mTxcOffset = (vbpg.mPosSize + vbpg.mNrmSize) * BYTES_PER_FLOAT;
		
		if(FLIP_TEXTURE_Y) {
			raw.vertexData[2].remappedData = flipTextureY(raw.vertexData[2].remappedData);
		}
		
		//For each polygon in the vcount list
		for(int vcountPos = 0, primitivePos = 0; vcountPos < raw.vcount.length; vcountPos++) {
			int numVertices = raw.vcount[vcountPos];

			//Iterate over the number of verts in the poly
			for(int vertexIdx = 0; vertexIdx < numVertices; vertexIdx++) {		
				
				VertexData vd0 = raw.vertexData[0];
				VertexData vd1 = raw.vertexData[1];
				VertexData vd2 = raw.vertexData[2];

				//Assuming the order per indexes above
				currentPIndex = raw.p[primitivePos];
				currentNIndex = raw.p[primitivePos+1];
				currentTIndex = raw.p[primitivePos+2];
				
				int betterPIndex = vd0.remappedIndex[currentPIndex];
				int betterNIndex = vd1.remappedIndex[currentNIndex];
				int betterTIndex = vd2.remappedIndex[currentTIndex];
				//Stride the primitive index
				primitivePos += 3;
				
				
				//TODO:
				//Really currentP+currentN+currentT need to be treated
				//as one index and re-optomized like before.
				
				//Go to the position array and get the XYZ at position currentPIndex
				List<Float> currentP = getVertexDataAt(vd0, currentPIndex);
				List<Float> betterP = getBetterVertexDataAt(vd0, betterPIndex);

				//Go to the normal array and get the XYZ at position currentNIndex
				List<Float> currentN = getVertexDataAt(vd1, currentNIndex);
				List<Float> betterN = getBetterVertexDataAt(vd1, betterNIndex);

				//Go to the texture coordinate array and get the UV at position currentTIndex
				List<Float> currentT = getVertexDataAt(vd2, currentTIndex);
				List<Float> betterT = getBetterVertexDataAt(vd2, betterTIndex);
				
				/*New - Log the changes for the remapping*/
				LOG.d("Mapping P Index: %s -> %s", currentPIndex, betterPIndex);
				LOG.d("P Value Comparison: %s -> %s", ArrayUtils.toString(currentP), ArrayUtils.toString(betterP));
				if(!ArrayUtils.isEquals(currentP,betterP)) {
					LOG.e("ARRAYS NOT EQUAL! %s -> %s", ArrayUtils.toString(currentP), ArrayUtils.toString(betterP));
				}
				
				LOG.d("Mapping N Index: %s -> %s", currentNIndex, betterNIndex);
				LOG.d("N Value Comparison: %s -> %s", ArrayUtils.toString(currentN), ArrayUtils.toString(betterN));
				if(!ArrayUtils.isEquals(currentN,betterN)) {
					LOG.e("ARRAYS NOT EQUAL! %s -> %s", ArrayUtils.toString(currentN), ArrayUtils.toString(betterN));
				}
				
				LOG.d("Mapping T Index: %s -> %s", currentTIndex, betterTIndex);
				LOG.d("T Value Comparison: %s -> %s", ArrayUtils.toString(currentT), ArrayUtils.toString(betterT));
				if(!ArrayUtils.isEquals(currentT,betterT)) {
					LOG.e("ARRAYS NOT EQUAL! %s -> %s", ArrayUtils.toString(currentT), ArrayUtils.toString(betterT));
				}
				
				//TODO: Fix this indexing so that it is not 1 data entry per index.
				vboData.addAll(betterP);
				vboData.addAll(betterN);
				vboData.addAll(betterT);
				iboData.add(currentDataIndex);
				currentDataIndex++;			
			}


		}
		
		vbpg.mData = ArrayUtils.toPrimitive(vboData.toArray(new Float[vboData.size()]));
		vbpg.mIndexes = ArrayUtils.toPrimitive(iboData.toArray(new Short[iboData.size()]));
		
		/*DEBUG CODE*/
		if(WRITE_TEXCOORD_FILE) {
			FileOutputStream fos = null;								
			try {
				fos = new FileOutputStream("C:\\Users\\nicholas.waun\\git\\openglsandbox\\ModelConverter\\res\\out\\texcoords.txt");

				//Rebuld tc from packed data
				List<Float> txcList = new ArrayList<Float>();
				for(int i = 0; i < vbpg.mIndexes.length; i++) {
					short baseIndex = vbpg.mIndexes[i];
					int coordIndex = (baseIndex * 8) + 6;

					txcList.add(vbpg.mData[coordIndex]);
					txcList.add(vbpg.mData[coordIndex+1]);
				}
				float[] tc =  ArrayUtils.toPrimitive(txcList.toArray(new Float[txcList.size()]));
				fos.write(String.format("%s\n",tc.length).getBytes());
				for(int i = 0; i < tc.length; i++) {
					fos.write(String.format("%sf\n",tc[i]).getBytes());
				}


			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
		/*DEBUG CODE*/

		return vbpg;
	}

	private static float[] flipTextureY(float[] remappedData) {
		float[] flippedData = new float[remappedData.length];
		
		for(int i = 0; i < remappedData.length; i ++) {
			if(i % 2 == 0) {
				//U
				flippedData[i] = remappedData[i];
			} else {
				//V
				flippedData[i] = 1 - remappedData[i];
			}
			
		}
		
		return flippedData;
	}

	protected static List<Float> getVertexDataAt(VertexData vertexData, int index) {
		List<Float> data = new ArrayList<Float>(vertexData.stride);		
		for(int i = 0; i < vertexData.stride; i++) {
			data.add(vertexData.data[(index * vertexData.stride)+i]);
		}
		return data;
	}
	
	protected static List<Float> getBetterVertexDataAt(VertexData vertexData, int index) {
		List<Float> data = new ArrayList<Float>(vertexData.stride);
		for(int i = 0; i < vertexData.stride; i++) {
			data.add(vertexData.remappedData[(index * vertexData.stride)+i]);
		}
		return data;
	}

	public static RawGeometry parseRawGeometry(InstanceGeometry ig, ModelNavMap mnm) {

		LOG.t("Parsing RawGeometry. (InstanceGeometry URL: %s)", ig.getUrl());

		RawGeometry rg = null;

		Geometry g = mnm.getObject(ig.getUrl());

		LOG.d("Preparing to parse geometry: %s", g.getName());

		Mesh m = g.getMesh();

		for(Object d : m.getLinesAndLinestripsAndPolygons()) {
			List<InputLocalOffset> ilos = new ArrayList<InputLocalOffset>(5);

			try {
				if(d instanceof Polylist){
					LOG.t("(%s) Parsing Polylist drawable.", g.getName());
					Polylist drawable = (Polylist)d;
					ilos.addAll(drawable.getInputs());
					rg = new RawGeometry(ilos.size());
					rg.vcount = ConversionUtils.parseIntArray(drawable.getVcount());
					rg.p = ConversionUtils.parseIntArray(drawable.getP());
				} else if (d instanceof Trifans) {
					LOG.t("(%s) Parsing Trifans drawable.", g.getName());
					throw new UnsupportedDrawableException("Cannot parse drawable of type Trifans.");
					//				Trifans drawable = (Trifans) d;
					//				ilos.addAll(drawable.getInputs());
				} else if (d instanceof Polygons) {
					LOG.t("(%s) Parsing Polygons drawable.", g.getName());
					throw new UnsupportedDrawableException("Cannot parse drawable of type Polygons.");
					//				Polygons drawable = (Polygons) d;
					//				ilos.addAll(drawable.getInputs());
				} else if (d instanceof Linestrips) {
					LOG.t("(%s) Parsing Linestrips drawable.", g.getName());
					throw new UnsupportedDrawableException("Cannot parse drawable of type Linestrips.");
					//				Linestrips drawable = (Linestrips) d;
					//				ilos.addAll(drawable.getInputs());
				} else if (d instanceof Triangles) {
					LOG.t("(%s) Parsing Triangles drawable.", g.getName());
					throw new UnsupportedDrawableException("Cannot parse drawable of type Triangles.");
					//				Triangles drawable = (Triangles) d;
					//				ilos.addAll(drawable.getInputs());
				} else if (d instanceof Lines) {
					LOG.t("(%s) Parsing Lines drawable.", g.getName());
					throw new UnsupportedDrawableException("Cannot parse drawable of type Lines.");
					//				Lines drawable = (Lines) d;
					//				ilos.addAll(drawable.getInputs());
				} else if (d instanceof Tristrips) {
					LOG.t("(%s) Parsing Tristrips drawable.", g.getName());
					throw new UnsupportedDrawableException("Cannot parse drawable of type Tristrips.");
					//				Tristrips drawable = (Tristrips) d;
					//				ilos.addAll(drawable.getInputs());
				}

			} catch(UnsupportedDrawableException ude) {
				LOG.w("%s is not a supported drawable type (%s).", ude, d.getClass().getSimpleName(), ig.getName());
			}

			if(rg != null && ilos.size() > 0) {
				rg.vertexData = new RawGeometry.VertexData[ilos.size()];
				for(InputLocalOffset ilo : ilos) {
					LOG.t("(%s) Input - Semantic: %s, Offset: %s", g.getName(), ilo.getSemantic(), ilo.getOffset());
					Object source = mnm.getObject(ilo.getSource());
					int offset = ilo.getOffset().intValue();

					if(source instanceof Vertices) {
						Vertices v = (Vertices)source;
						source = v.getInputs().get(0); //ASSUMING THERE IS ONLY 1???
					}

					if(source instanceof InputLocal) {
						InputLocal il = (InputLocal)source;
						source = mnm.getObject(il.getSource());
					}

					if(source instanceof Source) {
						Source s = (Source)source;

						LOG.t("(%s) Source - using %s", g.getName(), s.getFloatArray().getId());
						rg.vertexData[offset] = rg.new VertexData();
						rg.vertexData[offset].data = ConversionUtils.parseFloatArray(s.getFloatArray().getValues());
						rg.vertexData[offset].stride = s.getTechniqueCommon().getAccessor().getStride().intValue();
						rg.vertexData[offset].count = s.getTechniqueCommon().getAccessor().getCount().intValue();
						rg.vertexData[offset].semantic = RawGeometry.Semantic.valueOf(ilo.getSemantic());								
						rg.vertexData[offset] = remapVertexData(rg, offset);
					}

				}

				rg.name = (ig.getName() == null ? ig.getUrl() : ig.getName());

			}

		}

		return rg;
	}
	
	private static VertexData remapVertexData(RawGeometry rg, int offset) {
		VertexData vd = rg.vertexData[offset];
		
		Map<String,Integer> uniqueValues = new HashMap<String,Integer>();
		int[] newIndex = new int[rg.p.length / rg.vertexData.length];
		List<Float> remappedData = new ArrayList<Float>();
		int currentIndex = 0;
		
		//Iterate over the primitives
		for(int i = offset; i < rg.p.length; i += rg.vertexData.length) {
			int indexFromP = rg.p[i];
			
			//Concatenate the values
			//at this index to build a key
			String key = "";
			List<Float> values = new ArrayList<Float>(vd.stride);
			for(int j = 0; j < vd.stride; j++) {
				key += String.valueOf(vd.data[(indexFromP*vd.stride)+j]);
				values.add(vd.data[(indexFromP*vd.stride)+j]);
			}
						
			//If this is a new set of data
			//Add it to the remapped data
			//and save it with its index
			if(uniqueValues.get(key) == null) {
				remappedData.addAll(values);
				uniqueValues.put(key, currentIndex);	
				currentIndex++;
			}

			int indexFromMap = uniqueValues.get(key);
			
			newIndex[indexFromP] = indexFromMap;		
			
			/*Test what we just did*/
						
			int testIndex = newIndex[indexFromP];
			LOG.d("New index mapping: %s -> %s", indexFromP, testIndex);
			
			float testX = remappedData.get(testIndex*vd.stride);
			float testY = remappedData.get((testIndex*vd.stride) + 1);
			float origX = values.get(0);
			float origY = values.get(1);
			
			LOG.d("Vertex comparison: %s,%s -> %s,%s", origX, origY, testX, testY);
			
			if(origX != testX) {
				LOG.d("X values do not match! %s -> %s", origX, testX);
			}
			
			if(origY != testY) {
				LOG.d("Y values do not match! %s -> %s", origY, testY);
			}
			
			/*Test what we just did*/
		}
		
		VertexData remap = rg.new VertexData();
		remap.data = vd.data;
		remap.remappedData = ArrayUtils.toPrimitive(remappedData.toArray(new Float[remappedData.size()]));
		remap.remappedIndex = newIndex;
		remap.count = remap.remappedData.length / vd.stride;
		remap.semantic = vd.semantic;
		remap.stride = vd.stride;
		remap.readCount = vd.readCount;
		
		LOG.d("Remapped data at offset %s. Old count: %s, new count: %s. ", offset, vd.count, remap.count);
		
		return remap;
	}

	protected void writeVboIboGeometryToZip(VboIboObject vboObj, String modelName) throws IOException {
		if(vboObj == null) {
			throw new IllegalArgumentException("Param 'scene' cannot be null.");
		}

		if(modelName == null || modelName.trim().equals("")) {
			throw new IllegalArgumentException("Param 'modelName' cannot be null.");
		}

		String zipFile = mProgArgs.mOutputDir + File.separator + modelName + ZIP_FILE_EXT;

		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		StringBuilder indexFile = new StringBuilder();

		try {

			Map<String,String> descriptors = null;
			InputStream inStream = null;
			String descFileName = DATA_DIR + (modelName + DSC_FILE_EXT).trim();
			try {
				inStream = ModelConverter.class.getClassLoader().getResourceAsStream(descFileName);
				if(inStream != null) {
					String descFile = IOUtils.toString(inStream);
					descriptors = parseMap(descFile);
				}
			} finally {
				IOUtils.closeQuietly(inStream);
			}

			if(StringUtils.isNotBlank(vboObj.mAssetXml)) {
				LOG.t("Writing asset to zip.");
				writeZipEntry(zos, ASSETS_FILE_NAME, vboObj.mAssetXml);
			}

			for(VboIboPackedGeometry geo : vboObj.mGeometries) {
				LOG.t("Writing Geometry: %s - %s", geo.mName, geo.getClass().getSimpleName());

				String name = ConversionUtils.chopPound(geo.mName);
				indexFile.append(L1_SYM).append(name).append(LINE_SEP);

				String fileName = null;

				//VBO
				if(geo.mData != null) {
					fileName = name + V_FILE_EXT;
					byte[] fileContents = floatToByteArray(geo.mData);
					
					for(int i = 0; i < 32*4; i+=4) {
						LOG.d("Float: %s %s %s %s", fileContents[i],fileContents[i+1],fileContents[i+2],fileContents[i+3]);
					}
					
					indexFile.append(L2_SYM).append(fileName).append(LINE_SEP);
					writeZipEntry(zos, fileName, fileContents);
				} else {
					LOG.w("Positions array was null for geometry %s.",geo.mName);
				}

				//IBO
				if(geo.mIndexes != null) {
					fileName = name + I_FILE_EXT;
					byte[] fileContents = shortToByteArray(geo.mIndexes);
					
					for(int i = 0; i < 12*2; i+=2) {
						LOG.d("Short: %s %s", fileContents[i],fileContents[i+1]);
					}
					
					indexFile.append(L2_SYM).append(fileName).append(LINE_SEP);
					writeZipEntry(zos, fileName, fileContents);
				} else {
					LOG.w("Positions array was null for geometry %s.",geo.mName);
				}

				//MESH DESCRIPTOR
				Map<String,String> meshDesc = new HashMap<String,String>();

				//Pull in some defaults
				if(LOAD_DEFAULT_DESCRIPTORS) {
					if(descriptors != null && descriptors.size() > 0) {
						meshDesc.putAll(descriptors);
					}
				}

				meshDesc.put("name",name);
				
				meshDesc.put("num_elements",String.valueOf(geo.mNumElements));
				meshDesc.put("element_stride",String.valueOf(geo.mTotalStride));

				meshDesc.put("pos_size",String.valueOf(geo.mPosSize));
				meshDesc.put("nrm_size",String.valueOf(geo.mNrmSize));
				meshDesc.put("txc_size",String.valueOf(geo.mTxcSize));

				meshDesc.put("pos_offset",String.valueOf(geo.mPosOffset));
				meshDesc.put("txc_offset",String.valueOf(geo.mTxcOffset));
				meshDesc.put("nrm_offset",String.valueOf(geo.mNrmOffset));

				fileName = name + DESC_FILE_EXT;
				String fileContents = parseMap(meshDesc);
				indexFile.append(L2_SYM).append(fileName).append(LINE_SEP);
				writeZipEntry(zos, fileName, fileContents);

			}

			LOG.t("Writing index.");
			writeZipEntry(zos, INDEX_FILE_NAME, indexFile.toString());

			LOG.d(String.format("Writing model zip: %s",zipFile));
		} finally {
			if(zos != null){zos.close();}
		}

	}

	protected String parseMap(Map<String, String> mappy) {
		StringBuilder sb = new StringBuilder();

		for(Map.Entry<String,String> e : mappy.entrySet()) {
			if(sb.length() > 0) {
				sb.append(LINE_SEP);
			}
			sb.append(e.getKey()).append(EQUALS).append(e.getValue());
		}

		return sb.toString();
	}

	protected Map<String, String> parseMap(String s) {
		Map<String, String> mappy = new HashMap<String, String>();

		String[] entries = s.split(LINE_SEP);

		for(int i = 0; i < entries.length; i++) {
			String[] kv = entries[i].split(EQUALS);
			if(kv.length > 1) {
				mappy.put(kv[0], kv[1]);
			} else if(kv.length == 1) {
				mappy.put(kv[0], StringUtils.EMPTY);
			}
		}

		return mappy;
	}

	protected byte[] shortToByteArray(short[] data) {
		byte[] bytes = new byte[data.length * BYTES_PER_SHORT];

		for(int i = 0, j = 0; i < data.length; i++) {
			byte[] onefloat = ByteBuffer.allocate(BYTES_PER_SHORT).putShort(data[i]).array();
			bytes[j++] = onefloat[0];
			bytes[j++] = onefloat[1];
		}

		return bytes;
	}

	protected byte[] floatToByteArray(float[] data) {

		byte[] bytes = new byte[data.length * BYTES_PER_FLOAT];

		for(int i = 0, j = 0; i < data.length; i++) {
			byte[] onefloat = ByteBuffer.allocate(BYTES_PER_FLOAT).putFloat(data[i]).array();
			bytes[j++] = onefloat[0];
			bytes[j++] = onefloat[1];
			bytes[j++] = onefloat[2];
			bytes[j++] = onefloat[3];

		}

		return bytes;
	}

	protected String buildDataString(int stride, float[] data) {
		StringBuilder sb;
		String fileContents;
		sb = new StringBuilder();

		sb.append(String.valueOf(stride)).append(STRID_IND).append(LINE_SEP);

		for(float f : data) {
			sb.append((String.valueOf(f))).append(FLOAT_IND).append(LINE_SEP);
		}

		fileContents = sb.toString();
		return fileContents;
	}

	protected static void writeZipEntry(ZipOutputStream zos, String fileName, String contents) throws IOException {
		writeZipEntry(zos, fileName, contents.getBytes());
	}

	protected static void writeZipEntry(ZipOutputStream zos, String fileName, byte[] contents) throws IOException {
		ZipEntry zeIndex = new ZipEntry(ConversionUtils.chopPound(fileName));
		zos.putNextEntry(zeIndex);
		zos.write(contents);
		zos.closeEntry();
	}

	protected static String serializeAssetsXml(Asset asset) throws JAXBException {
		String assetXml = null;

		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Asset.class);
		Marshaller marsh = context.createMarshaller();
		marsh.marshal(asset, writer);
		assetXml = writer.toString();

		return assetXml;
	}

	protected String getPreferredName(Object o) {
		if(o == null) return null;		

		Method m = mMethods.get(o.getClass());

		if(m == null) {

			for(String s : mMethodNames) {
				Method m1 = null;
				try {m1 = o.getClass().getMethod(s, new Class<?>[0]);} catch (SecurityException e) {} catch (NoSuchMethodException e) {}
				if(m1 == null) continue;
				mMethods.put(o.getClass(), m1);
				m = m1;
				break;
			}

		}

		Object invokationResult = null;

		try {
			invokationResult = m.invoke(o, new Object[0]);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {}

		String name = null;

		if(invokationResult != null) {
			name = o.getClass().getSimpleName() + UNDERSCORE + invokationResult.toString();
		} else {
			name = o.getClass().getSimpleName() + UNDERSCORE;
		}

		return name;
	}

	public static void writeCollada(COLLADA c, String outputFile) throws IOException {
		FileOutputStream fout = new FileOutputStream(outputFile);
		ObjectOutputStream oout = null;
		try {
			LOG.d("Writing model: %s", c);
			oout = new ObjectOutputStream(fout);
			oout.writeObject(c);
		} finally {
			if(oout != null) {try {oout.close();} catch (IOException e) {}}
		}

	}

	public <T> void writeModelXml(T target, String directory) throws JAXBException, IOException {

		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(target.getClass());
		Marshaller marsh = context.createMarshaller();
		marsh.marshal(target, writer);
		System.out.println(writer.toString());

		StringBuilder fileName = new StringBuilder();

		fileName.append(getPreferredName(target)).append(UNDERSCORE).append(UUID.randomUUID()).append(XML_FILE_EXT);

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(directory + File.separator + fileName.toString());
			fos.write(writer.toString().getBytes());
		} finally {
			if(fos != null) try{fos.close();}catch(Exception e){}
		}		

	}

	public static COLLADA parseCollada(InputStream input) throws JAXBException {
		JAXBContext context = null;
		COLLADA c = null;

		LOG.t("Begin JAXB unmarshal.");

		context = JAXBContext.newInstance(COLLADA.class);
		Unmarshaller m = context.createUnmarshaller();
		c = (COLLADA)m.unmarshal(input);

		LOG.t("Collada unmarshalled.");

		return c;
	}

	protected static String getDebugValue(String id, int value) {
		if(id.equals("data point")) {
			switch(value) {
			case 0: return "X";
			case 1: return "Y";
			case 2: return "Z";
			}
		}
		return null;
	}

}
