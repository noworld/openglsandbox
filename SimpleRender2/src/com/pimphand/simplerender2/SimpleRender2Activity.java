package com.pimphand.simplerender2;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SimpleRender2Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_render2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simple_render2, menu);
		return true;
	}

}
