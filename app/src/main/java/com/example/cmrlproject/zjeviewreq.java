package com.example.cmrlproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class zjeviewreq extends AppCompatActivity {
    private JSONArray successArray;
    private JSONObject jsonResponse;
    String token;

    String eid;
    ImageButton b1, b2, b3,b4,b5;
    String selected_station,selected_status,not_selected_station,not_selected_status;
    private ListView faultListView;
    private List<String> faultList;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zjeviewreq);
        Intent i = getIntent();
        token = i.getStringExtra("token");

        // Initialize views and variables
        faultListView = findViewById(R.id.faultListView);
        faultList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, faultList);
        faultListView.setAdapter(adapter);

        // Execute the AsyncTask to make the HTTP POST request
        new HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/ze_view");

        b1 = findViewById(R.id.approvereq);
        b2 = findViewById(R.id.homebut);
        b3 = findViewById(R.id.viewreqbut);
        b4 = findViewById(R.id.profilebut);
        b5=findViewById(R.id.dashboardbut);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjeviewreq.this, zjeapprovereq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjeviewreq.this, zjehome.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjeviewreq.this, zjeprofile.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjeviewreq.this, zjedashboard.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        Spinner stationSpinner = findViewById(R.id.stationSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.station_codes1, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        stationSpinner.setAdapter(adapter);
        stationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the spinner
                selected_station = parent.getItemAtPosition(position).toString();
                ApplyFilterStation(selected_station);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                not_selected_station="true";
            }
        });
        Spinner statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.status_codes, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter1);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the spinner
                selected_status = parent.getItemAtPosition(position).toString();
                ApplyFilterStatus(selected_status);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                not_selected_status="true";
            }
        });
        faultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // Get the selected fault string from the filtered list
                    String selectedFault = faultList.get(position);

                    // Iterate through the original unfiltered array to find the corresponding JSON object
                    for (int i = 0; i < successArray.length(); i++) {
                        JSONObject successObject = successArray.getJSONObject(i);

                        // Get values from JSON
                        String station = successObject.getString("station");
                        String device = successObject.getString("device");
                        String status = successObject.getString("status");

                        // Create a fault string to match against the selectedFault
                        String fault = station + ": " + device + ": " + status;

                        if (fault.equals(selectedFault)) {
                            // Found the corresponding JSON object, open fault details
                            openFaultDetails(successObject);
                            break; // Break the loop once found
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openFaultDetails(JSONObject successObject) throws JSONException {
        Intent intent = new Intent(this, zjefaultdetail.class);
        String ackno = successObject.getString("ackno");
        Log.d("ACKNO",ackno);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        String date = successObject.getString("dt");
        String station = successObject.getString("station");
        String device = successObject.getString("device");
        String deviceno = successObject.getString("deviceno");
        String status = successObject.getString("status");
        intent.putExtra("ackno", ackno);
        intent.putExtra("date", date);
        intent.putExtra("station", station);
        intent.putExtra("device", device);
        intent.putExtra("deviceno", deviceno);
        intent.putExtra("status", status);
        intent.putExtra("token",token);
        startActivity(intent);
    }
    private void ApplyFilterStation(String SStation)
    {
        if (successArray == null) {
            // Handle the case where successArray is not initialized

            return;
        }


        faultList.clear();
        for (int i = 0; i < successArray.length(); i++) {
            try {
                JSONObject successObject = successArray.getJSONObject(i);

                // Get values from JSON
                String station = successObject.getString("station");
                String device = successObject.getString("device");
                String status = successObject.getString("status");

                if(SStation.equalsIgnoreCase("All Station"))
                {
                    String fault = station + ": " + device + ": " + status;
                    faultList.add(fault);

                }
                if (station.equalsIgnoreCase(SStation)) {
                    // Add station and ackno to the list
                    String fault = station + ": " + device + ": " + status;
                    faultList.add(fault);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void ApplyFilterStatus(String SStatus)
    {
        if (successArray == null) {
            // Handle the case where successArray is not initialized

            return;
        }


        faultList.clear();
        for (int i = 0; i < successArray.length(); i++) {
            try {
                JSONObject successObject = successArray.getJSONObject(i);

                // Get values from JSON
                String station = successObject.getString("station");
                String device = successObject.getString("device");
                String status = successObject.getString("status");

                if(SStatus.equalsIgnoreCase("All Status"))
                {
                    String fault = station + ": " + device + ": " + status;
                    faultList.add(fault);

                }
                if (status.equalsIgnoreCase(SStatus)) {
                    // Add station and ackno to the list
                    String fault = station + ": " + device + ": " + status;
                    faultList.add(fault);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                // Create a URL object and open a connection
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Set the request method to POST
                urlConnection.setRequestMethod("POST");

                // Set the request headers (if needed)
                // urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                // Enable input/output streams for writing/reading data
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                // Create the POST data (replace with your own data)
                Map<String, String> postData = new HashMap<>();
                postData.put("key1", "value1");
                postData.put("key2", "value2");

                // Write the POST data to the output stream
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                StringBuilder postDataString = new StringBuilder();
                for (Map.Entry<String, String> entry : postData.entrySet()) {
                    postDataString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                postDataString.deleteCharAt(postDataString.length() - 1); // Remove the trailing "&"
                outputStream.writeBytes(postDataString.toString());
                outputStream.flush();
                outputStream.close();

                // Read the input stream into a String
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                try {
                    // Parse the JSON response
                    jsonResponse = new JSONObject(result);
                    successArray = jsonResponse.getJSONArray("success");

                    // Iterate through the success array
                    for (int i = 0; i < successArray.length(); i++) {
                        JSONObject successObject = successArray.getJSONObject(i);

                        // Get values from JSON
                        String station = successObject.getString("station");
                        String device = successObject.getString("device");
                        String status = successObject.getString("status");

                        // Add station and ackno to the list
                        String fault = station + ": " + device+ ": "+ status;
                        faultList.add(fault);
                    }

                    // Notify the adapter that the data set has changed
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case where the result is null
                Toast.makeText(zjeviewreq.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
