package com.fourears.equalizerapp;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.*;
import android.widget.LinearLayout;
import android.view.View;

public class MainActivity extends Activity {

	public SeekBar sb1,sb2,sb3,sb4,sb5;
	private Equalizer equaliz;
	private LinearLayout mLinearLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLinearLayout = new LinearLayout(this);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);

        setContentView(mLinearLayout);
		//setContentView(R.layout.activity_main);
		setupEqualizerAndUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void setupEqualizerAndUI(){
		//Create and attach a new Equalizer to the global audio stream
		equaliz = new Equalizer(0,0);
		equaliz.setEnabled(true);
		
		
		TextView eqTextView = new TextView(this);
        eqTextView.setText("Equalizer:");
        mLinearLayout.addView(eqTextView);

        short bands = equaliz.getNumberOfBands();      
        final short minEQLevel = equaliz.getBandLevelRange()[0];
        final short maxEQLevel = equaliz.getBandLevelRange()[1];
        
        for(short i=0; i<bands; i++){
        	final short band = i;
        	
        	//Setup view for frequency range
        	TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            freqTextView.setText((equaliz.getCenterFreq(band) / 1000) + " Hz");
            mLinearLayout.addView(freqTextView);
            
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            
            //Setup view for min Db
            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + " dB");
            
            //Set up view for max Db
            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            
            //Setup seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(equaliz.getBandLevel(band));
            
            //Add listeners for each seekbar
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                    equaliz.setBandLevel(band, (short) (progress + minEQLevel));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            
            row.addView(minDbTextView);
            row.addView(bar);
            row.addView(maxDbTextView);

            mLinearLayout.addView(row);
        }
        
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
