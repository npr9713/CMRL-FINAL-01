package com.example.cmrlproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class cmodashboard extends AppCompatActivity {
    TextView tvopen, tvassign, tvin, tvclosed,tvns,tvsp;
    ImageButton b1,b2,b3,b4;
    PieChart pieChart;
    JSONArray successArray;
    String c_count,token;
    String o_count;
    String in_count;
    String a_count;
    String ns_count;
    String sp_count;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cmodashboard);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        new cmodashboard.HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/cmo_dashboard");
        b1=(ImageButton)findViewById(R.id.homebut);
        b2=(ImageButton)findViewById(R.id.addempbut);
        b3=(ImageButton)findViewById(R.id.viewreqbut);
        b4=(ImageButton)findViewById(R.id.profilebut);

        tvopen = findViewById(R.id.tvopen);
        tvassign = findViewById(R.id.tvas);
        tvin = findViewById(R.id.tvin);
        tvclosed = findViewById(R.id.tvclosed);
        tvns = findViewById(R.id.tvns);
        tvsp = findViewById(R.id.tvsr);
        pieChart = findViewById(R.id.piechart);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmodashboard.this, cmohome.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmodashboard.this, cmoaddemp.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmodashboard.this, cmoviewreq.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmodashboard.this, cmoprofile.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });



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
//                JSONObject postData = new JSONObject();
//                postData.put("key1", "value1");
//                postData.put("key2", "value2");

                // Write the JSON object to the output stream
//                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
////                outputStream.writeBytes(postData.toString());
//                outputStream.flush();
//                outputStream.close();

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
                Toast.makeText(cmodashboard.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }
        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                 successArray = jsonResponse.getJSONArray("success");

                // Iterate through the success array

                    JSONObject successObject = successArray.getJSONObject(0);

                    // Get values from JSON
                     c_count = successObject.getString("c_count");
                     o_count = successObject.getString("o_count");
                     in_count = successObject.getString("in_count");
                     a_count = successObject.getString("a_count");
                     ns_count = successObject.getString("ns_count");
                     sp_count = successObject.getString("sr_count");
                setData();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(cmodashboard.this); // Pass the context of l1home activity
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
            Intent intent = new Intent(cmodashboard.this, cmologin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

    }

    private void setData() {
            // Set the percentage of language used
            tvopen.setText((o_count));
            tvclosed.setText(c_count);
            tvin.setText(in_count);
            tvassign.setText(a_count);
            tvns.setText(ns_count);
            tvsp.setText(sp_count);

            // Set the data and color to the pie chart
            // Set the data and color to the pie chart
            pieChart.addPieSlice(
                    new PieModel(
                            "Open",
                            Integer.parseInt(tvopen.getText().toString()),
                            Color.parseColor("#FFFF00")));
            pieChart.addPieSlice(
                    new PieModel(
                            "Assigned",
                            Integer.parseInt(tvassign.getText().toString()),
                            Color.parseColor("#00FF00"))); // Corrected color code
            pieChart.addPieSlice(
                    new PieModel(
                            "In Progress",
                            Integer.parseInt(tvin.getText().toString()),
                            Color.parseColor("#FF0000")));
            pieChart.addPieSlice(
                    new PieModel(
                            "Closed",
                            Integer.parseInt(tvclosed.getText().toString()),
                            Color.parseColor("#4169C8")));
// Add two more data points
            pieChart.addPieSlice(
                    new PieModel(
                            "Spare Request",
                            Integer.parseInt(tvsp.getText().toString()),
                            Color.parseColor("#A020F0"))); // Purple
            pieChart.addPieSlice(
                    new PieModel(
                            "Need Support",
                            Integer.parseInt(tvns.getText().toString()),
                            Color.parseColor("#964B00"))); // Brown

// To animate the pie chart
            pieChart.startAnimation();

        }

}