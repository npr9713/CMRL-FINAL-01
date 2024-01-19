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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

public class against_faulty_spare extends AppCompatActivity {

    TextView t1;
    String fault_type;
    EditText e1, e2, e3;
    AutoCompleteTextView a1,a2,a3,a4,a5,a6;
    Button b1;
    String token,ackno;
    AutoCompleteTextView stationAutoCompleteTextView, slenameAutoCompleteTextView, slenoAutoCompleteTextView, unitnameAutoCompleteTextView;
    AutoCompleteTextView sparerequestedAutoCompleteTextView, requestedbyAutoCompleteTextView;
    String selectedStation, selectedSLEname, selectedSLEno, selectedUnitname, selectedSparereq, selectedReqby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.against_faulty_spare);
        t1 = findViewById(R.id.sparereqtype);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        ackno = i.getStringExtra("ackno");
        Log.d("token",token);
        Log.d("ackno",ackno);
        e2 = findViewById(R.id.faulty_spare_SL_no);
        e3 = findViewById(R.id.fault_desc);
        b1 = findViewById(R.id.submit);
        a1 = findViewById(R.id.sparerequested);
        a2 = findViewById(R.id.slenameAutoCompleteTextView);
        a3 = findViewById(R.id.slenoAutoCompleteTextView);
        a4 = findViewById(R.id.stationAutoCompleteTextView);
        a5 = findViewById(R.id.unitnameAutoCompleteTextView);
        a6 = findViewById(R.id.requestedbyAutoCompleteTextView);

        fault_type = i.getStringExtra("fault type");
        t1.setText(fault_type);

        stationAutoCompleteTextView = findViewById(R.id.stationAutoCompleteTextView);
        slenameAutoCompleteTextView = findViewById(R.id.slenameAutoCompleteTextView);
        slenoAutoCompleteTextView = findViewById(R.id.slenoAutoCompleteTextView);
        unitnameAutoCompleteTextView = findViewById(R.id.unitnameAutoCompleteTextView);
        sparerequestedAutoCompleteTextView = findViewById(R.id.sparerequested);
        requestedbyAutoCompleteTextView = findViewById(R.id.requestedbyAutoCompleteTextView);

        // Replace R.array.station_codes with the array of station names
        String[] stationArray = getResources().getStringArray(R.array.station_codes);

        ArrayAdapter<String> stationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stationArray);
        stationAutoCompleteTextView.setAdapter(stationAdapter);

        stationAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedStation = (String) parent.getItemAtPosition(position);
                Log.d("selected location", selectedStation);
            }
        });
        String[] SLEnameArray = getResources().getStringArray(R.array.SLE_name_codes);

        ArrayAdapter<String> SLEnameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SLEnameArray);
        slenameAutoCompleteTextView.setAdapter(SLEnameAdapter);

        slenameAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSLEname = (String) parent.getItemAtPosition(position);
                Log.d("selected location", selectedSLEname);
            }
        });
        String[] SLEnoArray = getResources().getStringArray(R.array.SLE_no_codes);

        ArrayAdapter<String> SLEnoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SLEnoArray);
        slenoAutoCompleteTextView.setAdapter(SLEnoAdapter);

        slenoAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSLEno = (String) parent.getItemAtPosition(position);
                Log.d("selected location", selectedSLEno);
            }
        });
        String[] UnitnameArray = getResources().getStringArray(R.array.unit_name_codes);

        ArrayAdapter<String> UnitnameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, UnitnameArray);
        unitnameAutoCompleteTextView.setAdapter(UnitnameAdapter);

        unitnameAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUnitname = (String) parent.getItemAtPosition(position);
                Log.d("selected location", selectedUnitname);
            }
        });
        String[] SparerequestedArray = getResources().getStringArray(R.array.SPARE_REQUESTED_codes);

        ArrayAdapter<String> SparerequestedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SparerequestedArray);
        sparerequestedAutoCompleteTextView.setAdapter(SparerequestedAdapter);

        sparerequestedAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSparereq = (String) parent.getItemAtPosition(position);
                Log.d("selected location", selectedSparereq);
            }
        });
        String[] RequestedbyArray = getResources().getStringArray(R.array.requested_by_codes);

        ArrayAdapter<String> RequestedbyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, RequestedbyArray);
        requestedbyAutoCompleteTextView.setAdapter(RequestedbyAdapter);

        requestedbyAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedReqby = (String) parent.getItemAtPosition(position);
                Log.d("selected location", selectedReqby);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateRequest().execute();

            }
        });

    }

    private class CreateRequest extends AsyncTask<Void, Void, String> {
//        private final String token;

        // Constructor to receive token


        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/zje_afs";

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

                    // Set authorization header
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

                        handleSuccessResponse(jsonResponse);
                    } else if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                        // If unauthorized message is present, alert the user to relogin and redirect to login page
                        showTokenExpiredAlert();
                    } else {
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
            } else {
                showUnsuccessfulAssignmentAlert();
            }
        }


        private void showAssignmentSuccessAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(against_faulty_spare.this);
            builder.setTitle("Against faulty spare Created");
            builder.setMessage("The New fault has been created successfully.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    redirectToReqPage();
                }
            });
            builder.show();
            // Optional: Close the current activity if needed
        }

        private void showUnsuccessfulAssignmentAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(against_faulty_spare.this);
            builder.setTitle("Fault Creation Failed");
            builder.setMessage("The fault creation was unsucessfull.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();

        }

        private void showTokenExpiredAlert() {

            AlertDialog.Builder builder = new AlertDialog.Builder(against_faulty_spare.this);
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
            Intent intent = new Intent(against_faulty_spare.this, zjelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

        private void redirectToReqPage() {
            Intent intent = new Intent(against_faulty_spare.this, zjeviewreq.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }

    @SuppressLint("DefaultLocale")
    private String createJsonBody() {
        try {
            // Include latitude, longitude, and ackno in the JSON body
            return String.format("{\"ackno\":\"%s\", \"des\":\"%s\", \"fsno\":\"%s\", \"need_spare\":\"%s\",\"sle_name\":\"%s\",\"sle_no\":\"%s\",\"station\":\"%s\",\"unit\":\"%s\"}",
                    ackno, e3.getText().toString(), e2.getText().toString(), a1.getText().toString(),a2.getText().toString(),a3.getText().toString(),a4.getText().toString(),a5.getText().toString());

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
}
