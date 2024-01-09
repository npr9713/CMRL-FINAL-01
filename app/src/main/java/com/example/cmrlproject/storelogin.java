package com.example.cmrlproject;



import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;


import android.content.Intent;

import android.os.Bundle;
import android.widget.Spinner;

public class storelogin extends AppCompatActivity {
    Button b1,b2;
   String f="4";
    EditText e1,e3;
    String eid;
    AlertDialog.Builder builder;
    String usertype,not_selected_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelogin);


        e1 =  findViewById(R.id.eidl);

        e3 =  findViewById(R.id.passwordl);
        b1 = findViewById(R.id.loginb);
        b2 =  findViewById(R.id.resetpass);
        builder= new AlertDialog.Builder(this);
        Spinner userSpinner = findViewById(R.id.userSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.store_user_codes, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        userSpinner.setAdapter(adapter);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the spinner
                usertype = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                not_selected_user = "true";
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(e1.getText().toString().trim().length()==0||e3.getText().toString().trim().length()==0) {
                    builder.setTitle("Error!!")
                            .setMessage("Enter all the value to continue!")
                            .setCancelable(true)
                            .show();
                }

                else{
                    eid=e1.getText().toString();
                    Intent i = new Intent(storelogin.this, storehome.class);
                    startActivity(i);
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(storelogin.this, forgotpass.class);
                i.putExtra("flag",f);
                startActivity(i);

            }
        });

    }}
