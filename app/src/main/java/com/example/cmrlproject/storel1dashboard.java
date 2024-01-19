package com.example.cmrlproject;

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

import androidx.appcompat.app.AppCompatActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class storel1dashboard extends AppCompatActivity {
    TextView tvpending, tvhandovered, tvhsf, tvclosed,tvas;
    ImageButton b1,b2,b3;
    PieChart pieChart;
    String token;

    JSONArray successArray;
    String c_count;
    String non_count;
    String pr_count;
    String as_count;
    String afs_count;
    String ho_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storel1dashboard);
        Intent i = getIntent();
        new storel1dashboard.HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/store_dashboard");
        token=  i.getStringExtra("token");
        b1=findViewById(R.id.sparemov);
        b2=findViewById(R.id.homebut);
        b3=findViewById(R.id.profilebut);
        tvpending=findViewById(R.id.tvpending);
        tvhandovered=findViewById(R.id.tvhandovered);
        tvhsf=findViewById(R.id.tvafs);
        tvclosed=findViewById(R.id.tvclosed);
        tvas = findViewById(R.id.tvas);
        pieChart = findViewById(R.id.piechart);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1dashboard.this, storel1home.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1dashboard.this, storel1_sparemov.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1dashboard.this, storel1profile.class);
                i.putExtra("token",token);
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
                Toast.makeText(storel1dashboard.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }
        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                successArray = jsonResponse.getJSONArray("success");

                // Iterate through the success array

                JSONObject successObject = successArray.getJSONObject(0);

                // Get values from JSON
                c_count = successObject.getString("c_count");
                pr_count = successObject.getString("pr_count");
                afs_count = successObject.getString("afs_count");
                as_count = successObject.getString("as_count");
                ho_count = successObject.getString("ho_count");
                setData();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1dashboard.this); // Pass the context of l1home activity
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
            Intent intent = new Intent(storel1dashboard.this, cmologin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

    }
    private void setData() {
        // Set the percentage of language used
        tvpending.setText(pr_count);
        tvclosed.setText(c_count);
        tvhsf.setText(afs_count);
        tvas.setText(as_count);
        tvhandovered.setText(ho_count);

        // Set the data and color to the pie chart
        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "pending",
                        Integer.parseInt(tvpending.getText().toString()),
                        Color.parseColor("#FFA500")));
        pieChart.addPieSlice(
                new PieModel(
                        "assigned_spare",
                        Integer.parseInt(tvas.getText().toString()),
                        Color.parseColor("#7F00FF"))); // Corrected color code
        pieChart.addPieSlice(
                new PieModel(
                        "handover",
                        Integer.parseInt(tvhandovered.getText().toString()),
                        Color.parseColor("#00FF00")));
        pieChart.addPieSlice(
                new PieModel(
                        "Closed",
                        Integer.parseInt(tvclosed.getText().toString()),
                        Color.parseColor("#0000FF")));
// Add two more data points
        pieChart.addPieSlice(
                new PieModel(
                        "against faulty spare",
                        Integer.parseInt(tvhsf.getText().toString()),
                        Color.parseColor("#FF0000"))); // Purple// Brown

// To animate the pie chart
        pieChart.startAnimation();

    }
}