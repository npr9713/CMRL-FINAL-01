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

public class storel1_sparemov extends AppCompatActivity {
    ImageButton b1,b2,b3;
    String token;
    private JSONArray successArray;
    private ListView faultListView;
    private List<String> faultList;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storel1_sparemov);
        Intent i = getIntent();
        token=  i.getStringExtra("token");
        Log.d("token",token);
        new storel1_sparemov.HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/view_assigned_spare");
        faultListView = findViewById(R.id.faultListView);
        faultList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, faultList);
        faultListView.setAdapter(adapter);
        b1=findViewById(R.id.homebut);
        b2=findViewById(R.id.dashboardbut);
        b3=findViewById(R.id.profilebut);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov.this, storel1dashboard.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov.this, storel1profile.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1_sparemov.this, storel1home.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        faultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject successObject = successArray.getJSONObject(position);
                    Log.d("POSITION",Integer.toString(position));

                    openFaultDetails(successObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void openFaultDetails(JSONObject successObject) throws JSONException {
        Intent intent = new Intent(this, storel1_sparemov_req.class);
        String ackno = successObject.getString("ackno");
        Log.d("ACKNO",ackno);
        String station = successObject.getString("station");
        String mr_id = successObject.getString("mr_id");
        String spare_name = successObject.getString("spare_name");
        String spare_sno = successObject.getString("sno");
        String status = successObject.getString("status");
        Log.d("spare_name",spare_name);
        Log.d("spare_sno",spare_sno);


        intent.putExtra("token",token);
        intent.putExtra("ackno", ackno);
        intent.putExtra("sno", spare_sno);
        intent.putExtra("station", station);
        intent.putExtra("mr_id", mr_id);
        intent.putExtra("spare_name", spare_name);
        intent.putExtra("status", status);
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
                Toast.makeText(storel1_sparemov.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }
        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                successArray = jsonResponse.getJSONArray("success");

                // Iterate through the success array
                for (int i = 0; i < successArray.length(); i++) {
                    JSONObject successObject = successArray.getJSONObject(i);

                    // Get values from JSON
                    String station = successObject.getString("station");
                    String status = successObject.getString("status");
                    String mr_id = successObject.getString("mr_id");

                    // Add station and ackno to the list
                    String fault = station + ": " + status+": "+mr_id;
                    faultList.add(fault);
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1_sparemov.this); // Pass the context of l1home activity
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
            editor.putString("AccessToken-sl1",null);
            editor.apply();
            Intent intent = new Intent(storel1_sparemov.this, storelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

    }
}