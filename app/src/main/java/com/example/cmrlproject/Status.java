package com.example.cmrlproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Status  extends AppCompatActivity {
    Button closed, spare,need;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptedreqdetail);
        spare = findViewById(R.id.sparereqb);
        closed = findViewById(R.id.closeb);
        need = findViewById(R.id.need);
        spare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Onclick","CLICKED");
            }
        });
    }


}
