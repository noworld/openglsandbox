package com.solesurvivor.simplescroller.scene.water;

import java.util.List;

import android.graphics.Point;
import android.opengl.Matrix;

import com.solesurvivor.simplescroller.rendering.BaseRenderer;
import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.simplescroller.rendering.ShaderManager;
import com.solesurvivor.simplescroller.rendering.TextureManager;
import com.solesurvivor.simplescroller.scene.Drawable;
import com.solesurvivor.simplescroller.scene.Light;
import com.solesurvivor.simplescroller.scene.Node;
import com.solesurvivor.util.SSArrayUtil;
import com.solesurvivor.util.math.Vec3;

public class Water implements Node {

	
	float[] verts;
	int datHandle;
	int idxHandle;
	protected short[] indexes = {0,2,1,1,2,3};
	protected boolean dirty = true;
	protected float[] matrix;
	protected Point dim;
	
	protected int texture;
	protected int shader;
	protected BaseRenderer ren;
	protected Drawable draw;
	
	public Water(Point dimensions) {
		this.dim = dimensions;
		matrix = new float[dim.x * dim.y * 16];
		float aspectWidth = ((float)dim.x)/((float)dim.y);
		verts = new float[]{
				/*vvv*/-aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,1.0f,
				/*vvv*/ aspectWidth,  1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,1.0f,
				/*vvv*/-aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 1.0f,0.0f,
				/*vvv*/ aspectWidth, -1.0f, 0.0f, /*nnn*/ 0.0f,0.0f,-1.0f, /*cc*/ 0.0f,0.0f};
		ren = RendererManager.getRenderer();
		datHandle = ren.loadToVbo(SSArrayUtil.floatToByteArray(verts));
		idxHandle = ren.loadToIbo(SSArrayUtil.shortToByteArray(indexes));
		texture = TextureManager.getTextureId("waves");
		shader = ShaderManager.getShaderId("tex_shader");
		Matrix.setIdentityM(matrix, 0);
		draw = new Drawable(){
			
			protected int posSize = 3;
			protected int nrmSize = 3;
			protected int txcSize = 2;
			protected int numElements = 6;
			protected int elementStride = 32;
			protected int posOffset = 0;
			protected int nrmOffset = 12;
			protected int txcOffset = 24;
			protected int shaderHandle = shader;
			protected int textureHandle = texture;
			protected int datBufHandle = datHandle;
			protected int idxBufHandle = idxHandle;
			
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
				return datBufHandle;
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
				return 0;
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
				return null;
			}

			@Override
			public List<Light> getLights() {
				return null;
			}
			
		};
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		ren.drawGeometry(draw, matrix);	
	}

	@Override
	public void scale(Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(float angle, Vec3 axes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void translate(Vec3 trans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChild(Node n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getWorldMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getTransMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

}
