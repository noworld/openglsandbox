package com.solesurvivor.simplerender2_5;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SimpleRender25Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_render25);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simple_render25, menu);
		return true;
	}

}
