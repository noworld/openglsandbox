package com.solesurvivor.simplerender2_5.scene;

import java.util.HashMap;
import java.util.Map;

import com.solesurvivor.simplerender2_5.game.GameWorld;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class PathAnimation extends NodeImpl {

	protected Curve path;
	protected double currentFrame = 0;
	protected Map<NodeImpl,Float> frameOffsets;
	protected boolean loop = true;
	protected long totalTime = 3000;
	protected boolean drawPath = false;
	protected Long startTime;
	protected int segments;
	
	public PathAnimation(Curve path, long totalTime) {
		this.path = path;
		this.totalTime = totalTime;
		frameOffsets = new HashMap<NodeImpl,Float>();
		startTime = GameWorld.inst().getGameT();
		segments = path.getPoints().length / path.getDataSize();
	}
	
	public void setDrawPath(boolean draw) {
		this.drawPath = draw;
	}
	
	public boolean isDrawPath() {
		return drawPath;
	}
	
	public void setPath(Curve path) {
		this.path = path;
	}
	
	public Curve getPath() {
		return path;
	}
	
	public void addChild(NodeImpl n, float frameOffset) {
		children.add(n);
		frameOffsets.put(n, frameOffset);
	}
	
	@Override
	public void addChild(Node n) {		
		children.add(n);		
		frameOffsets.put((NodeImpl)n, 0.0f);
	}
	
	public void removeChild(Node n) {
		children.remove(n);
		frameOffsets.remove(n);
	}
	
	@Override
	public void update() {
		if(mDirty || (parent != null && parent.isDirty())) {
			recalcMatrix();
			this.mDirty = true;
		}		
		
		currentFrame = calcCurrentFrame();

		for(Node n : children) {
			if(n instanceof AnimatedNodeImpl) {
				AnimatedNodeImpl ani = (AnimatedNodeImpl)n;
				ani.resetAnimation();
				double frameOffset = frameOffsets.get(ani)  + currentFrame;
				Vec3 pos = getPosForOffset(frameOffset);
				ani.translateAnimation(pos);
				ani.rotateAnimation(90.0f, new Vec3(0,0,1.0f));
				ani.rotateAnimation(getZAngleForOffset(frameOffset), new Vec3(0,0,1.0f));
			}
			n.update();
			
		}
	}

	@Override
	public void render() {
		if(drawPath) {
			path.render();
		}
		renderChildren();
	}
	
	private double calcCurrentFrame() {
		long animT = GameWorld.inst().getGameT() - startTime;
		double deltaT = ((double)animT) / ((double)totalTime);
		double frame = deltaT * segments;
		return frame;
	}

	private Vec3 getPosForOffset(double frameOffset) {
		if(frameOffset >= segments) {
			frameOffset = frameOffset % segments;
		}
		
		int frame = (int)frameOffset * path.getDataSize();
		if(frame >= path.getPoints().length) {
			frame = frame - path.getPoints().length;
		}		
		Vec3 startPos = Vec3.fromFloatArray(path.getPoints(), frame);

		int destFrame = frame + path.getDataSize();
		if(destFrame >= path.getPoints().length) {
			destFrame = destFrame - path.getPoints().length;
		}
		Vec3 destPos = Vec3.fromFloatArray(path.getPoints(), destFrame);


		double pct = frameOffset - ((int)frameOffset);
		double inv = 1.0 - pct;

		float x = (float)((destPos.getX() * pct) + (startPos.getX() * inv));
		float y = (float)((destPos.getY() * pct) + (startPos.getY() * inv));
		float z = (float)((destPos.getZ() * pct) + (startPos.getZ() * inv));

		Vec3 pos = new Vec3(x, y, z);
		
		pos.add(path.getPosition());
		return pos;
	}
	

	private float getZAngleForOffset(double frameOffset) {
		if(frameOffset >= segments) {
			frameOffset = frameOffset % segments;
		}
		
		int frame = (int)frameOffset * path.getDataSize();
		if(frame >= path.getPoints().length) {
			frame = frame - path.getPoints().length;
		}		
		Vec3 startPos = Vec3.fromFloatArray(path.getPoints(), frame);

		int destFrame = frame + path.getDataSize();
		if(destFrame >= path.getPoints().length) {
			destFrame = destFrame - path.getPoints().length;
		}
		Vec3 destPos = Vec3.fromFloatArray(path.getPoints(), destFrame);
		
		float dx = destPos.getX() - startPos.getX();
		float dy = destPos.getY() - startPos.getY();
		
		return (float)Math.toDegrees(Math.atan2(dy,dx));
	}
	
}
