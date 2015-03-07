package com.fyp.diabetes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.androidgpsexample.R;
import com.fyp.library.UserFunctions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class setmet extends Activity {

	TextView height, gender;
	EditText editWeight, editHeigth, editGlucose, editA1c, editBPsys, editBPdia;
	Button btnAdd,btnDelete,btnModify,btnView,btnViewAll,btnShowInfo,btnDashboard,btnCancel,btnSex;
	SQLiteDatabase db;
	UserFunctions userFunction = new UserFunctions();
	AlertDialog levelDialog;
	String sex = "";
	String age = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.upd_metrics);

		// If coming from AddReminder
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int nullMets = extras.getInt("metNull");
			if(nullMets == 1){
				showMessage("No Records Found", "Please enter values!");
				new CountDownTimer(2000, 2000) {
					@Override
					public void onTick(long millisUntilFinished) {}
					@Override
					public void onFinish() {
						ageDialog();
					}
				}.start();
			}
		}  

		// Enable permissions to post to db
		if (android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		height = (TextView)findViewById(R.id.complete_by);
		gender =  (TextView)findViewById(R.id.tvGender);

		editWeight = (EditText)findViewById(R.id.updateWeight);
		editHeigth = (EditText)findViewById(R.id.updateHeight);
		editGlucose = (EditText)findViewById(R.id.updateGlucose);
		editA1c = (EditText)findViewById(R.id.updateA1c);
		editBPsys = (EditText)findViewById(R.id.currBPsys);
		editBPdia = (EditText)findViewById(R.id.currBPdia);

		btnAdd = (Button)findViewById(R.id.btnUpdMetUpd);
		btnCancel = (Button)findViewById(R.id.btnUpdMetCancel);
		btnSex = (Button)findViewById(R.id.btnSex);

		db = openOrCreateDatabase("MetricsDB", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS user_metrics ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," 			
				+ "user_id TEXT,"
				+ "weight TEXT,"
				+ "height TEXT,"
				+ "glucose TEXT,"
				+ "hba1c TEXT,"
				+ "BPsys TEXT,"
				+ "BPdia TEXT,"
				+ "sex TEXT,"
				+ "birth_year TEXT,"
				+ "created_on TEXT)");

		Cursor c = db.rawQuery("SELECT * FROM user_metrics WHERE user_id = " +
				"'"+userFunction.getUID(getApplicationContext())+"'", null);

		if(c.moveToLast()){
			if(c.getString(3).toString().length()>1){
				// Hide textview and input box
				height.setVisibility(android.view.View.GONE);
				editHeigth.setVisibility(android.view.View.GONE);
			}
			if(c.getString(8).toString().length()>1){
				// Hide textview and input box
				gender.setVisibility(android.view.View.GONE);
				btnSex.setVisibility(android.view.View.GONE);
			}
		}
	

		// Goal Type Button
		btnSex.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Strings to Show In Dialog with Radio Buttons
				CharSequence[] items = {" Male "," Female "};

				// Creating and Building the Dialog 
				AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
				builder.setTitle("Select Gender");
				builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						switch(item) {
						case 0:
							sex = "Male";
							btnSex.setText("Male");
							break;
						case 1:
							sex = "Female";
							btnSex.setText("Female");
							break;
						}   
						levelDialog.dismiss();
					}
				});
				levelDialog = builder.create();
				levelDialog.show();
			}
		});


		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(view == btnAdd) {
					Cursor c = db.rawQuery("SELECT * FROM user_metrics WHERE user_id = " +
							"'"+userFunction.getUID(getApplicationContext())+"'", null);

					ArrayList<String> list = new ArrayList<String>();
					boolean errorCall = false;

					if(c.moveToLast()){
						list.add(c.getString(2));
						list.add(c.getString(3));
						list.add(c.getString(4));
						list.add(c.getString(5));
						list.add(c.getString(6));
						list.add(c.getString(7));
						list.add(c.getString(8));
						list.add(c.getString(9));
					}

					if(editWeight.getText().toString().trim().length()==0){
						errorCall = true;
						editWeight.setText(list.get(0).toString());
					} 
					if(editHeigth.getText().toString().trim().length()==0){
						editHeigth.setText(list.get(1));
					}
					if(editGlucose.getText().toString().trim().length()==0){
						errorCall = true;
						editGlucose.setText(list.get(2));
					}
					if(editA1c.getText().toString().trim().length()==0){
						errorCall = true;
						editA1c.setText(list.get(3));
					}
					if(editBPsys.getText().toString().trim().length()==0){
						errorCall = true;
						editBPsys.setText(list.get(4));
					}
					if(editBPdia.getText().toString().trim().length()==0){
						errorCall = true;
						editBPdia.setText(list.get(5));
					}
					if(sex == ""){
						sex = list.get(6);
					}
					if(age == ""){
						age = list.get(7);
					}

					if (errorCall == true)
						showMessage("Warning!", "Empty Fields, using old values");

					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					Date date = new Date();

					UserFunctions userFunction = new UserFunctions();

					// ****Add in age and sex here****
					userFunction.postMetrics(userFunction.getUID(getApplicationContext()), 
							editWeight.getText().toString(), editHeigth.getText().toString(), 
							editGlucose.getText().toString(), editA1c.getText().toString(), 
							editBPsys.getText().toString(), editBPdia.getText().toString(),
							sex.toString(),age.toString());

					db.execSQL("INSERT INTO user_metrics (user_id, weight, height, glucose, hba1c, BPsys, BPdia, sex, birth_year, created_on) " +
							"VALUES('"+userFunction.getUID(getApplicationContext())+"','"+editWeight.getText()+"','"+editHeigth.getText()+
							"','"+editGlucose.getText()+"','"+editA1c.getText()+"','"+editBPsys.getText()+
							"','"+editBPdia.getText()+"','"+sex.toString()+"','"+age.toString()+"','"+dateFormat.format(date).toString()+"');");

					showMessage("Success", "Record added");
					//clearText();
					
					new CountDownTimer(1500, 1500) {
						@Override
						public void onTick(long millisUntilFinished) {}
						@Override
						public void onFinish() {
							Intent dia = new Intent(getApplicationContext(), diadash.class);
							dia.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							startActivity(dia);
							finish();
						}
					}.start();
					
				}
			}
		});


		// Update Metrics Button
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent dia = new Intent(getApplicationContext(), diadash.class);
				dia.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(dia);
				finish();
			}
		});

	}
	
	// Age dialog pop-up
	protected void ageDialog() {
		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(setmet.this);
		View promptView = layoutInflater.inflate(R.layout.age_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(setmet.this);
		alertDialogBuilder.setView(promptView);

		final EditText editText = (EditText) promptView.findViewById(R.id.editAge);
		
		// setup a dialog window
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				age = String.valueOf(editText.getText());
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				Intent dia = new Intent(getApplicationContext(), diadash.class);
				dia.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				finish();
			}
		});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	// Display message
	public void showMessage(String title,String message) {
		Builder builder=new Builder(this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}

	// Clears textboxes
	public void clearText()	{
		editWeight.setText("");
		editHeigth.setText("");
		editA1c.setText("");
		editBPsys.setText("");
		editBPdia.setText("");
		editGlucose.setText("");
		editWeight.requestFocus();
	}
}

