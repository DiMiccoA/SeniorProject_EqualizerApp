package com.fourears.equalizerapp;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	public SeekBar sb1,sb2,sb3,sb4,sb5;
	private Equalizer equaliz;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupEqualizerAndUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setupEqualizerAndUI(){
		equaliz = new Equalizer(0,0);
		equaliz.setEnabled(true);
		
		TextView eqTextView = new TextView(this);
        eqTextView.setText("Equalizer:");

        short bands = equaliz.getNumberOfBands();
        
        
        
	}


}
