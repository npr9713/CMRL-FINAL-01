package com.example.cmrlproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class forgotpass extends AppCompatActivity {
    ImageButton i1;
    Button b1,b2;
    String flag;
    EditText e1,e2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpass);

        i1=findViewById(R.id.backbutton);
        b1=findViewById(R.id.otpbut);
        b2=findViewById(R.id.submitbut);
        e1=findViewById(R.id.mailid);
        e2=findViewById(R.id.resetotp);
        Intent i = getIntent();
        flag=i.getStringExtra("flag");
        Log.d("flag",flag);


        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(flag, "1"))
                {
                    Intent i=new Intent(forgotpass.this, l1login.class);
                    startActivity(i);
                } else if (Objects.equals(flag, "2")) {
                    Intent i=new Intent(forgotpass.this, cmologin.class);
                    startActivity(i);

                } else if (Objects.equals(flag, "3")) {
                    Intent i=new Intent(forgotpass.this, zjelogin.class);
                    startActivity(i);

                }
                else {
                    Intent i=new Intent(forgotpass.this, storelogin.class);
                    startActivity(i);
                }

            }
        });
    }
}
