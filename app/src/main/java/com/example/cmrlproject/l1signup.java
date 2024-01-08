package com.example.cmrlproject;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import android.content.Intent;

import android.os.Bundle;

public class l1signup extends AppCompatActivity{
    Button b1,b2;
    EditText e1,e2,e3,e4,e5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l1signup);
        Intent i1 = getIntent();

    }

}
