//
// Based on the vclip C++ struct VertConeNode.
// See the file COPYRIGHT for copyright information.
//

package vclip;

import javax.vecmath.*;

/**
 * Represents a vertex cone element associated with a specific
 * edge adjacent to a vertex.
 *
 * @author Brian Mirtich (C++ version)
 * @author <a href="http://www.cs.ubc.ca/~eddybox">Eddy Boxerman</a>
 * @see <a href="{@docRoot}/copyright.html">Copyright information</a>
 */
class VertexConeNode
{
	/* Instance Variables */

	VertexConeNode next;
	public Plane plane;
	public Edge nbr; // neighbouring edge when plane violated
	
	/* Methods */

	/**
	 * Returns a string representation of this object.
	 */
	public final String toString()
	 {
	   return new String("plane: " +
			     plane.toString() +
			     ", nbr = [" +
			     nbr.toString() + "]");
	 }
}
