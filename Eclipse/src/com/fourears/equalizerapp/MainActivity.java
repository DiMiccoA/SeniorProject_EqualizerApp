package com.fourears.equalizerapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ScrollView;
import android.content.Context;
import android.content.DialogInterface;
import android.media.audiofx.Equalizer;

public class MainActivity extends Activity {

	private Equalizer equaliz;
	private LinearLayout mLinearLayout;
	@SuppressWarnings("unused")
	private File settingsFolder;
	private SeekBar[] bars;
	@SuppressWarnings("unused")
	private RadioGroup rGroup;
	private RadioGroup group;
	public int id_count;
	private boolean check_flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingsFolder = getDir("FourEars_Settings", Context.MODE_PRIVATE);
		id_count = 0;
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
	        case R.id.action_back:
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
		/* Modified code from Android API examples */
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
			
			bars[i] = new SeekBar(this);
			bars[i].setLayoutParams(layoutParams);
			bars[i].setMax(maxEQLevel - minEQLevel);
			bars[i].setProgress(equaliz.getBandLevel(band));
			bars[i].setId(id_count++);

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
				//saveFile(bands, equaliz);
				
			}//End button click
		});

		row.addView(save);
		mLinearLayout.addView(row);
	}
	
	private void saveEqualizSetting(short bands, Equalizer equalizer){
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
					public void onClick(DialogInterface arg0, int arg1) {
					
					}
				});

		savePrompt.show();
	}

	@SuppressWarnings("resource")
	private void loadEqualizSetting(File file) {
		String line = null;
		String[] temp = null;
		final short minEQLevel = equaliz.getBandLevelRange()[0];
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			for(int i = 0; i<bars.length; i++){
				line = reader.readLine();
				temp = line.split(" ");
									//Band Number				Band Level
				equaliz.setBandLevel(Short.parseShort(temp[1]), Short.parseShort(temp[0]));
				bars[i].setProgress(Short.parseShort(temp[0])-minEQLevel);
				//setProgress activates the seek bar listener, the equation
				//inside undoes what happens when the listener acts.
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		setContentView(mLinearLayout);
	}

	/* Grab a name from user and save it to the device */
	private String promptSaveName() {

		AlertDialog.Builder savePrompt = new AlertDialog.Builder(this);
		savePrompt.setTitle("Custom Preset Name");
		savePrompt.setMessage("Enter a name for the new custom preset.");

		// Setup text field for user input
		final EditText input = new EditText(this);
		savePrompt.setView(input);
		
		// Setup save button on prompt
		savePrompt.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						check_flag = true;
					}// End onClick
				});

		// Do Nothing.
		savePrompt.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						check_flag = false;
					}
				});

		savePrompt.show();
		if (check_flag == false){
			return "";
		}else{
			return input.getText().toString();
		}
	}
	
	private boolean isOverwriteOk(){
		AlertDialog.Builder overwriteOk = new AlertDialog.Builder(this);
		overwriteOk.setTitle("File with that name already exists!");
		overwriteOk.setMessage("Do you want to overwrite the file?");
		
		overwriteOk.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						check_flag = true;
					}// End onClick
				});

		// Do Nothing.
		overwriteOk.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						check_flag = false;
					}
				});
		
		overwriteOk.show();
		
		return check_flag;
	}
	
	private void saveFile(short bands, Equalizer equalizer){
		List<File> currentListOfFiles = getListFiles(new File(getFilesDir().toString()));
		boolean save = true;
		final String saveName = promptSaveName();
		String FILENAME = saveName;
		
		for(File file : currentListOfFiles){
			Log.d(FILENAME, "In forloop");
			if(FILENAME.equals(file.getName())){
				if(isOverwriteOk()){
					break;
				}else{
					save = false;
					break;
				}
			}
		}
		//Checks if it's OK to save the file; if it is, then it saves
		//otherwise it does nothing.
		if(save){
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
			
			try {
				File saveFile = new File(getFilesDir(), FILENAME);
				FileOutputStream fos = new FileOutputStream(
						saveFile);
				fos.write(settings.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}//End if-statement
	}

	public void openSavePage(View v) {
		ScrollView scrollSaves = new ScrollView(this); //Allows for scrolling
		LinearLayout savesLayout = new LinearLayout(this);
		savesLayout.setOrientation(LinearLayout.VERTICAL);
		scrollSaves.addView(savesLayout);
		setContentView(scrollSaves);
		setupSavesPage(savesLayout);
	}

	private void setupSavesPage(LinearLayout saves) {
		//Sets up the saves page by finding every single file returned by 
		//the getFilesDir() function
		
		LinearLayout row = new LinearLayout(this);
		row.setOrientation(LinearLayout.HORIZONTAL);
		
		List<File> files = getListFiles(new File(getFilesDir().toString()));
		RadioGroup rGroup = new RadioGroup(this);
		rGroup.setId(id_count++);
		//Nothing in this loop is saved.
		for (File file : files) {
			RadioButton radio = new RadioButton(this);
			radio.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			radio.setId(id_count++);
			radio.setText(file.getName());
			rGroup.addView(radio);
		}
		row.addView(rGroup);
		saves.addView(row);
		Button load_button = new Button(this);
		Button delete_button = new Button(this);
		
		group = (RadioGroup) findViewById(rGroup.getId());
		
		load_button.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		load_button.setId(id_count++);
		load_button.setText("Load Configuration");
		load_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) findViewById(id);
				String file = getFilesDir().toString().concat("/").concat((String) rb.getText());
				File toBeLoaded = new File(file);
				loadEqualizSetting(toBeLoaded);
			}
		});
		
		
		delete_button.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		delete_button.setId(id_count++);
		delete_button.setText("Delete Configuration");
		delete_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) findViewById(id);
				String file = getFilesDir().toString().concat("/").concat((String) rb.getText());
				File toBeDeleted = new File(file);
				deleteFileIsOk(toBeDeleted, rb);
				/*if(toBeDeleted.delete() == true){
					group.removeView(rb);
				}*/
			}
		});
		
		saves.addView(load_button);
		saves.addView(delete_button);
	}

	private List<File> getListFiles(File parentDir) {
		//Obtained mostly from a stackoverflow page. It returns every file
		//in a given directory as an array.
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
	
	private void deleteFileIsOk(File fd, RadioButton rb){
		final File toBeDeleted = fd;
		final RadioButton toBeRemoved = rb;
		
		AlertDialog.Builder deleteOk = new AlertDialog.Builder(this);
		deleteOk.setTitle("WARNING: Once a setting is deleted, it cannot be recovered.");
		deleteOk.setMessage("Are you sure that you want to delete this configuration?");
		
		deleteOk.setPositiveButton("Yes", 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(toBeDeleted.delete() == true){
					group.removeView(toBeRemoved);
				}else{
					Log.println(0, "FileNotFound", "Selected file could not be deleted.");
				}
			}
		});
		
		deleteOk.setNegativeButton("Cancel", 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Do nothing
			}
		});
		
		deleteOk.show();
	}

	public void openTutorialPage(View v) {
		setContentView(R.layout.tutorial);
	}

}
