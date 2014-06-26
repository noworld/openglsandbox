package com.solesurvivor.model.parsing;

import java.util.HashMap;
import java.util.Map;

import com.solesurvivor.collada.Animation;
import com.solesurvivor.collada.COLLADA;
import com.solesurvivor.collada.Geometry;
import com.solesurvivor.collada.LibraryAnimations;
import com.solesurvivor.collada.LibraryGeometries;
import com.solesurvivor.collada.LibraryVisualScenes;
import com.solesurvivor.collada.Node;
import com.solesurvivor.collada.Sampler;
import com.solesurvivor.collada.Source;
import com.solesurvivor.collada.VisualScene;
import com.solesurvivor.model.util.Log4JLogUtil;

public class ModelNavMap {
	
	private static final String POUND = "#";
	
	private static final Log4JLogUtil LOG = new Log4JLogUtil(ModelNavMap.class);

	private Map<String, Object> navMap;
	private COLLADA model;

	public ModelNavMap(COLLADA collada) {
		
		model = collada;
		navMap = buildNavMap();
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(String url) {
		
		if(url == null || url.trim().equals("")) return null;
		
		Object source = navMap.get(trimUri(url));
		
		if(source == null) return null;
		
		return (T)source;
	}

	private Map<String, Object> buildNavMap() {
		LOG.d("Building ModelNavMap.");
		Map<String, Object> nm = new HashMap<String,Object>();
		
		/* Objects of the following type(s) are allowed in the list
		 * {@link LibraryEffects }
		 * {@link LibraryAnimations }
		 * {@link LibraryAnimationClips }
		 * {@link LibraryMaterials }
		 * {@link LibraryVisualScenes }
		 * {@link LibraryForceFields }
		 * {@link LibraryPhysicsModels }
		 * {@link LibraryNodes }
		 * {@link LibraryCameras }
		 * {@link LibraryPhysicsMaterials }
		 * {@link LibraryImages }
		 * {@link LibraryLights }
		 * {@link LibraryGeometries }
		 * {@link LibraryControllers }
		 * {@link LibraryPhysicsScenes }
		 */
		for(Object o : model.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras()) {
			
			if (o instanceof LibraryGeometries) {
				nm.putAll(mapLibraryGeometries((LibraryGeometries)o));
			} else if (o instanceof LibraryAnimations) {
				nm.putAll(mapLibraryAnimations((LibraryAnimations)o));
			} else if (o instanceof LibraryVisualScenes) {
				nm.putAll(mapLibraryVisualScenes((LibraryVisualScenes)o));
			}

			//Not used yet
			//		    else if(o instanceof LibraryAnimationClips) {LibraryAnimationClips lib = (LibraryAnimationClips)o;}
			//		    else if(o instanceof LibraryMaterials) {LibraryMaterials lib = (LibraryMaterials)o;}		    
			//		    else if(o instanceof LibraryForceFields) {LibraryForceFields lib = (LibraryForceFields)o;}
			//		    else if(o instanceof LibraryPhysicsModels) {LibraryPhysicsModels lib = (LibraryPhysicsModels)o;}
			//		    else if(o instanceof LibraryNodes) {LibraryNodes lib = (LibraryNodes)o;}
			//		    else if(o instanceof LibraryCameras) {LibraryCameras lib = (LibraryCameras)o;}
			//		    else if(o instanceof LibraryPhysicsMaterials) {LibraryPhysicsMaterials lib = (LibraryPhysicsMaterials)o;}
			//		    else if(o instanceof LibraryImages) {LibraryImages lib = (LibraryImages)o;}
			//		    else if(o instanceof LibraryLights) {LibraryLights lib = (LibraryLights)o;}
			//		    else if(o instanceof LibraryEffects) {LibraryEffects lib = (LibraryEffects)o;}
			//		    else if(o instanceof LibraryControllers) {LibraryControllers lib = (LibraryControllers)o;}
			//		    else if(o instanceof LibraryPhysicsScenes) {LibraryPhysicsScenes lib = (LibraryPhysicsScenes)o;}
		}
		
		return nm;
	}

	private Map<String,Object> mapLibraryVisualScenes(LibraryVisualScenes lib) {
		
		Map<String, Object> nm = new HashMap<String, Object>();
		
		for(VisualScene vs : lib.getVisualScenes()) {
			nm.put(vs.getId(), vs);

			for(Node n : vs.getNodes()) {
				nm.put(n.getId(), n);
			}
		}
		
		return nm;
		
	}

	private Map<String,Object> mapLibraryAnimations(LibraryAnimations lib) {

		Map<String, Object> nm = new HashMap<String, Object>();
		
		for(Animation a : lib.getAnimations()) {
			nm.putAll(mapAnimation(a));
		}
		
		return nm;
	}

	private Map<String,Object> mapAnimation(Animation a) {
		Map<String, Object> nm = new HashMap<String, Object>();

		nm.put(a.getId(), a);

		for(Source s : a.getSources()) {
			nm.put(s.getId(), s);
		}

		for(Sampler s : a.getSamplers()) {
			nm.put(s.getId(), s);
		}

		for(Animation a1 : a.getAnimations()) {
			mapAnimation(a1);
		}

		return nm;
	}

	private Map<String,Object> mapLibraryGeometries(LibraryGeometries lib) {
		LOG.t("Mapping Library: %s",lib.getClass().getName());
		
		Map<String, Object> nm = new HashMap<String, Object>();
		
		for(Geometry g : lib.getGeometries()) {
			LOG.t("Mapping Geometry: %s",g.getId());
			nm.put(g.getId(), g);

			for(Source s : g.getMesh().getSources()) {
				LOG.t("Mapping Source: %s",s.getId());
				nm.put(s.getId(), s);
			}
			
			/* {@link Trifans }
		     * {@link Polygons }
		     * {@link Linestrips }
		     * {@link Triangles }
		     * {@link Lines }
		     * {@link Tristrips }
		     * {@link Polylist }
		     */
//			for(Object o : g.getMesh().getLinesAndLinestripsAndPolygons()) {
//				if(o instanceof Trifans) {nm.putAll(mapTrifans((Trifans)o));}
//			    if(o instanceof Polygons) {nm.putAll(mapPolygons((Polygons)o));}
//			    if(o instanceof Linestrips) {nm.putAll(mapLinestrips((Linestrips)o));}
//			    if(o instanceof Triangles) {nm.putAll(mapTriangles((Triangles)o));}
//			    if(o instanceof Lines) {nm.putAll(mapLines((Lines)o));}
//			    if(o instanceof Tristrips) {nm.putAll(mapTristrips((Tristrips)o));}
//			    if(o instanceof Polylist) {nm.putAll(mapPolylist((Polylist)o));}
//			}

			LOG.t("Mapping Vertices: %s",g.getMesh().getVertices().getId());
			nm.put(g.getMesh().getVertices().getId(), g.getMesh().getVertices());
		}
		
		return nm;
	}
	
//	private Map<String,Object> mapTrifans(Trifans o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		
//		nm.put(, value)
//		
//		return nm;
//	}
//
//	private Map<String,Object> mapPolygons(Polygons o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		
//		return nm;
//	}
//
//	private Map<String,Object> mapLinestrips(Linestrips o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		return nm;
//	}
//
//	private Map<String,Object> mapTriangles(Triangles o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		return nm;
//	}
//
//	private Map<String,Object> mapLines(Lines o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		
//		return nm;
//	}
//
//	private Map<String,Object> mapTristrips(Tristrips o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		
//		return nm;
//	}
//
//	private Map<String,Object> mapPolylist(Polylist o) {
//		LOG.trace(String.format("Mapping: %s",o.getName()));
//		
//		Map<String,Object> nm = new HashMap<String,Object>();
//		
//		return nm;
//	}

	public static String trimUri(String url) {
		if(url == null) return null;
		return url.substring(url.indexOf(POUND) + 1);
	}
	
}
