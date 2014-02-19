package com.fourears.equalizerapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void openConfigPage(View v){
		setContentView(R.layout.configuration);
	}
	
	public void openSavePage(View v){
		setContentView(R.layout.saves);
	}
	
	public void openTutorialPage(View v){
		setContentView(R.layout.tutorial);
	}

}
