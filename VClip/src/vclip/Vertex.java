//
// Based on the vclip C++ struct Vertex
// See the file COPYRIGHT for copyright information.
//

package vclip;

import javax.vecmath.*;
import java.util.Vector;

/**
 * A vertex of a polyhedron.
 *
 * @author Brian Mirtich (C++ version)
 * @author <a href="http://www.cs.ubc.ca/~eddybox">Eddy Boxerman</a>
 * @author <a href="http://www.cs.ubc.ca/~lloyd">John E. Lloyd</a> (Java port)
 * @see <a href="{@docRoot}/copyright.html">Copyright information</a>
 */
public class Vertex extends Feature
{

	/* Instance Variables */

	protected Point3dX coords;
	protected Vector cone; // list of VertexConeNode objects
	VertexConeNode coneNode0;

	/* Methods */

	/** Default Constructor */
	Vertex()
	 { 
	   this("");
	   this.type = VERTEX;
	 }

	/** Constructor */
	Vertex(String name)
	 { 
	   this.setName(name);
	   this.type = VERTEX;
	   coords = new Point3dX();
	   cone   = new Vector();
	 }

	/**
	 * Constructs a Vertex with a given name and coordinates.
	 *
	 * @param name vertex name
	 * @param p coordinate values
	 */
	public Vertex(String name, Point3d p)
	 { 
	   this.setName(name);
	   this.type = VERTEX;
	   coords = new Point3dX(p);
	   cone   = new Vector();
	 }

	/**
	 * Constructs a Vertex with a given name and coordinate
	 * values.
	 *
	 * @param name vertex name
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public Vertex(String name, double x, double y, double z)
	 { 
	   this.setName(name);
	   this.type = VERTEX;
	   coords = new Point3dX(x, y, z);
	   cone   = new Vector();
	 }

	/**
	 * Sets the coordinates of this vertex.
	 *
	 * @param coords coordinates of this vertex
	 */
	public void setCoords(Point3d coords)
	 { 
	   this.coords = new Point3dX(coords);
	 }

	/**
	 * Gets the coordinates of this vertex.
	 *
	 * @return coordinates of this vertex
	 */
	public Point3d getCoords()
	 {
	   return coords;
	 }

	/**
	 * Produces a string representation of this vertex.
	 *
	 * @return string representation
	 */
	public final String toString()
	 { 
	   return ("vertex: "+name+": "+coords.toString());
	 }

	public Feature promote (Vector3d nrm, double angtol)
	 { 
	   Edge e1 = null;  /* 1st edge which is nearly parallel to plane */
	   Edge e2 = null;  /* 2nd edge which is nearly parallel to plane */

	   double minAbsDot = Double.POSITIVE_INFINITY;
	   Edge minEdge = null;

	   for (VertexConeNode vcn=coneNode0; vcn!=null; vcn=vcn.next)
	    { double absDot = Math.abs(vcn.nbr.dir.dot(nrm));
	      if (absDot < minAbsDot)
	       { minEdge = vcn.nbr;
		 minAbsDot = absDot;
	       }
	    }
	   // minAbsDot is the absolute value of the cosine of the angle
	   // between the edge and the normal. If the feature is to be
	   // promoted, then this angle should be close to +/- PI/2, and we can
	   // use the approximation
	   //
	   // cos (PI/2 + e) = -e for small e
	   //
	   if (minAbsDot <= angtol)
	    { return minEdge.promote (nrm, angtol);
	    }
	   else
	    { return this;
	    }
	 }
}
