package com.fourears.equalizerapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.ActionBar;

public class MainActivity extends Activity implements Serializable {

	private Equalizer equaliz;
	private LinearLayout mLinearLayout;
	private LinearLayout saves_page;
	private File settingsFolder;
	private SeekBar[] bars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingsFolder = getDir("FourEars_Settings", Context.MODE_PRIVATE);
		
		mLinearLayout = new LinearLayout(this);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		setupEqualizerAndUI();

		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            setContentView(R.layout.activity_main);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void openConfigPage(View v) {
		setContentView(mLinearLayout);
	}

	private void setupEqualizerAndUI() {
		equaliz = new Equalizer(0, 0); // Create and attach a new Equalizer to
										// the global audio stream
		equaliz.setEnabled(true);

		TextView eqTextView = new TextView(this);
		eqTextView.setText("Equalizer:");
		mLinearLayout.addView(eqTextView);

		final short bands = equaliz.getNumberOfBands();
		final short minEQLevel = equaliz.getBandLevelRange()[0];
		final short maxEQLevel = equaliz.getBandLevelRange()[1];
		
		bars = new SeekBar[bands];

		for (short i = 0; i < bands; i++) {
			final short band = i;
			// Setup text for frequency range
			TextView freqTextView = new TextView(this);
			freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			freqTextView.setText((equaliz.getCenterFreq(band) / 1000) + " Hz");
			mLinearLayout.addView(freqTextView);

			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);

			// Setup text for min Db
			TextView minDbTextView = new TextView(this);
			minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			minDbTextView.setText((minEQLevel / 100) + " dB");

			// Setup text for max Db
			TextView maxDbTextView = new TextView(this);
			maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			maxDbTextView.setText((maxEQLevel / 100) + " dB");

			// Setup seekbar
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.weight = 1;
			/*SeekBar bar = new SeekBar(this);
			bar.setLayoutParams(layoutParams);
			bar.setMax(maxEQLevel - minEQLevel);
			bar.setProgress(equaliz.getBandLevel(band));*/
			
			bars[i] = new SeekBar(this);
			bars[i].setLayoutParams(layoutParams);
			bars[i].setMax(maxEQLevel - minEQLevel);
			bars[i].setProgress(equaliz.getBandLevel(band));

			// Add listeners for each seekbar
			bars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					equaliz.setBandLevel(band, (short) (progress + minEQLevel));
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});

			row.addView(minDbTextView);
			row.addView(bars[i]);
			row.addView(maxDbTextView);

			mLinearLayout.addView(row);
		}
		/* Creates a Save button */
		LinearLayout row = new LinearLayout(this);
		row.setOrientation(LinearLayout.HORIZONTAL);
		Button save;

		save = new Button(this);
		save.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		save.setText("Save Configuration");
		// Setup button listener (checks for if clicked.)
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveEqualizSetting(bands, equaliz);
			}
		});

		row.addView(save);
		mLinearLayout.addView(row);
	}

	private void loadEqualizSetting(File file) {
		String line = null;
		String[] temp = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i<bars.length; i++){
				line = reader.readLine();
				temp = line.split(" ");
				equaliz.setBandLevel(Short.parseShort(temp[1]), Short.parseShort(temp[0]));
				//Drawable t = bars[i].getThumb();
				//t.setLevel(Integer.parseInt(temp[0]));
				//bars[i].setThumb(t);
				//bars[i].setMax(0);
				//bars[i].setProgress(0);
				//bars[i].setMax(Short.parseShort(temp[0]));//equaliz.getBandLevelRange()[1] - equaliz.getBandLevelRange()[0]);
				bars[i].setProgress(Integer.parseInt(temp[0]));
				bars[i].updateThumb()
				//bars[i].onSizeChanged(bars[i].getWidth(),bars[i].getHeight(),0,0);
			}
			/*while((line = reader.readLine()) != null){
				temp = line.split(" ");
				equaliz.setBandLevel(Short.parseShort(temp[1]), Short.parseShort(temp[0]));
				
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		//openConfigPage(mLinearLayout);
		setContentView(mLinearLayout);
	}

	/* Grab a name from user and save it to the device */
	private void saveEqualizSetting(final short bands, Equalizer equalizer) {

		AlertDialog.Builder savePrompt = new AlertDialog.Builder(this);
		savePrompt.setTitle("Custom Preset Name");
		savePrompt.setMessage("Enter a name for the new custom preset.");

		// Setup text field for user input
		final EditText input = new EditText(this);
		savePrompt.setView(input);

		String s = "";
		// Define a string object to store equalizer band settings that is
		// separated by new line characters
		for (short i = 0; i < bands; i++) {
			final short band = i;
			final short bLevel = equalizer.getBandLevel(band);
			String newLine = System.getProperty("line.separator");
			s = s.concat(String.valueOf(bLevel)).concat(" ")
					.concat(String.valueOf(band).concat(newLine));
		}

		final String settings = s; // Creates a string object that can be saved.

		// Setup save button on prompt
		savePrompt.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String FILENAME = input.getText().toString();
						try {
							File saveFile = new File(getFilesDir(), FILENAME);
							FileOutputStream fos = new FileOutputStream(
									saveFile);
							fos.write(settings.getBytes());
							fos.close();
							// DEBUG: Uncomment below to check if save files
							// work.//
							/*
							 * if(saveFile.exists()){ throw new
							 * Exception("file found"); }
							 */
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}// End onClick
				});

		// Do Nothing.
		savePrompt.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancel
					}
				});

		savePrompt.show();
	}

	public void openSavePage(View v) {
		LinearLayout savesLayout = new LinearLayout(this);
		savesLayout.setOrientation(LinearLayout.VERTICAL);
		setContentView(savesLayout);
		setupSavesPage(savesLayout);
		// setContentView(R.layout.saves);
		/* saves_page = new LinearLayout(this); */
	}

	private void setupSavesPage(LinearLayout saves) {
		List<File> files = getListFiles(new File(getFilesDir().toString()));

		for (File file : files) {
			final File file_temp = file;
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);

			Button b = new Button(this);
			b.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			b.setText(file.getName());

			// Setup button listener
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					loadEqualizSetting(file_temp);
				}
			});

			row.addView(b);
			saves.addView(row);
		}
	}

	private List<File> getListFiles(File parentDir) {
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] files = parentDir.listFiles();
		for(File file : files) {
			if(file.isDirectory()) {
				inFiles.addAll(getListFiles(file));
			} else {
				inFiles.add(file);
			}
		}
		return inFiles;
	}

	public void openTutorialPage(View v) {
		setContentView(R.layout.tutorial);
	}

}
