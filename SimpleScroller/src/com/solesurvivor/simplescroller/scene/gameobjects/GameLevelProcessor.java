package com.solesurvivor.simplescroller.scene.gameobjects;

import android.graphics.Point;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.solesurvivor.simplescroller.game.messaging.GameMessage;
import com.solesurvivor.simplescroller.rendering.DrawingConstants;
import com.solesurvivor.simplescroller.rendering.RendererManager;
import com.solesurvivor.simplescroller.rendering.ScrollerRenderer;
import com.solesurvivor.simplescroller.rendering.ShaderManager;
import com.solesurvivor.simplescroller.rendering.TextureManager;
import com.solesurvivor.simplescroller.scene.Rectangle;
import com.solesurvivor.simplescroller.scene.water.WaterAnimationState;
import com.solesurvivor.util.logging.SSLog;
import com.solesurvivor.util.math.Vec3;

public class GameLevelProcessor extends Rectangle {

	private static final String TAG = GameLevelProcessor.class.getSimpleName();
	private static final String TEXTURE_NAME = "bit_water";
	private static final float WATER_SIZE = 150.0f;	
	private static final Vec3 PUSHBACK = new Vec3(0, 0, -5.0f);
	private static final Vec3 WATER_SCALE = new Vec3(WATER_SIZE, WATER_SIZE, 0.0f);

	protected boolean dirty = true;
	protected int texture;
	protected int shader;
	protected float[] gridMatrix;
	protected ScrollerRenderer ren;
	protected double totalTrans = 0;
	protected int scrollRow = 0;
	protected int numRows = -1;
	protected int numColumns = -1;
	protected GameLevel currentLevel;
	

	public GameLevelProcessor(Point dim) {
		super(dim);
		ren = RendererManager.getRenderer();
		shader = ShaderManager.getShaderId("twodee_shader");
		texture = TextureManager.getTextureId(TEXTURE_NAME,"uvgrid");
		this.scale(WATER_SCALE);
		this.translate(getTransalation());
		this.changeState(new WaterAnimationState());		
		gridMatrix = createGridMatrix();
//		JsonReader reader = new JsonReader(in)

	}	

	protected Vec3 getTransalation() {
		Point view = RendererManager.getViewport();
		Vec3 trans = new Vec3(-view.x/2, -view.y/2, 0);
		trans.add(PUSHBACK);
		return trans;
	}

	public float[] getGridMatrix() {
		return gridMatrix;
	}

	@Override
	public void receive(GameMessage message) {
		SSLog.d(TAG, "Received message: %s at %s", message.getData(), SystemClock.uptimeMillis());
	}

	@Override
	public int getShaderHandle() {
		return shader;
	}

	@Override
	public int getTextureHandle() {
		return texture;
	}

	@Override
	public int getElementOffset() {
		return 0;
	}

	@Override
	public void render() {
		ren.drawGameLevel(this);
		super.render();
	}
	
	public void translateAnimation(Vec3 trans) {
		totalTrans += trans.getY();
		double watersz = WATER_SIZE * 2.0;
		if(totalTrans > watersz) {
			super.translate(new Vec3(0.0f,(float)-totalTrans,0.0f));
			totalTrans = totalTrans % watersz;
			super.translate(new Vec3(0.0f,(float)totalTrans,0.0f));
		} else if(totalTrans < -watersz) {
			super.translate(new Vec3(0.0f,(float)-totalTrans,0.0f));
			totalTrans = totalTrans % watersz;
			super.translate(new Vec3(0.0f,(float)totalTrans,0.0f));
		}else {
			super.translate(trans);
		}
	}

	@Override
	protected void recalcMatrix() {
		Matrix.multiplyMM(worldMatrix, 0, transMatrix, 0, scaleMatrix, 0);
	}

	protected float[] createGridMatrix() {

		Point view = RendererManager.getViewport();

		numRows = (view.x / ((int)WATER_SIZE));
		numColumns = (view.y / ((int)WATER_SIZE));

		int size =  numRows * numColumns;
		
		SSLog.d(TAG, "Grid Size (RxC): %s x %s", numRows, numColumns);

		float[] matrix = new float[DrawingConstants.MATRIX_SIZE * size];

		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numColumns; j++) {
				int index = (i * numColumns * DrawingConstants.MATRIX_SIZE) + (j * DrawingConstants.MATRIX_SIZE);
				Matrix.setIdentityM(matrix, index);
				float x = i * WATER_SIZE * 2.0f;
				float y = j * WATER_SIZE * 2.0f;
				Matrix.translateM(matrix, index, x, y, 0);
			}
		}

		return matrix;
	}
	
}
