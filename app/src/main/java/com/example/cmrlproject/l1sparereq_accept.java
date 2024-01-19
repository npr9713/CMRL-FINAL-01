package com.example.cmrlproject;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
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
import java.util.Date;
import java.util.Locale;
import java.util.Random;

// Import statements

// Import statements

public class l1sparereq_accept extends AppCompatActivity {
    ImageButton b1, b2, b3,b4;
    Button acc;
    TextView t1, t2, t3, t4, t5, t6,t7;
    String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l1sparereq_accpet);
        b1 = findViewById(R.id.homebut);
        b2 = findViewById(R.id.profilebut);
        b3 = findViewById(R.id.logsbut);
        b4 = findViewById(R.id.handoverbutton);
        t1 = findViewById(R.id.acceptedackno);
        t2 = findViewById(R.id.accepteddateandtime);
        t3 = findViewById(R.id.acceptedstation);
        t4 = findViewById(R.id.accepteddevice);
        t5 = findViewById(R.id.accepteddeviceno);
        t6 = findViewById(R.id.acceptedstatus);
        t7 = findViewById(R.id.acceptedstore);
        acc = findViewById(R.id.acceptb);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        Log.d("token-from FD", token);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1sparereq_accept.this, l1home.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1sparereq_accept.this, l1profile.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1sparereq_accept.this, l1acceptedreq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        //checking whether the fault is available or not and we have to see properly

        if (intent != null && intent.hasExtra("ackno")) {
            String ackno = intent.getStringExtra("ackno");
            String date = intent.getStringExtra("date");
            String station = intent.getStringExtra("station");
            String device = intent.getStringExtra("device");
            String deviceno = intent.getStringExtra("deviceno");
            String status = intent.getStringExtra("status");
            String storeLoc = intent.getStringExtra("location");


            t1.setText(ackno);
            t2.setText(date);
            t3.setText(station);
            t4.setText(device);
            t5.setText(deviceno);
            t6.setText(status);
            t7.setText(storeLoc);
        }
        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpRequestTask().execute();
            }
        });
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_spare_assign";

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
                    Intent intent = getIntent();
                    String token = intent.getStringExtra("token");
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
                    Log.d("response", String.valueOf(jsonResponse));

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
           int  success = jsonResponse.getInt("success");
           if(success==1)
           {
               showAssignmentSuccessAlert();
           }

        }

        private void showAssignmentSuccessAlert() {
            Log.d("status","assigned");
            AlertDialog.Builder builder = new AlertDialog.Builder(l1sparereq_accept.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been assigned successfully.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(l1sparereq_accept.this,l1acceptedreq.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
            });
            builder.show();
        }
        private void showAssignmentUnSuccessAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(l1sparereq_accept.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been assigned Already");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(l1sparereq_accept.this,l1home.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
            });
            builder.show();
        }

        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(l1sparereq_accept.this);
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
            Intent intent = new Intent(l1sparereq_accept.this, l1login.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }

    @SuppressLint("DefaultLocale")
    private String createJsonBody() {
        // Create a JSON object with the required parameters
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            String hoa = "Self";
            String status = "Assigned";

            return String.format("{\"ackno\":\"%s\",\"hoa\":\"%s\",\"status\":\"%s\"}",
                    t1.getText().toString(), hoa, status);
        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
}


