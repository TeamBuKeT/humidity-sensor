package com.example.davidt.humiditysensor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference humidity, arrosage, en_cours, automatic;
    TextView TVTauxHumidity;
    ImageView imageArrosage;
    Button boutonWater;
    Long arrosage_en_cours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        humidity = database.getReference("humidity");
        arrosage = database.getReference("arrosage");
        en_cours = database.getReference("en_cours");
        automatic = database.getReference("automatic");

        TVTauxHumidity = findViewById(R.id.TVHumidity);
        boutonWater = findViewById(R.id.BtnWater);
        imageArrosage = findViewById(R.id.imgArrosage);
        boutonWater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickWater();
            }
        });

        // Read from the database
        en_cours.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                arrosage_en_cours = (Long) dataSnapshot.getValue();
                if (arrosage_en_cours == 1) {
                    imageArrosage.setImageResource(R.drawable.green);
                    boutonWater.setBackgroundColor(getResources().getColor(R.color.arrosage_red));
                    boutonWater.setText(R.string.stop_water);
                }else {
                    imageArrosage.setImageResource(R.drawable.red);
                    boutonWater.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    boutonWater.setText(R.string.arroser);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getBaseContext(), "Echec de lecture" + error.toException(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // Read from the database
        humidity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Long value = (Long) dataSnapshot.getValue();
                TVTauxHumidity.setText(String.valueOf(value));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getBaseContext(), "Echec de lecture" + error.toException(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // Read from the database
        automatic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long value = (Long) dataSnapshot.getValue();
                if (value == 1) {
                    //Désactivation du bouton
                    boutonWater.setEnabled(false);
                    boutonWater.setVisibility(View.GONE);
                }else {
                    //Activation du bouton
                    boutonWater.setEnabled(true);
                    boutonWater.setVisibility(View.VISIBLE);

                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de items à l'ActionBar
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this,
                        SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickWater() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        if (arrosage_en_cours == 0) {
            builder.setTitle("Arrosage")
                    .setMessage("Etes-vous sur de vouloir arroser la plante ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            en_cours.setValue(1);
                            Toast.makeText(getBaseContext(), "Arrosage en cours !",
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            en_cours.setValue(0);

        }

    }
}
