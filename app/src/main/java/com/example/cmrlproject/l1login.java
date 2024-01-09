package com.example.cmrlproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class l1login extends AppCompatActivity {
    Button b1, b2;
    String token;
    TextView t1;
    EditText e1,  e3;
    String eid;
    String f="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l1login);


        e1 = findViewById(R.id.eidl);

        e3 =  findViewById(R.id.passwordl);
        b1 =  findViewById(R.id.loginb);
        b2 =  findViewById(R.id.resetpass);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (e1.getText().toString().trim().length() == 0  || e3.getText().toString().trim().length() == 0) {
                    t1.setText("Enter all values to continue!!");
                }  else {
                    eid = e1.getText().toString();
                    String password = e3.getText().toString();

                    // Make HTTP request in the background
                    new HttpRequestTask().execute(eid, password);
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(l1login.this, forgotpass.class);
                i.putExtra("flag",f);
                startActivity(i);

            }
        });
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String eid = params[0];
            String password = params[1];
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_login"; // Replace with your actual API URL

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set up HTTP POST request
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Create JSON request body manually
                String jsonBody = "{\"employeeId\":\"" + eid + "\",\"password\":\"" + password + "\"}";
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
                    String message = jsonResponse.optString("message");

                    switch (message) {
                        case "Login successful":
                            token = jsonResponse.optString("token");
                            showSuccessAlert(token);
                            Load(token);
                            break;
                        case "Invalid password":
                            // Handle invalid password case (show an error message, etc.)
                            showInvalidPasswordAlert();
                            break;
                        case "EID not found":
                            // Handle EID not found case (show an error message, etc.)
                            showEIDNotFoundAlert();
                            break;
                        default:
                            // Handle other conditions or show appropriate message
                            Log.e("API Error", "Unexpected response: " + message);
                            // You may want to handle other conditions here
                            break;
                    }
                } catch (JSONException e) {
                    Log.e("API Error", "Error parsing JSON response", e);
                }
            } else {
                Log.e("API Error", "Failed to get response");
            }
        }


    }
    private void showSuccessAlert(String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Successful");
        builder.setMessage("You have successfully logged in.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Redirect to l1home page and pass the token
                Intent intent = new Intent(l1login.this, l1home.class);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();  // Optional: Close the current activity if needed
            }
        });
        builder.show();
    }
    private void Load(String token)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AccessToken-l1",token);
        editor.apply();

    }
    private void showInvalidPasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invalid Password");
        builder.setMessage("The provided password is incorrect.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle OK button click if needed
            }
        });
        builder.show();
    }
    private void showEIDNotFoundAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EID Not Found");
        builder.setMessage("The provided Employee ID was not found.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle OK button click if needed
            }
        });
        builder.show();
    }
}
