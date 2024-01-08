package com.example.cmrlproject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class acceptedreqdetail extends AppCompatActivity {
    ImageButton b1, b2, b3;
    Button progress, closed, spare,need;
    TextView t1, t2, t3, t4, t5, t6;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    Location location;
    String Latitude;
    String Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptedreqdetail);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    location = locationResult.getLastLocation();
                    if (location != null) {
                        // Log latitude and longitude
                       Latitude= Double.toString(location.getLatitude());
                       Longitude = Double.toString(location.getLongitude());
                        Log.d("Location", "Latitude: " + Latitude + ", Longitude: " + Longitude);
                    }
                }
            }
        };
        requestLocationUpdates();

        b1 = findViewById(R.id.homebut);
        b2 = findViewById(R.id.profilebut);
        b3 = findViewById(R.id.logsbut);
        progress = findViewById(R.id.progress);
        spare = findViewById(R.id.sparereqb);
        closed = findViewById(R.id.closeb);
        need = findViewById(R.id.need);
        t1 = findViewById(R.id.acceptedackno);
        t2 = findViewById(R.id.accepteddateandtime);
        t3 = findViewById(R.id.acceptedstation);
        t4 = findViewById(R.id.accepteddevice);
        t5 = findViewById(R.id.accepteddeviceno);
        t6 = findViewById(R.id.acceptedstatus);

        Intent intent = getIntent();
        String token = intent.getStringExtra("token");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(acceptedreqdetail.this, l1home.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(acceptedreqdetail.this, l1profile.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(acceptedreqdetail.this, l1acceptedreq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HttpRequestTask(token).execute();
            }
        });
        closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CloseRequestTask(token).execute();
            }
        });
        spare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SpareRequestTask(token).execute();
            }
        });
        need.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NeedRequestTask(token).execute();
            }
        });

        if (intent != null && intent.hasExtra("ackno")) {
            String ackno = intent.getStringExtra("ackno");
            String date = intent.getStringExtra("time");
            String station = intent.getStringExtra("station");
            String device = intent.getStringExtra("device");
            String deviceno = intent.getStringExtra("description");
            String status = intent.getStringExtra("status");

            t1.setText(ackno);
            t2.setText(date);
            t3.setText(station);
            t4.setText(device);
            t5.setText(deviceno);
            t6.setText(status);
            if ("Assigned".equals(status)) {
                progress.setVisibility(View.VISIBLE);
                spare.setVisibility(View.GONE);
                closed.setVisibility(View.GONE);
                need.setVisibility(View.GONE);
            } else if ("In Progress".equals(status)) {
                progress.setVisibility(View.GONE);
                spare.setVisibility(View.VISIBLE);
                closed.setVisibility(View.VISIBLE);
                need.setVisibility(View.VISIBLE);
            }
            else if ("Spare Request".equals(status)) {
                progress.setVisibility(View.GONE);
                spare.setVisibility(View.GONE);
                closed.setVisibility(View.VISIBLE);
                need.setVisibility(View.GONE);
            }
            else if ("Need Support".equals(status)) {
                progress.setVisibility(View.GONE);
                spare.setVisibility(View.GONE);
                closed.setVisibility(View.VISIBLE);
                need.setVisibility(View.GONE);
            }
        }
    }

    private void requestLocationUpdates() {
        // Check for location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Request location updates
                fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
            } else {
                // Request location permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(5000)   // 5 seconds
                .setFastestInterval(3000)  // 3 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        private final String token;

        // Constructor to receive token
        public HttpRequestTask(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_in_progress";

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

                    if (jsonResponse.has("success")&&jsonResponse.has("distance")) {

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
            int distance = jsonResponse.getInt("distance");

            if (success == 1&& distance==1) {
                showAssignmentSuccessAlert();
            } else {

                showUnsuccessfulAssignmentAlert();
            }
        }

        private void showUnsuccessfulAssignmentAlert() {
            progress.setVisibility(View.VISIBLE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("Assignment Failed");
            builder.setMessage("The fault assignment to in progress was unsuccessful.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();
        }

        private void showAssignmentSuccessAlert() {
            progress.setVisibility(View.GONE);
            spare.setVisibility(View.VISIBLE);
            closed.setVisibility(View.VISIBLE);
            need.setVisibility(View.VISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been changed to in progress successfully.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();
        }

        private void showTokenExpiredAlert() {

            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
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
            Intent intent = new Intent(acceptedreqdetail.this, l1login.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }
    private class SpareRequestTask extends AsyncTask<Void, Void, String> {
        private final String token;

        // Constructor to receive token
        public SpareRequestTask(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_spare";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Create JSON request body
                    String jsonBody = createJsonackno();

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
            progress.setVisibility(View.GONE);
            need.setVisibility(View.GONE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been changed to Spare Requested successfully.");
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
            progress.setVisibility(View.VISIBLE);
            need.setVisibility(View.GONE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("CLOSED FAILED!");
            builder.setMessage("The fault assignment to Spare request  was unsuccessful.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();

        }

        private void showTokenExpiredAlert() {

            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
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
            Intent intent = new Intent(acceptedreqdetail.this, l1login.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
        private void redirectToReqPage() {
            Intent intent = new Intent(acceptedreqdetail.this, l1acceptedreq.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }
    private class CloseRequestTask extends AsyncTask<Void, Void, String> {
        private final String token;

        // Constructor to receive token
        public CloseRequestTask(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_closed";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Create JSON request body
                    String jsonBody = createJsonackno();

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
            progress.setVisibility(View.GONE);
            need.setVisibility(View.GONE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been changed to closed successfully.");
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
            progress.setVisibility(View.VISIBLE);
            need.setVisibility(View.GONE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("CLOSED FAILED!");
            builder.setMessage("The fault assignment to closed was unsuccessful.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();

        }

        private void showTokenExpiredAlert() {

            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
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
            Intent intent = new Intent(acceptedreqdetail.this, l1login.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
        private void redirectToReqPage() {
            Intent intent = new Intent(acceptedreqdetail.this, l1acceptedreq.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }
    private class NeedRequestTask extends AsyncTask<Void, Void, String> {
        private final String token;

        // Constructor to receive token
        public NeedRequestTask(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_need";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Create JSON request body
                    String jsonBody = createJsonackno();

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
            progress.setVisibility(View.GONE);
            need.setVisibility(View.GONE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("Fault Assigned");
            builder.setMessage("The fault has been changed to Need Request successfully.");
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
            progress.setVisibility(View.VISIBLE);
            need.setVisibility(View.GONE);
            spare.setVisibility(View.GONE);
            closed.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
            builder.setTitle("CLOSED FAILED!");
            builder.setMessage("The fault assignment to Need Request was unsuccessful.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();

        }

        private void showTokenExpiredAlert() {

            AlertDialog.Builder builder = new AlertDialog.Builder(acceptedreqdetail.this);
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
            Intent intent = new Intent(acceptedreqdetail.this, l1login.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
        private void redirectToReqPage() {
            Intent intent = new Intent(acceptedreqdetail.this, l1acceptedreq.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }
    @SuppressLint("DefaultLocale")
    private String createJsonBody() {
        try {
            // Include latitude, longitude, and ackno in the JSON body
            return String.format("{\"ackno\":\"%s\", \"latitude\":\"%s\", \"longitude\":\"%s\", \"station\":\"%s\"}",
                    t1.getText().toString(), Latitude, Longitude, t3.getText().toString());

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
    private String createJsonackno() {
        try {
            // Include latitude, longitude, and ackno in the JSON body
            return String.format("{\"ackno\":\"%s\"}",
                    t1.getText().toString());

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
}