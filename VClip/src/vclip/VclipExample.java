package vclip;

import vclip.*;
import javax.vecmath.*;
import java.util.HashMap;

class VclipExample
{
	public static void main (String[] args) 
	 {
	   try
	    { HashMap library = new HashMap();
	      PolyTree.scanLibrary ("PolyTreeExamples.txt", library, true);

	      PolyTree ptree1 = (PolyTree)library.get("unit-cube");
	      PolyTree ptree2 = (PolyTree)library.get("cone");

	      DistanceReport drep = new DistanceReport();
	      ClosestFeaturesHT ht = new ClosestFeaturesHT();

	      Matrix4d X21 = new Matrix4d();
	      for (double x=10; x>=0; x-=1)
	       { X21.set (new Vector3d (x, 0, 0));
		 double dist = ptree1.vclip (drep, ptree2, X21, 0, ht);
		 if (dist > 0)
		  { System.out.println (dist);
		  }
		 else
		  { System.out.println ("colliding"); 
		  }
	       }
	    }
	   catch (Exception e) 
	    { e.printStackTrace();
	      System.exit(1);
	    }
	 }
}
