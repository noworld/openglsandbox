package com.pimphand.simplerender2.scene;

import java.util.ArrayList;
import java.util.List;

import com.pimphand.simplerender2.input.InputHandler;
import com.pimphand.simplerender2.text.Cursor;

public class GameObjectLibrary {

	public List<GameEntity> mEntities = new ArrayList<GameEntity>();
	public List<InputHandler> mInputHandlers = new ArrayList<InputHandler>();
	public List<UiElement> mDisplayElements = new ArrayList<UiElement>();
	public List<Light> mLights = new ArrayList<Light>();
	public List<Cursor> mCursors = new ArrayList<Cursor>();
	
}
