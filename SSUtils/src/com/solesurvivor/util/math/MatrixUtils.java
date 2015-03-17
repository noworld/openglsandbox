package com.solesurvivor.util.math;

public class MatrixUtils {

	public void interpolateMatrix(float[] mat1, float[] mat2, float dt) {
		
//		void MD5Animation::InterpolateSkeletons( FrameSkeleton& finalSkeleton, const FrameSkeleton& skeleton0, const FrameSkeleton& skeleton1, float fInterpolate )
//		{
//		    for ( int i = 0; i < m_iNumJoints; ++i )
//		    {
//		        SkeletonJoint& finalJoint = finalSkeleton.m_Joints[i];
//		        glm::mat4x4& finalMatrix = finalSkeleton.m_BoneMatrices[i];
//		 
//		        const SkeletonJoint& joint0 = skeleton0.m_Joints[i];
//		        const SkeletonJoint& joint1 = skeleton1.m_Joints[i];
//		 
//		        finalJoint.m_Parent = joint0.m_Parent;
//		 
//		        finalJoint.m_Pos = glm::lerp( joint0.m_Pos, joint1.m_Pos, fInterpolate );
//		        finalJoint.m_Orient = glm::mix( joint0.m_Orient, joint1.m_Orient, fInterpolate );
//		 
//		        // Build the bone matrix for GPU skinning.
//		        finalMatrix = glm::translate( finalJoint.m_Pos ) * glm::toMat4( finalJoint.m_Orient );
//		    }
//		}
	}
}
