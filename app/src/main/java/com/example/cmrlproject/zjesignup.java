package com.example.cmrlproject;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import android.content.Intent;
import android.os.Bundle;
public class zjesignup extends AppCompatActivity {
    Button b1,b2;
    EditText e1,e2,e3,e4,e5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zjesignup);
        Intent i = getIntent();
        b1=(Button) findViewById(R.id.button2);
        b2=(Button) findViewById(R.id.button);
        e1=(EditText) findViewById(R.id.editTextText);
        e2=(EditText) findViewById(R.id.editTextText2);
        e3=(EditText) findViewById(R.id.editTextTextPassword);
        e4=(EditText) findViewById(R.id.editTextPhone);
        e5=(EditText) findViewById(R.id.editTextNumberPassword);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjesignup.this, zjelogin.class);
                startActivity(i);
            }
        });



    }

}