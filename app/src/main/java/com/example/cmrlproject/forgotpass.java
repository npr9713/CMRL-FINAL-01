package com.example.cmrlproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class forgotpass extends AppCompatActivity {
    ImageButton i1;
    Button b1,b2;
    String flag;
    EditText e1,e2;
    TextView e3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpass);

        i1=findViewById(R.id.backbutton);
        b1=findViewById(R.id.otpbut);
        b2=findViewById(R.id.submitbut);
        e1=findViewById(R.id.mailid);
        e2=findViewById(R.id.resetotp);
        e3 = findViewById(R.id.textView20);
        Intent i = getIntent();
        flag=i.getStringExtra("flag");
        Log.d("flag",flag);
        e2.setVisibility(View.GONE);
        e3.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpRequestTask().execute();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckTask().execute();
            }
        });
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(flag, "1"))
                {
                    Intent i=new Intent(forgotpass.this, l1login.class);
                    startActivity(i);
                } else if (Objects.equals(flag, "2")) {
                    Intent i=new Intent(forgotpass.this, cmologin.class);
                    startActivity(i);

                } else if (Objects.equals(flag, "3")) {
                    Intent i=new Intent(forgotpass.this, zjelogin.class);
                    startActivity(i);

                }
                else {
                    Intent i=new Intent(forgotpass.this, storelogin.class);
                    startActivity(i);
                }

            }
        });
    }
    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_forget_password"; // Replace with your actual API URL

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set up HTTP POST request
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Create JSON request body manually
                String jsonBody = "{\"email\":\"" + e1.getText().toString() + "\"}";
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

                    if ("Otp sent".equals(message)) {

                        showSuccessAlert();

                    } else if ("EID not found".equals(message)) {
                        // Handle EID not found case (show an error message, etc.)
                        showEIDNotFoundAlert();
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
    private void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Otp sent");
        builder.setMessage("Four digit otp has been sent to your registered email");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                e2.setVisibility(View.VISIBLE);
                e3.setVisibility(View.VISIBLE);
                b2.setVisibility(View.VISIBLE);
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

            }
        });
        builder.show();
    }
    private class CheckTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_check_otp"; // Replace with your actual API URL

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set up HTTP POST request
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Create JSON request body manually
                String jsonBody = "{\"email\":\"" + e1.getText().toString() + "\",\"otp\":\"" + e2.getText().toString()+ "\"}";
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

                    if ("Correct Otp".equals(message)) {

                        showSuccessAlertOtp();

                    } else if ("Incorrect Otp".equals(message)) {
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
        builder.setTitle("Correct Otp");
        builder.setMessage("Proceed with reset password");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(forgotpass.this,resetpass.class);
                intent.putExtra("email",e1.getText().toString());
                startActivity(intent);
            }
        });
        builder.show();
    }


    private void showEIDNotFoundAlertOtp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incorrect Otp");
        builder.setMessage("The provied otp is incorrect try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        builder.show();
    }
}
