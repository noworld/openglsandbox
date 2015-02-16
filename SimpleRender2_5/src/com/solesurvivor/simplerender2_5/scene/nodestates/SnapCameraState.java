package com.solesurvivor.simplerender2_5.scene.nodestates;

import com.solesurvivor.simplerender2_5.scene.NodeImpl;
import com.solesurvivor.simplerender2_5.scene.NodeState;
import com.solesurvivor.util.math.Vec3;

public class SnapCameraState implements NodeState<NodeImpl> {
	
	private static final float ROTATION_SPEED = 3.0f; //3 degrees per frame
	private static final float STOP_SENSITIVITY = ROTATION_SPEED + 0.01f;
	private static final Vec3 AXES = new Vec3(0.0f, 1.0f, 0.0f); //Rotate on the Y (up) axis
	
	protected Vec3 previousPos; //Previous position
	protected float targetA; //Target angle to rotate to

	@Override
	public void enter(NodeImpl target) {
		if(target == null) {
			return;
		}
		
		//This is the object's current position
		//and rotation in the world
		float[] worldMat = target.getWorldMatrix();
		
		//start off with the current position
		previousPos = new Vec3(worldMat[14], worldMat[13], worldMat[12]);
	}

	@Override
	public void execute(NodeImpl target) {		
		if(target == null) {
			return;
		}
		
		//This is the object's current position
		//and rotation in the world
		float[] worldMat = target.getWorldMatrix();
		
		//Pull the current position out of the matrix
		Vec3 currentPos = new Vec3(worldMat[14], worldMat[13], worldMat[12]);
		
		//Make a copy of the previous position
		Vec3 compareVec = previousPos.clone();
		
		//Subtract the current position from the
		//previous to find the direction we are heading
		compareVec.subtract(currentPos);

		//If the vector has a magnitude greater than 0
		//then we are moving and go ahead and update the
		//target angle with the direction we are heading
		//If not, then we will just keep the last heading
		//we were given and continue to rotate towards that
		if(compareVec.getMagSq() > 0.0) {
			//Find the target angle from the vector
			targetA = (float)Math.toDegrees(Math.atan2(compareVec.getZ(), compareVec.getX()));
		}
		
		//Find the current facing angle from the world matrix
		float facingA = -(float)Math.toDegrees(Math.atan2(worldMat[2], worldMat[0]));

		//This is how far we are going to turn
		//and the direction based on if the value
		//is positive or negative
		float diff = targetA - facingA;
		
		//We want to turn the shortest distance
		//So if we are turning more than 180
		//degrees, subtract 360 from this number
		//to turn the other way
		if(diff > 180.0f) {
			diff = diff - 360.0f;
		} else if (diff < -180.0f) {
			//Handle the opposite case
			diff = diff + 360.0f;
		}

		//We will ignore any direction changes
		//until they are about within the ROTATION SPEED angle
		//This needs to be more than the ROTATION_SPEED
		//or the entity will oscillate around the direction.
		if(Math.abs(diff) > STOP_SENSITIVITY) {
			//Choose which way to turn based on 
			//if the difference was positive or
			//negative
			if(diff > 0.0f) {
				target.rotate(ROTATION_SPEED, AXES);
			} else {
				target.rotate(-ROTATION_SPEED, AXES);
			}			
		}

		//Only if the position is changing.
		if(compareVec.getMagSq() > 0.0) {
			//If we are moving, save the 
			//position change
			previousPos = currentPos;
		}
	}

	@Override
	public void exit(NodeImpl target) {
		// TODO Auto-generated method stub
	}

}
