package com.example.cmrlproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class l1acceptedreq extends AppCompatActivity {
    private List<FaultItem> faultList;
    private ArrayAdapter<FaultItem> adapter;

    String token;
    ImageButton b1, b2, b3,b4;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l1acceptedreq);

        // Initialize views and variables
        b1 = findViewById(R.id.homebut);
        b2 = findViewById(R.id.profilebut);
        b3 = findViewById(R.id.logsbut);
        b4 = findViewById(R.id.handoverbutton);
        t1 = findViewById(R.id.notext);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        Log.d("TOKEN-L1",token);

        faultList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, faultList);

        ListView faultListView = findViewById(R.id.faultListView);
        faultListView.setAdapter(adapter);
        t1.setVisibility(View.GONE);

        // Execute the AsyncTask to make the HTTP POST request
        new HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_request_page");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1acceptedreq.this, l1home.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1acceptedreq.this, l1profile.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1acceptedreq.this, l1acceptedreq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        // Handle item click to open details page
        faultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaultItem selectedFault = faultList.get(position);
                openFaultDetails(selectedFault);
            }
        });
    }

    private void openFaultDetails(FaultItem fault) {
        Intent intent = new Intent(this, acceptedreqdetail.class);
        intent.putExtra("ackno", fault.getAckno());
        intent.putExtra("status", fault.getStatus());
        intent.putExtra("device", fault.getDevice());
        intent.putExtra("description", fault.getDescription());
        intent.putExtra("time", fault.getTime());
        intent.putExtra("station", fault.getStation());
        intent.putExtra("token", token);
        startActivity(intent);
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                // Create the JSON object
                JSONObject postData = new JSONObject();
                postData.put("key1", "value1");
                postData.put("key2", "value2");

                // Write the JSON object to the output stream
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(postData.toString());
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
                    JSONObject jsonResponse = new JSONObject(result);

                    // Check for token expiration response
                    if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                        // Token is expired or invalid, redirect to login page
                        showTokenExpiredAlert();
                    } else {
                        // Continue processing other responses
                        // Parse the JSON response
                        // Update the UI
                        parseAndDisplayResponse(jsonResponse);
                    }
                } catch (JSONException e) {
                    Log.e("API Error", "Error parsing JSON response", e);
                }
            } else {
                // Handle the case where the result is null
                Toast.makeText(l1acceptedreq.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                if (jsonResponse.has("success")) {
                    // Check if "success" is an array or a single object
                    String success = jsonResponse.getString("success");
                    Object successObject = jsonResponse.get("success");
                    if("No data".equals(success))
                    {
                        t1.setVisibility(View.VISIBLE);
                    }
                    if (successObject instanceof JSONObject) {
                        // Single object
                        JSONObject singleObject = (JSONObject) successObject;
                        processJsonObject(singleObject);
                    } else if (successObject instanceof JSONArray) {
                        // Array of objects
                        JSONArray jsonArray = (JSONArray) successObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            processJsonObject(jsonObject);
                        }
                    }
                } else if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                    // Token is expired or invalid, redirect to login page
                    showTokenExpiredAlert();
                } else {
                    // Handle other cases or log an error
                    Log.e("API Error", "Unexpected JSON format");
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void processJsonObject(JSONObject jsonObject) throws JSONException {
            String ackno = jsonObject.getString("ackno");
            String status = jsonObject.getString("status");
            String device = jsonObject.getString("device");
            String station = jsonObject.getString("station");
            String time = jsonObject.getString("a_time");
            String description = jsonObject.getString("description");

            // Create a FaultItem and add it to the list
            FaultItem faultItem = new FaultItem(ackno, status, device, description, time, station);
            faultList.add(faultItem);
        }


        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(l1acceptedreq.this);
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
            SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("AccessToken-l1",null);
            editor.apply();
            Intent intent = new Intent(l1acceptedreq.this, l1login.class);
            startActivity(intent);
            finish();
        }
    }
}
