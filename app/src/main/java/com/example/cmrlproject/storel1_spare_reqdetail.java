package com.example.cmrlproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class storel1_spare_reqdetail extends AppCompatActivity {
    TextView t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16,t17,t18;
    EditText e1;
    Button b5;
    ImageButton b1,b2,b3,b4;
    String token;
    String request_type;
    String date;
    String station;
    String mr_id;
    String sle_name;
    String sle_no;
    String unit;
    String need_spare;
    String por;
    String fsno;
    String description;
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storel1_spare_reqdetail);
        Intent intent = getIntent();
        token=  intent.getStringExtra("token");
        t1=findViewById(R.id.reqtype);
        t2=findViewById(R.id.date);
        t3=findViewById(R.id.station);
        t4=findViewById(R.id.slename);
        t5=findViewById(R.id.sleno);
        t6=findViewById(R.id.uniname);
        t7=findViewById(R.id.sparereq);
        t8=findViewById(R.id.reqby);
        t9=findViewById(R.id.fspareslno);
        t10=findViewById(R.id.fdesc);
        t11=findViewById(R.id.status);
        t12=findViewById(R.id.frsle);
        t13=findViewById(R.id.frsleno);
        t14=findViewById(R.id.wsno);
        t15=findViewById(R.id.mrid);
        t16 = findViewById(R.id.textView41);
        t17 = findViewById(R.id.textView42);
        t18 = findViewById(R.id.textView43);
        b1=findViewById(R.id.sparemov);
        b2=findViewById(R.id.homebut);
        b3=findViewById(R.id.dashboardbut);
        b4=findViewById(R.id.profilebut);
        e1=findViewById(R.id.serialno);
        b5=findViewById(R.id.button4);
        if (intent != null && intent.hasExtra("ackno")) {
            String fsle=null,fsleno=null,wsno=null;
            date = intent.getStringExtra("date");
            station = intent.getStringExtra("station");
            mr_id = intent.getStringExtra("mr_id");
            sle_name = intent.getStringExtra("sle_name");
            sle_no = intent.getStringExtra("sle_no");
            unit = intent.getStringExtra("unit");
            need_spare = intent.getStringExtra("need_spare");
            por = intent.getStringExtra("por");
            fsno = intent.getStringExtra("fsno");
            description = intent.getStringExtra("description");
            status = intent.getStringExtra("status");
            request_type = intent.getStringExtra("request_type");
//            if(fsle==null)
//            {
//                t16.setVisibility(View.GONE);
//            }
//            if(fsleno==null)
//            {
//                t17.setVisibility(View.GONE);
//            }
//            if(wsno==null)
//            {
//                t18.setVisibility(View.GONE);
//            }
            if("against faulty spare".equalsIgnoreCase(request_type))
            {
            t1.setText(request_type);
            t2.setText(date);
            t3.setText(station);
            t4.setText(sle_name);
            t5.setText(sle_no);
            t6.setText(unit);
            t7.setText(need_spare);
            t8.setText(por);
            t9.setText(fsno);
            t10.setText(description);
            t11.setText(status);
            t15.setText(mr_id);
            t18.setVisibility(View.GONE);
            t17.setVisibility(View.GONE);
                t16.setVisibility(View.GONE);
            }
            if(!("pending_sr".equalsIgnoreCase(status)))
            {
                e1.setVisibility(View.GONE);
                b5.setVisibility(View.GONE);
            }
        }
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_spare_reqdetail.this, storel1dashboard.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_spare_reqdetail.this, storel1_sparemov.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_spare_reqdetail.this, storel1profile.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_spare_reqdetail.this, storel1home.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpRequestTask().execute();
            }
        });


    }
    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/request_assign";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Create JSON request body
                    String jsonBody = createJsonBody();

                    // Set up the HTTP request
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    // Retrieve the token from the intent
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                    urlConnection.setDoOutput(true);

                    // Write JSON data to the output stream
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(jsonBody.getBytes("UTF-8"));
                    os.close();

                    // Get the response from the server
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    if (jsonResponse.has("success")) {
                        // If success array is present, the fault is assigned successfully
                        handleSuccessResponse(jsonResponse);
                        //hello world


                    } else if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                        // If unauthorized message is present, alert the user to relogin and redirect to login page
                        showTokenExpiredAlert();
                    }
                    else if (jsonResponse.has("message") && "Already assigned".equals(jsonResponse.getString("message"))) {
                        // If unauthorized message is present, alert the user to relogin and redirect to login page
                        showAssignmentUnSuccessAlert();
                    }else {
                        // Handle other cases or response formats
                        Log.e("API Error", "Unexpected response format");
                    }
                } catch (JSONException e) {
                    Log.e("API Error", "Error parsing JSON response", e);
                }
            } else {
                // Handle error
                Log.e("API Error", "Failed to get response");
            }
        }

        private void handleSuccessResponse(JSONObject jsonResponse) throws JSONException {
            int success = jsonResponse.getInt("success");


            if (success == 1) {
                showAssignmentSuccessAlert();
            }else{
                showAssignmentUnSuccessAlert();
            }
        }

        private void showAssignmentSuccessAlert() {
            Log.d("status","assigned");
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_spare_reqdetail.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been assigned successfully.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(storel1_spare_reqdetail.this,storel1home.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
            });
            builder.show();
        }
        private void showAssignmentUnSuccessAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_spare_reqdetail.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been assigned Already");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(storel1_spare_reqdetail.this,storel1home.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
            });
            builder.show();
        }

        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_spare_reqdetail.this);
            builder.setTitle("Session Expired");
            builder.setMessage("Your session has expired. Please log in again.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Redirect to login page
                    redirectToLoginPage();
                }
            });
            builder.show();
        }

        private void redirectToLoginPage() {
            Intent intent = new Intent(storel1_spare_reqdetail.this, storelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }

    @SuppressLint("DefaultLocale")
    private String createJsonBody() {
        // Create a JSON object with the required parameters
        try {
            Random random = new Random();
            String eid = String.valueOf(random.nextInt(1000)); // Replace 1000 with your desired upper limit for eid

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            String hoa = "Self";
            String status = "Assigned";
            Log.d("mr_id",mr_id);Log.d("sle_no",sle_no);Log.d("sno",e1.getText().toString());Log.d("spare_name",need_spare);Log.d("station",station);
            mr_id = mr_id.trim();

// Escape special characters in e1.getText().toString()
            String escapedE1 = e1.getText().toString().replace("\"", "\\\"");
            String jsonString = String.format("{\"mr_id\":\"%s\",\"sle_no\":\"%s\",\"sno\":\"%s\",\"spare_name\":\"%s\",\"station\":\"%s\"}",
                    mr_id, sle_no, escapedE1, need_spare, station);

// Log the generated JSON string
            System.out.println("Generated JSON: " + jsonString);
            return jsonString;

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }


}