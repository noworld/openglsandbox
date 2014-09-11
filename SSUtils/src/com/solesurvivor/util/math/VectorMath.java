package com.solesurvivor.util.math;


public class VectorMath {
	
	public static Float[] cross(Float[] p1, Float[] p2, Float[] p3) {
		Float[] cross = new Float[3];
		
		cross[0] = crossX(p1, p2, p3);
		cross[1] = crossY(p1, p2, p3);
		cross[2] = crossZ(p1, p2, p3);
		
		return cross;
		
	}
	
	public static float crossX(Float[] p1, Float[] p2, Float[] p3) {
		float crossX = 0.0f;
		
		//x = ay*bz - az*by
		crossX = ((p2[1]-p1[1]) * (p3[2]-p1[2])) - ((p2[2]-p1[2]) * (p3[1]-p1[1]));
		
		return crossX;
	}
	
	
	public static float crossY(Float[] p1, Float[] p2, Float[] p3) {
		float crossY = 0.0f;
		
		//y = az*bx - ax*bz
		crossY = ((p2[2]-p1[2]) * (p3[0]-p1[0])) - ((p2[0]-p1[0]) * (p3[2]-p1[2]));
		
		return crossY;
	}
	

	public static float crossZ(Float[] p1, Float[] p2, Float[] p3) {
		float crossZ = 0.0f;
		
		//z = ax*by - ay*bx
		crossZ = ((p2[0]-p1[0]) * (p3[1]-p1[1])) - ((p2[1]-p1[1]) * (p3[0]-p1[0]));
		
		return crossZ;
	}
	
	/**
	 * Computes a distance that can be used for comparison purposes.
	 * Distance is distance of P3 from line P1-P2.
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	public static float compDist(Float[] p1, Float[] p2, Float[] p3) {
		
		Float[] grade1 = new Float[] {
				p2[0]-p1[0],
				p2[1]-p1[1]
		};
		
		Float[] grade2 = new Float[] {
				p1[0]-p3[0],
				p1[1]-p3[1]
		};
	    
		return Math.abs((grade1[0]*grade2[1])-(grade1[1]*grade2[0]));
	}
	
	public static float compDot(Vec3 v1, Vec3 v2) {		
		return (v1.getX()*v2.getX()) + (v1.getY()*v2.getY()) + (v1.getZ()*v2.getZ());
	}
	
}
