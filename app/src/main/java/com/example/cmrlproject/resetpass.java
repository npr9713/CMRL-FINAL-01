package com.example.cmrlproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class resetpass extends AppCompatActivity {
    String email;
    EditText e1;
    Button b1;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpass);
        Intent intent = getIntent();
        email= intent.getStringExtra("email");
        e1 = findViewById(R.id.Password2);
        b1 = findViewById(R.id.submit);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ResetTask().execute();
            }
        });

    }
    private class ResetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_reset"; // Replace with your actual API URL

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set up HTTP POST request
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Create JSON request body manually
                String jsonBody = "{\"email\":\"" + email + "\",\"pass\":\"" + e1.getText().toString()+ "\"}";
                Log.d("JSON",jsonBody);
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(jsonBody);
                os.flush();
                os.close();

                // Get response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();
            } catch (Exception e) {
                Log.e("HTTP Request", "Error: " + e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Log the response from the API
                Log.d("API Response", result);

                // Check for success or handle other conditions based on the response
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String message = jsonResponse.optString("success");

                    if ("Update is successfull".equals(message)) {

                        showSuccessAlertOtp();

                    } else if ("Update is unsuccessfull".equals(message)) {
                        // Handle EID not found case (show an error message, etc.)
                        showEIDNotFoundAlertOtp();
                    } else {
                        // Handle other conditions or show appropriate message
                        Log.e("API Error", "Unexpected response: " + message);
                        // You may want to handle other conditions here
                    }
                } catch (JSONException e) {
                    Log.e("API Error", "Error parsing JSON response", e);
                }
            } else {
                Log.e("API Error", "Failed to get response");
            }
        }


    }
    private void showSuccessAlertOtp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password changed");
        builder.setMessage("Please login again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(resetpass.this,l1login.class);
                startActivity(intent);
            }
        });
        builder.show();
    }


    private void showEIDNotFoundAlertOtp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Please Try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(resetpass.this,forgotpass.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

}
