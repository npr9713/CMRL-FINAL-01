package com.example.cmrlproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class cmoaddemp extends AppCompatActivity {
    Button b1;
    ImageButton i1,i2,i3,i4,i5;
    EditText e1,e2,e3,e4,e5;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cmoaddemp);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        e1=(EditText) findViewById(R.id.l1name);
        e2=(EditText) findViewById(R.id.l1empid);
        e3=(EditText) findViewById(R.id.l1phoneno);
        e4=(EditText) findViewById(R.id.l1mailid);
        e5=(EditText) findViewById(R.id.l1password);
        i1=(ImageButton) findViewById(R.id.addempbut);
        i2=(ImageButton) findViewById(R.id.homebut);
        i3=(ImageButton) findViewById(R.id.viewreqbut);
        i4=(ImageButton)  findViewById(R.id.profilebut);
        i5=(ImageButton)findViewById(R.id.dashboardbut);
        b1=(Button) findViewById(R.id.adddetailb);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new cmoaddemp.CreateEmployee().execute();
            }
        });
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmoaddemp.this, cmohome.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });
        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmoaddemp.this, cmoviewreq.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmoaddemp.this, cmoprofile.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });
        i5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cmoaddemp.this, cmodashboard.class);
                i.putExtra("token", token);
                startActivity(i);

            }
        });

    }
    private class CreateEmployee extends AsyncTask<Void, Void, String> {
//        private final String token;

        // Constructor to receive token
//        public NeedRequestTask(String token) {
//            this.token = token;
//        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/add";

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
            }else{
                showUnsuccessfulAssignmentAlert();
            }
        }



        private void showAssignmentSuccessAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(cmoaddemp.this);
            builder.setTitle("Fault Created");
            builder.setMessage("The New employee has been created successfully.");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(cmoaddemp.this);
            builder.setTitle("Fault Creation Failed");
            builder.setMessage("The employee creation was unsucessfull.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();

        }

        private void showTokenExpiredAlert() {

            AlertDialog.Builder builder = new AlertDialog.Builder(cmoaddemp.this);
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
            Intent intent = new Intent(cmoaddemp.this, cmologin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
        private void redirectToReqPage() {
            Intent intent = new Intent(cmoaddemp.this, cmoviewreq.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }
    @SuppressLint("DefaultLocale")
    private String createJsonBody() {
        try {
            // Include latitude, longitude, and ackno in the JSON body
            return String.format("{\"name\":\"%s\", \"eid\":\"%s\", \"phone\":\"%s\", \"email\":\"%s\",\"password\":\"%s\"}",
                    e1.getText().toString(),e2.getText().toString(),e3.getText().toString(),e4.getText().toString(),e5.getText().toString());

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
}
