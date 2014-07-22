package com.pimphand.simplerender2.fsm;

import android.content.Context;
import android.content.res.TypedArray;

import com.pimphand.simplerender2.R;
import com.pimphand.simplerender2.game.GameGlobal;
import com.pimphand.simplerender2.loading.GeometryLoader;
import com.pimphand.simplerender2.scene.GameObjectLibrary;
import com.pimphand.simplerender2.scene.GameWorld;

public class WaterWorldState implements State<GameWorld> {
	
	protected float[] mMVPMatrix = new float[16]; 
	protected GameObjectLibrary mObjectLibrary;
	
	public WaterWorldState() {
		Context ctx = GameGlobal.inst().getContext();
		
		TypedArray modelArray = ctx.getResources().obtainTypedArray(R.array.main_menu_models);
		
		this.mObjectLibrary = GeometryLoader.loadGameObjects(GameGlobal.inst().getContext(), modelArray);
		
		modelArray.recycle();
	}

	@Override
	public void enter(GameWorld target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(GameWorld target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(GameWorld target) {
		// TODO Auto-generated method stub

	}

	@Override
	public GameObjectLibrary getLibrary() {
		return mObjectLibrary;
	}

}
