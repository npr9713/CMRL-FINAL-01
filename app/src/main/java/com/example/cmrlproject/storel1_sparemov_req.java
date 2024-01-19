package com.example.cmrlproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class storel1_sparemov_req extends AppCompatActivity {
    TextView t1,t2,t3,t4;
    ImageButton b1,b2,b3,b4;
    Button b5;
    Spinner Moved_Station_Spinner;
    String token;
    String date;
    String station;
    String mr_id;
    String spare_sno;

    String spare_name;
    String por;
    String fsno;
    String description;
    String status;
    String selected_moved_station;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storel1_sparemov_req);
        Intent intent = getIntent();
        token=  intent.getStringExtra("token");
        t1=findViewById(R.id.mrid);
        t2=findViewById(R.id.station);
        t3=findViewById(R.id.sparename);
        t4=findViewById(R.id.spareserno);
        b1=findViewById(R.id.sparemov);
        b2=findViewById(R.id.homebut);
        b3=findViewById(R.id.dashboardbut);
        b4=findViewById(R.id.profilebut);
        b5=findViewById(R.id.updateb);
        Moved_Station_Spinner = findViewById(R.id.Moved_Station_Spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.moved_station_codes, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Moved_Station_Spinner.setAdapter(adapter1);

        Moved_Station_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the spinner
                selected_moved_station = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String not_selected_status = "true";
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov_req.this, storel1dashboard.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov_req.this, storel1_sparemov.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov_req.this, storel1profile.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov_req.this, storel1home.class);
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
        if (intent != null && intent.hasExtra("ackno")) {
            String fsle=null,fsleno=null,wsno=null;
            station = intent.getStringExtra("station");
            mr_id = intent.getStringExtra("mr_id");
            spare_sno = intent.getStringExtra("sno");
            spare_name = intent.getStringExtra("spare_name");
            Log.d("spare_name",spare_name);
            Log.d("spare_sno",spare_sno);
            status = intent.getStringExtra("status");


            t1.setText(mr_id);
            t2.setText(station);
            t3.setText(spare_name);
            t4.setText(spare_sno);

        }

    }
    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/request_handover";

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
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_sparemov_req.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been handovered successfully.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(storel1_sparemov_req.this,storel1home.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
            });
            builder.show();
        }
        private void showAssignmentUnSuccessAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_sparemov_req.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been handovered Already");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(storel1_sparemov_req.this,storel1home.class);
                    intent.putExtra("token",token);
                    startActivity(intent);
                }
            });
            builder.show();
        }

        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_sparemov_req.this);
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
            Intent intent = new Intent(storel1_sparemov_req.this, storelogin.class);
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

            return String.format("{\"mr_id\":\"%s\",\"location\":\"%s\"}",
                    mr_id, selected_moved_station);

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }

}