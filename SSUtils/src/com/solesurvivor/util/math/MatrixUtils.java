package com.solesurvivor.util.math;

import android.util.Log;


public class MatrixUtils {

	@SuppressWarnings("unused")
	private static final String TAG = MatrixUtils.class.getSimpleName();

	public static String getMatrixString(float[] mat) {
		StringBuilder sb = new StringBuilder("4x4 Matrix: <");

		for(int i = 0; i < 16; i++) {
			if(i % 4 == 0){sb.append(">\n<");}
			sb.append(String.format("%2f", mat[i])).append(", ");
		}
		sb.append(">\n");
		return sb.toString();
	}

	public static float[] lerpMatrix(float[] m1, float[] m2, float dt) {

		//		Log.d(TAG, String.format("START MATRIX: %s", getMatrixString(m1)));
		//		Vec4 quat = matToQuaternion(m1);
		//		Log.d(TAG, String.format("QUATERNION: %s", quat.prettyString()));
		//		float[] newMatrix = quatToMatrix(quat);
		//		System.arraycopy(m1, 12, newMatrix, 12, 4);
		//		Log.d(TAG, String.format("FINAL MATRIX: %s", getMatrixString(newMatrix)));


		//Interpolate the rotation					
		float[] pos = lerpPosition(m1, m2, dt);
		float[] lm = quatToMatrix(slerpQuaternion(matToQuaternion(m1), matToQuaternion(m2), dt));
		System.arraycopy(pos, 0, lm, 12, pos.length);

		return lm;
	}

	public static float[] lerpPosition(float[] m1, float[] m2, float dt) {

		float lx = (m2[12] * dt) + (m1[12] * (1.0f - dt));
		float ly = (m2[13] * dt) + (m1[13] * (1.0f - dt));
		float lz = (m2[14] * dt) + (m1[14] * (1.0f - dt));
		float lw = (m2[15] * dt) + (m1[15] * (1.0f - dt));

		return new float[]{lx,ly,lz,lw};

	}
	

	public static Vec4 slerpQuaternion(Vec4 q1, Vec4 q2, float dt) {
		
		float dot = VectorMath.compDot(q1, q2);

		if (dot < 0) {
			q2.negate();
			dot = -dot;
		}

		float remain = 1.0f - dt;
		double lx=0,ly=0,lz=0,lw=0;

		if(dot >= 0.95f) {
			lx = (q1.getX() * remain) + (q2.getX() * dt);
			ly = (q1.getY() * remain) + (q2.getY() * dt);
			lz = (q1.getZ() * remain) + (q2.getZ() * dt);
			lw = (q1.getW() * remain) + (q2.getW() * dt);
		} else if(dot <= -0.99f) {
			lx = 0.5f * (q1.getX() + q2.getX());
			ly = 0.5f * (q1.getY() + q2.getY());
			lz = 0.5f * (q1.getX() + q2.getZ());
			lw = 0.5f * (q1.getW() + q2.getW());
		} else {
			double sintheta = Math.sqrt(1.0 - (dot * dot));
			double theta = Math.acos(dot);
			double scaleA = Math.sin(remain * theta) / sintheta;
			double scaleB = Math.sin(dt * theta) / sintheta;
			
			lx = (q1.getX() * scaleA) + (q2.getX() * scaleB);
			ly = (q1.getY() * scaleA) + (q2.getY() * scaleB);
			lz = (q1.getZ() * scaleA) + (q2.getZ() * scaleB);
			lw = (q1.getW() * scaleA) + (q2.getW() * scaleB);
			
		}
		
		return new Vec4((float)lx, (float)ly, (float)lz, (float)lw);
	}

	public static Vec4 matToQuaternion(float[] mat) {

		double tr = mat[0] + mat[5] + mat[10];
		double qx=0,qy=0,qz=0,qw=0;

		if (tr > 0) { 
			double w = Math.sqrt(tr+1.0) * 2; // S=4*qw 
			qw = 0.25 * w;
			qx = (mat[9] - mat[6]) / w;
			qy = (mat[2] - mat[8]) / w; 
			qz = (mat[4] - mat[1]) / w; 
		} else if ((mat[0] > mat[5])&(mat[0] > mat[10])) { 
			double w = Math.sqrt(1.0 + mat[0] - mat[5] - mat[10]) * 2; // S=4*qx 
			qw = (mat[9] - mat[6]) / w;
			qx = 0.25 * w;
			qy = (mat[1] + mat[4]) / w; 
			qz = (mat[2] + mat[8]) / w; 
		} else if (mat[5] > mat[10]) { 
			double w = Math.sqrt(1.0 + mat[5] - mat[0] - mat[10]) * 2; // S=4*qy
			qw = (mat[2] - mat[8]) / w;
			qx = (mat[1] + mat[4]) / w; 
			qy = 0.25 * w;
			qz = (mat[6] + mat[9]) / w; 
		} else { 
			double w = Math.sqrt(1.0 + mat[10] - mat[0] - mat[5]) * 2; // S=4*qz
			qw = (mat[4] - mat[1]) / w;
			qx = (mat[2] + mat[8]) / w;
			qy = (mat[6] + mat[9]) / w;
			qz = 0.25 * w;
		}

		return new Vec4((float)qx,(float)qy,(float)qz,(float)qw);
	}

	public static float[] quatToMatrix(Vec4 q){
		double sqw = q.w*q.w;
		double sqx = q.x*q.x;
		double sqy = q.y*q.y;
		double sqz = q.z*q.z;

		float[] mat = new float[16];	    

		double invs = 1 / (sqx + sqy + sqz + sqw);
		mat[0] = (float)(( sqx - sqy - sqz + sqw)*invs);
		mat[5] = (float)((-sqx + sqy - sqz + sqw)*invs);
		mat[10] = (float)((-sqx - sqy + sqz + sqw)*invs);

		double tmp1 = q.x*q.y;
		double tmp2 = q.z*q.w;
		mat[4] = (float)(2.0 * (tmp1 + tmp2)*invs);
		mat[1] = (float)(2.0 * (tmp1 - tmp2)*invs);

		tmp1 = q.x*q.z;
		tmp2 = q.y*q.w;
		mat[8] = (float)(2.0 * (tmp1 - tmp2)*invs);
		mat[2] = (float)(2.0 * (tmp1 + tmp2)*invs);
		tmp1 = q.y*q.z;
		tmp2 = q.x*q.w;
		mat[9] = (float)(2.0 * (tmp1 + tmp2)*invs);
		mat[6] = (float)(2.0 * (tmp1 - tmp2)*invs);

		return mat;
	}
}
