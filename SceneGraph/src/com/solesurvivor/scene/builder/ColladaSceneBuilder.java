package com.solesurvivor.scene.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.solesurvivor.scene.PackedGeometry;
import com.solesurvivor.scene.Scene;

public class ColladaSceneBuilder extends SceneBuilder {

	private static final String NS = null;
	private static final String LIB_GEOMETRY_EL_NAME = "library_geometries";
	private static final String GEOMETRY_EL_NAME = "geometry";
	private static final String MESH_EL_NAME = "mesh";
	private static final String SOURCE_EL_NAME = "source";
	private static final String POLYLIST_EL_NAME = "polylist";
	private static final String P_EL_NAME = "p";
	private static final String VCOUNT_EL_NAME = "vcount";
	private static final String FLOATARRAY_EL_NAME = "floatarray";

	private PackedGeometryBuilder.GeometryData mWorkGeo = null;
	
	@Override
	public Scene buildScene() {
		Scene scene = new Scene();
		return scene;
	}

	public List<PackedGeometry> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readModels(parser);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public List<PackedGeometry> readModels(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<PackedGeometry> entries = new ArrayList<PackedGeometry>();

		parser.require(XmlPullParser.START_TAG, NS, "COLLADA");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(LIB_GEOMETRY_EL_NAME)) {
				entries.addAll(readLibGeometries(parser));
			} else {
				skip(parser);
			}
		}  
		return entries;

	}
	
	private List<PackedGeometry> readLibGeometries(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<PackedGeometry> entries = new ArrayList<PackedGeometry>();

		parser.require(XmlPullParser.START_TAG, NS, LIB_GEOMETRY_EL_NAME);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(GEOMETRY_EL_NAME)) {
				entries.add(readGeometry(parser));
			} else {
				skip(parser);
			}
		}  
		return entries;
	}

	private PackedGeometry readGeometry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, GEOMETRY_EL_NAME);
		PackedGeometry pGeo = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(MESH_EL_NAME)) {
				mWorkGeo = new PackedGeometryBuilder.GeometryData();
				mWorkGeo.mName = parser.getAttributeValue(NS, "name");
				readMesh(parser);
				pGeo = PackedGeometryBuilder.build(mWorkGeo);
			} else {
				skip(parser);
			}
		}
		return pGeo;

	}

	private void readMesh(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, MESH_EL_NAME);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(SOURCE_EL_NAME)) {
				readSource(parser);
			} else if (name.equals(POLYLIST_EL_NAME)) {
				readPolyList(parser);
			} else {
				skip(parser);
			}
		}
	}

	private void readSource(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, SOURCE_EL_NAME);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(FLOATARRAY_EL_NAME)) {
				if(parser.getAttributeValue(NS, "id").indexOf("positions-array") > -1) {
					mWorkGeo.mPosData = parseFloatArray(parser.getText());
				} else if(parser.getAttributeValue(NS, "id").indexOf("normals-array") > -1) {
					mWorkGeo.mNrmData = parseFloatArray(parser.getText());
				} else if(parser.getAttributeValue(NS, "id").indexOf("map-0-array") > -1) {
					mWorkGeo.mTxcData = parseFloatArray(parser.getText());
				}
			} else {
				skip(parser);
			}
		}

	}

	private void readPolyList(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, POLYLIST_EL_NAME);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(VCOUNT_EL_NAME)) {
				mWorkGeo.mVCount = parseIntArray(parser.getText());
			} else if (name.equals(P_EL_NAME)) {
				mWorkGeo.mPrimData = parseIntArray(parser.getText());
			} else {
				skip(parser);
			}
		}
	}
	
	private float[] parseFloatArray(String text) {
		String[] strings = text.split(" ");
		float[] floats = new float[strings.length];
		for(int i = 0; i < strings.length; i++) {
			floats[i] = Float.parseFloat(strings[i]);
		};
		return floats;
	}
	
	private int[] parseIntArray(String text) {
		String[] strings = text.split(" ");
		int[] ints = new int[strings.length];
		for(int i = 0; i < strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		};
		return ints;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
