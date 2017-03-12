package com.psarmmiey.placesViewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddService extends AppCompatActivity {
	FloatingActionButton saveFab;
	private GoogleApiClient mGoogleApiClient;
	private TextView latText;
	private TextView longitudeText;
	private TextInputLayout nameTextInputLayout;
	private TextInputLayout phoneTextInputLayout;
	private TextInputLayout vicinityTextInputLayout;
	private Spinner typeSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_service);
		latText = (TextView) findViewById(R.id.latAddTextview);
		longitudeText = (TextView) findViewById(R.id.longAddTextView);
		nameTextInputLayout = (TextInputLayout) findViewById(R.id.nameTextInputLayout);
		phoneTextInputLayout = (TextInputLayout) findViewById(R.id.telephoneTextInputLayout);
		vicinityTextInputLayout = (TextInputLayout) findViewById(R.id.vicinityTextInputLayout);
		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		
		Intent intent = getIntent();
		// String message = ;
		
		latText.setText(String.format("%s",
			intent.getStringExtra(MainActivity.EXTRA_MESSAGE_LAT)));
		longitudeText.setText(String.format("%s",
			intent.getStringExtra(MainActivity.EXTRA_MESSAGE_LONG)));
		
		saveFab = (FloatingActionButton) findViewById(R.id.saveServiceFab);
		saveFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(nameTextInputLayout.getEditText().toString() == null) {
					nameTextInputLayout.getEditText().setError("Name cannot be Empty");
					if(phoneTextInputLayout.getEditText().toString() == null) {
						phoneTextInputLayout.getEditText().setError("Insert Phone Number");
						if(vicinityTextInputLayout.getEditText().toString() == null)
							vicinityTextInputLayout.getEditText().setError("Enter Place Address");
					}
				} else
					savePlaceToFirebase();
			}
		});
		
	}
	
	public void savePlaceToFirebase() {
		nameTextInputLayout = (TextInputLayout) findViewById(R.id.nameTextInputLayout);
		phoneTextInputLayout = (TextInputLayout) findViewById(R.id.telephoneTextInputLayout);
		vicinityTextInputLayout = (TextInputLayout) findViewById(R.id.vicinityTextInputLayout);
		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		latText = (TextView) findViewById(R.id.latAddTextview);
		longitudeText = (TextView) findViewById(R.id.longAddTextView);
		
		// Write a message to the database
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference("oyo");
		//	myRef.child(typeSpinner.getSelectedItem().toString()).child("result");
		myRef.child(typeSpinner.getSelectedItem().toString()).child("result").child("name").setValue(nameTextInputLayout.getEditText().getText().toString());
		myRef.child("name").child("phone").setValue(phoneTextInputLayout.getEditText().getText().toString());
		myRef.child("name").child("vicinity").setValue(vicinityTextInputLayout.getEditText().getText().toString());
		myRef.child("name").child("location").child("lat").setValue(latText.getText());
		myRef.child("name").child("location").child("long").setValue(longitudeText.getText());
	}
	
	
}
