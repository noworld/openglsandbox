package com.solesurvivor.simplescroller.scene;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.solesurvivor.simplescroller.game.messaging.GameMessage;
import com.solesurvivor.simplescroller.game.messaging.GameMessageEnum;
import com.solesurvivor.simplescroller.game.messaging.MessageReceiver;
import com.solesurvivor.util.math.Vec3;

public class Geometry implements Drawable, MessageReceiver {

	@SuppressWarnings("unused")
	private static final String TAG = Geometry.class.getSimpleName();
	
	protected String name;
	
	protected int shaderHandle;
	protected int dataBufHandle;
	protected int idxBufHandle;
	
	protected int posSize;
	protected int nrmSize;
	protected int txcSize;
	
	protected int posOffset;
	protected int nrmOffset;
	protected int txcOffset;
	
	protected int numElements;
	protected int elementStride;
	protected int elementOffset = 0;
	
	protected float[] worldMatrix;
	protected int textureHandle;
	protected List<Light> lights;
	
	protected Vec3 transDir;
	protected Vec3 scaleFac;
	protected Vec3 rotAxes;
	protected float rotAngle;	
	
	protected boolean mDirty = true;
	
	protected Geometry() {
		super();
		worldMatrix = new float[16];		
		lights = new ArrayList<Light>();
		resetTransforms();
	}
	
	public Geometry(Geometry geo) {
		this();
		this.name = geo.name;
		this.shaderHandle = geo.shaderHandle;
		this.dataBufHandle = geo.dataBufHandle;
		this.idxBufHandle = geo.idxBufHandle;
		this.posSize = geo.posSize;
		this.nrmSize = geo.nrmSize;
		this.txcSize = geo.txcSize;
		this.posOffset = geo.posOffset;
		this.nrmOffset = geo.nrmOffset;
		this.txcOffset = geo.txcOffset;
		this.numElements = geo.numElements;
		this.elementStride = geo.elementStride;
		this.textureHandle = geo.textureHandle;
	}

	public Geometry(String name, int shaderHandle, int dataBufHandle, int idxBufHandle,
			int posSize, int nrmSize, int txcSize, int posOffset,
			int nrmOffset, int txcOffset, int numElements, int elementStride,
			int textureHandle) {
		this();
		this.name = name;
		this.shaderHandle = shaderHandle;
		this.dataBufHandle = dataBufHandle;
		this.idxBufHandle = idxBufHandle;
		this.posSize = posSize;
		this.nrmSize = nrmSize;
		this.txcSize = txcSize;
		this.posOffset = posOffset;
		this.nrmOffset = nrmOffset;
		this.txcOffset = txcOffset;
		this.numElements = numElements;
		this.elementStride = elementStride;
		this.textureHandle = textureHandle;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int getShaderHandle() {
		return shaderHandle;
	}

	@Override
	public int getTextureHandle() {
		return textureHandle;
	}

	@Override
	public int getDatBufHandle() {
		return dataBufHandle;
	}

	@Override
	public int getIdxBufHandle() {
		return idxBufHandle;
	}

	@Override
	public int getPosSize() {
		return posSize;
	}

	@Override
	public int getNrmSize() {
		return nrmSize;
	}

	@Override
	public int getTxcSize() {
		return txcSize;
	}

	@Override
	public int getNumElements() {
		return numElements;
	}

	@Override
	public int getElementStride() {
		return elementStride;
	}
	
	@Override
	public int getElementOffset() {
		return elementOffset;
	}

	@Override
	public int getPosOffset() {
		return posOffset;
	}

	@Override
	public int getNrmOffset() {
		return nrmOffset;
	}

	@Override
	public int getTxcOffset() {
		return txcOffset;
	}

	@Override
	public float[] getWorldMatrix() {
		if(mDirty) {
			applyTransforms();
		}
		return worldMatrix;
	}
	
	public void addLight(Light l) {
		lights.add(l);
	}

	@Override
	public List<Light> getLights() {
		return lights;
	}
	
	public void setPosOffset(int offset) {
		this.posOffset = offset;
	}
	
	public void setNrmOffset(int offset) {
		this.nrmOffset = offset;
	}
	
	public void setTxcOffset(int offset) {
		this.txcOffset = offset;
	}
	
	public void setElementOffset(int offset) {
		this.elementOffset = offset;
	}
	
	public void setNumElements(int numElements) {
		this.numElements = numElements;
	}
	
	public void rotate(float angle, Vec3 dir) {
		if(rotAxes == null) {
			rotAxes = dir.normalizeClone();
		} else {
			rotAxes.add(dir);
			rotAxes.normalize();
		}
		
		rotAngle += angle;
		mDirty = true;
	}
	
	public void translate(Vec3 dir) {
		transDir.add(dir);
		mDirty = true;
	}
	
	public void scale(Vec3 fac) {
		scaleFac.componentScale(fac);
		mDirty = true;
	}
	
	public void resetTransforms() {
		Matrix.setIdentityM(worldMatrix, 0);
		transDir = new Vec3(0.0f,0.0f,0.0f);
		scaleFac = new Vec3(1.0f,1.0f,1.0f);
		rotAxes = null;
		rotAngle = 0.0f;	
		mDirty = false;
	}
	
	protected void applyTransforms() {
		Matrix.setIdentityM(worldMatrix, 0);		
		Matrix.translateM(worldMatrix, 0, transDir.getX(), transDir.getY(), transDir.getZ());
		if(rotAxes != null) {
			Matrix.rotateM(worldMatrix, 0, rotAngle, rotAxes.getX(), rotAxes.getY(), rotAxes.getZ());
		}
		Matrix.scaleM(worldMatrix, 0, scaleFac.getX(), scaleFac.getY(), scaleFac.getZ());

		mDirty = false;
	}

	@Override
	public void receive(GameMessage message) {
		if(message.getMessage().equals(GameMessageEnum.RESET_TRANSFORMS)) {
			this.resetTransforms();
			if(message.getData() != null && message.getData() instanceof Vec3) {
				this.translate((Vec3)message.getData());
			}
		}
	}

}
