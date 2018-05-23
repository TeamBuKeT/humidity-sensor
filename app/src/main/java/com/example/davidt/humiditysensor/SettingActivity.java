package com.example.davidt.humiditysensor;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.Math.toIntExact;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    NumberPicker pickerMin, pickerMax;
    FirebaseDatabase database;
    DatabaseReference minimum, maximum;
    Long tauxMin, tauxMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button button = (Button) findViewById(R.id.btnEnregistrer);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickEnregistrer();
            }
        });

        database = FirebaseDatabase.getInstance();
        minimum = database.getReference("minimum");
        maximum = database.getReference("maximum");

        pickerMin = findViewById(R.id.PickerMin);
        pickerMax = findViewById(R.id.PickerMax);
        pickerMin.setMinValue(0);
        pickerMin.setMaxValue(99);
        pickerMax.setMaxValue(100);
        pickerMin.setOnValueChangedListener(this);

        // Read from the database
        minimum.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                tauxMin = (Long)dataSnapshot.getValue();
                pickerMin.setValue(toIntExact(tauxMin));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getBaseContext(), "Echec de lecture" + error.toException(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // Read from the database
        maximum.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                tauxMax = (Long) dataSnapshot.getValue();
                pickerMax.setValue(toIntExact(tauxMax));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getBaseContext(), "Echec de lecture" + error.toException(),
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                this.finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        pickerMax.setMinValue(newVal + 1);
    }

    private void onClickEnregistrer() {
        tauxMin = Long.valueOf(pickerMin.getValue());
        tauxMax = Long.valueOf(pickerMax.getValue());
        minimum.setValue(tauxMin);
        maximum.setValue(tauxMax);
        this.finish();
    }
}
