package com.solesurvivor.simplerender2_5.scene.animation;

import java.util.HashMap;
import java.util.Map;

public class PoseLibrary {
	
	protected String name;
	protected Pose restPose;
	protected Pose restPoseInv;
	protected Map<String,Pose> animPoses;
	
	public PoseLibrary(String name, Pose restPose, Pose resPoseInv) {
		this.name = name;
		this.restPose = restPose;
		this.restPoseInv = resPoseInv;
		animPoses = new HashMap<String,Pose>();
	}
	
	public String getName() {
		return name;
	}
	
	public Pose getRestPose() {
		return restPose;
	}
	
	public Pose getRestPoseInv() {
		return restPoseInv;
	}
	
	public void addPose(String name, Pose pose) {
		animPoses.put(name, pose);
	}
	
	public Pose getPose(String name) {
		return animPoses.get(name);
	}
	
	public Armature getInterpolatedPose(String pose1, String pose2, float dt) {
		return null;
	}
	
}
