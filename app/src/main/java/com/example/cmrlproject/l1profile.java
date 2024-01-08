package com.example.cmrlproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class l1profile extends AppCompatActivity {
    Button b1, punch;
    String token;
    String eid;
    ImageButton b2, b3;
    TextView t1, t2, t3, t4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l1profile);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        Log.d("token", token);
        // Initialize views
        t1 = findViewById(R.id.enamep);
        t2 = findViewById(R.id.eidp);
        t3 = findViewById(R.id.eemailp);
        t4 = findViewById(R.id.ephonep);
        b1 = findViewById(R.id.logoutb);
        b2 = findViewById(R.id.logsbut);
        b3 = findViewById(R.id.homebut);
        punch = findViewById(R.id.punchIn);

        // Execute the AsyncTask to make the HTTP POST request for profile data retrieval
        new ProfileDataTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_profile");

        // Other initialization code
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("AccessToken-l1",null);
                editor.apply();
                Intent i = new Intent(l1profile.this, MainActivity.class);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1profile.this, l1acceptedreq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1profile.this, l1home.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        punch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (punch.getText().equals("Punch In")) {
                    new PunchInTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_punch");
                } else {
                    new PunchOutTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_punchout");
                }

            }
        });
    }

    // AsyncTask to handle profile data retrieval
    private class ProfileDataTask extends AsyncTask<String, Void, String> {
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
                    JSONObject jsonResponse = new JSONObject(result);

                    // Check for token expiration response
                    if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                        // Token is expired or invalid, redirect to login page
                        showTokenExpiredAlert();
                    } else if (jsonResponse.has("Active") && "1".equals(jsonResponse.getString("Active"))) {
                        punch.setText("Punch Out");
                        parseAndDisplayResponse(jsonResponse);
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
                Toast.makeText(l1profile.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                // Extract values from the JSON response
                eid = jsonResponse.getJSONObject("success").getString("eid");
                String name = jsonResponse.getJSONObject("success").getString("name");
                String email = jsonResponse.getJSONObject("success").getString("email");
                String phone = jsonResponse.getJSONObject("success").getString("phone");

                // Set the values to corresponding TextViews
                t1.setText(name);
                t2.setText(eid);
                t3.setText(email);
                t4.setText(phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(l1profile.this); // Pass the context of l1profile activity
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
            Intent intent = new Intent(l1profile.this, l1login.class); // Change to your login activity
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }

    // AsyncTask to handle punching in functionality
    private class PunchInTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                // Add the eid to the request body
                String requestBody = String.format("{\"eid\":\"%s\"}", eid);

                urlConnection.setDoOutput(true);

                // Write the request body to the output stream
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(requestBody);
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
                    JSONObject jsonResponse = new JSONObject(result);

                    // Check for token expiration response
                    if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                        // Token is expired or invalid, redirect to login page
                        showTokenExpiredAlert();
                    } else {
                        // Continue processing other responses
                        // Parse the JSON response
                        // Update the UI
                        handlePunchInResponse(jsonResponse);
                    }
                } catch (JSONException e) {
                    Log.e("API Error", "Error parsing JSON response", e);
                }
            } else {
                // Handle the case where the result is null
                Toast.makeText(l1profile.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }

        private void handlePunchInResponse(JSONObject jsonResponse) {
            try {
                // Extract values from the JSON response
                boolean status = jsonResponse.getBoolean("status");
                int success = jsonResponse.getInt("success");

                if (status && success == 1) {
                    punch.setText("Punch Out");
                    showPunchOutSuccessAlert();
                } else {
                    // Punch in failed
                    showPunchOutFailureAlert();
                }
            } catch (JSONException e) {
                Log.e("API Error", "Error parsing JSON response", e);
            }
        }
    }
    private class PunchOutTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                // Add the eid to the request body
                String requestBody = String.format("{\"eid\":\"%s\"}", eid);

                urlConnection.setDoOutput(true);

                // Write the request body to the output stream
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(requestBody);
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
                    JSONObject jsonResponse = new JSONObject(result);

                    // Check for token expiration response
                    if (jsonResponse.has("message") && "Unauthorized: Invalid token".equals(jsonResponse.getString("message"))) {
                        // Token is expired or invalid, redirect to login page
                        showTokenExpiredAlert();
                    } else {
                        // Continue processing other responses
                        // Parse the JSON response
                        // Update the UI
                        handlePunchInResponse(jsonResponse);
                    }
                } catch (JSONException e) {
                    Log.e("API Error", "Error parsing JSON response", e);
                }
            } else {
                // Handle the case where the result is null
                Toast.makeText(l1profile.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }

        private void handlePunchInResponse(JSONObject jsonResponse) {
            try {
                // Extract values from the JSON response
                boolean status = jsonResponse.getBoolean("status");
                int success = jsonResponse.getInt("success");

                if (status && success == 1) {
                    punch.setText("Punch In");
                    showPunchOutSuccessAlert();
                } else {
                    // Punch in failed
                    showPunchOutFailureAlert();
                }
            } catch (JSONException e) {
                Log.e("API Error", "Error parsing JSON response", e);
            }
        }
    }
    private void showTokenExpiredAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(l1profile.this); // Pass the context of l1profile activity
        builder.setTitle("Session Expired");
        builder.setMessage("Your session has expired. Please log in again.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                redirectToLoginPage();
            }
        });
        builder.show();
    }

    private void showPunchOutSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(l1profile.this);
        builder.setTitle("Punched In Successfully");
        builder.setMessage("You have been punched out successfully.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle any specific action or leave it empty
            }
        });
        builder.show();
    }

    private void showPunchOutFailureAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(l1profile.this);
        builder.setTitle("Punch In Failed");
        builder.setMessage("Punch out was unsuccessful. Please try again.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle any specific action or leave it empty
            }
        });
        builder.show();
    }

    private void redirectToLoginPage() {
        SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AccessToken-l1",null);
        editor.apply();
        Intent intent = new Intent(l1profile.this, l1login.class); // Change to your login activity
        startActivity(intent);
        finish();  // Optional: Close the current activity if needed
    }

}



