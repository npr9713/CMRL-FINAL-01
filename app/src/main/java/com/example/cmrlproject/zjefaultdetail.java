package com.example.cmrlproject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class zjefaultdetail extends AppCompatActivity{
    ImageButton b1,b2,b3,b4,b5;
    Button assign,accept,close;
    private JSONArray successArray;
    private String ackno;
    private String a_time;
    private String i_time;
    private String n_time;
    private String s_time;
    private String c_time;
    private String t_time;
    private String status;
    private String emp;
    String token;
    TextView t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12;
    TextView t13,t14,t15,t16,t17,t18;
    private String Selected_emp_name;
    private String Selected_emp_id;
    private static class Employee
    {
        String name;
        String id;
        Employee(String name,String id)
        {
            this.name = name;
            this.id = id;
        }
        public String toString()
        {
            return name;
        }


    }
    List<Employee> employees = new ArrayList<>();
    private ArrayAdapter<Employee> adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zjefaultdetail);
        Intent i = getIntent();
        token = i.getStringExtra("token");
        new zjefaultdetail.HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/l1_view");
        new zjefaultdetail.GetRequest().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/ze_getAckno");
        b1=(ImageButton)findViewById(R.id.homebut);
        b2=(ImageButton)findViewById(R.id.approvereq);
        b3=(ImageButton)findViewById(R.id.viewreqbut);
        b4 = findViewById(R.id.profilebut);
        b5=findViewById(R.id.dashboardbut);
        t1=(TextView)findViewById(R.id.acceptedackno);
        t2=(TextView)findViewById(R.id.accepteddateandtime);
        t3=(TextView)findViewById(R.id.acceptedstation);
        t4=(TextView)findViewById(R.id.accepteddevice);
        t5=(TextView)findViewById(R.id.accepteddeviceno);
        t6=(TextView)findViewById(R.id.acceptedstatus);
        t7=(TextView)findViewById(R.id.emp_value);
        t8=(TextView)findViewById(R.id.atime_value);
        t9 = (TextView)findViewById(R.id.itime_value);
        t10 = (TextView)findViewById(R.id.ntime_value);
        t11 = (TextView)findViewById(R.id.stime_value);
        t12 = (TextView)findViewById(R.id.ctime_value);
        t13=(TextView)findViewById(R.id.emp);
        t14=(TextView)findViewById(R.id.a_time);
        t15 = (TextView)findViewById(R.id.i_time);
        t16 = (TextView)findViewById(R.id.n_time);
        t17 = (TextView)findViewById(R.id.s_time);
        t18 = (TextView)findViewById(R.id.c_time);
        assign=(Button)findViewById(R.id.submit);
        Spinner employeeSpinner = (Spinner) findViewById(R.id.employeeSpinner);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employees);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeSpinner.setAdapter(adapter);

        employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("selected", "itemSelected");
                Employee selectedEmployee = (Employee) parentView.getItemAtPosition(position);
                Selected_emp_name = selectedEmployee.name;
                Selected_emp_id = selectedEmployee.id;
                Log.d("name", Selected_emp_id);

                // Use the selectedEmployeeName and selectedEmployeeId as needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected if needed
            }
        });

// Log the number of items in the employees list
        Log.d("Spinner", "Number of items: " + employees.size());

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjefaultdetail.this, zjehome.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjefaultdetail.this, zjeapprovereq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjefaultdetail.this, zjeviewreq.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjefaultdetail.this, zjeprofile.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(zjefaultdetail.this, zjedashboard.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        //checking whether the fault is available or not and we have to see properly
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ackno")) {
            ackno = intent.getStringExtra("ackno");
            String date = intent.getStringExtra("date");
            String station = intent.getStringExtra("station");
            String device = intent.getStringExtra("device");
            String deviceno = intent.getStringExtra("deviceno");
            status = intent.getStringExtra("status");

            t1.setText(ackno);
            t2.setText(date);
            t3.setText(station);
            t4.setText(device);
            t5.setText(deviceno);
            t6.setText(status);
            if(status.equalsIgnoreCase("Open"))
            {
                t7.setVisibility(View.GONE);
                t8.setVisibility(View.GONE);
                t9.setVisibility(View.GONE);
                t10.setVisibility(View.GONE);
                t11.setVisibility(View.GONE);
                t12.setVisibility(View.GONE);
                t13.setVisibility(View.GONE);
                t14.setVisibility(View.GONE);
                t15.setVisibility(View.GONE);
                t16.setVisibility(View.GONE);
                t17.setVisibility(View.GONE);
                t18.setVisibility(View.GONE);
            }
            if(status.equalsIgnoreCase("Closed")||status.equalsIgnoreCase("In Progress")||status.equalsIgnoreCase("Spare Request")||
                    status.equalsIgnoreCase("Need Support")||status.equalsIgnoreCase("Assigned"))
            {
                employeeSpinner.setVisibility(View.GONE);
                assign.setVisibility(View.GONE);
            }
            if(status.equalsIgnoreCase("Assigned"))
            {

                t9.setVisibility(View.GONE);
                t10.setVisibility(View.GONE);
                t11.setVisibility(View.GONE);
                t12.setVisibility(View.GONE);
                t15.setVisibility(View.GONE);
                t16.setVisibility(View.GONE);
                t17.setVisibility(View.GONE);
                t18.setVisibility(View.GONE);
            }
        }
        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new zjefaultdetail.AssignRequest().execute();

            }
        });


    }
    private class GetRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                String jsonbody = createJsonBody2();
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                // Create the JSON object


                // Write the JSON object to the output stream
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(jsonbody.toString());
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
                Toast.makeText(zjefaultdetail.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }
        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                JSONObject successObject = jsonResponse.getJSONObject("success");

                // Get time fields from JSON
                a_time = successObject.optString("a_time_ist", "");
                i_time = successObject.optString("i_time_ist", "");
                c_time = successObject.optString("c_time_ist", "");
                n_time = successObject.optString("n_time_ist", "");
                s_time = successObject.optString("s_time_ist", "");
                emp = successObject.optString("eid");
                Log.d("emp", emp);
                Log.d("a_time", a_time);
                Log.d("i_time", i_time);
                Log.d("c_time", c_time);
                Log.d("n_time", n_time);
                Log.d("s_time", s_time);
                if (!status.equalsIgnoreCase("Open")) {
                    t7.setText(emp);
                    t8.setText(a_time);
                    t9.setText(i_time);
                    t10.setText(n_time);
                    t11.setText(s_time);
                    t12.setText(c_time);
                }
                if(i_time.equalsIgnoreCase("null"))
                {
                    t9.setVisibility(View.GONE);
                    t15.setVisibility(View.GONE);
                }
                if(n_time.equalsIgnoreCase("null"))
                {
                    t10.setVisibility(View.GONE);
                    t16.setVisibility(View.GONE);
                }
                if(s_time.equalsIgnoreCase("null"))
                {
                    t11.setVisibility(View.GONE);
                    t17.setVisibility(View.GONE);
                }
                if(c_time.equalsIgnoreCase("null"))
                {
                    t12.setVisibility(View.GONE);
                    t18.setVisibility(View.GONE);
                }



                // Notify the adapter that the data set has changed
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder( zjefaultdetail.this); // Pass the context of l1home activity
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
            Intent intent = new Intent(zjefaultdetail.this, zjelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

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
                Toast.makeText(zjefaultdetail.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
            }
        }
        private void parseAndDisplayResponse(JSONObject jsonResponse) {
            try {
                successArray = jsonResponse.getJSONArray("success");

                // Iterate through the success array
                for (int i = 0; i < successArray.length(); i++) {
                    JSONObject successObject = successArray.getJSONObject(i);

                    // Get values from JSON
                    String eid = successObject.getString("eid");

                    String name = successObject.getString("name");
                    Log.d("eid",eid);
                    employees.add(new Employee(name,eid));

                }
                adapter.notifyDataSetChanged();

                // Notify the adapter that the data set has changed

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder( zjefaultdetail.this); // Pass the context of l1home activity
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
            Intent intent = new Intent(zjefaultdetail.this, zjelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

    }
    private class AssignRequest extends AsyncTask<Void, Void, String> {
//        private final String token;

        // Constructor to receive token
//        public NeedRequestTask(String token) {
//            this.token = token;
//        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/cmo_assign";

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
//                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);

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
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(zjefaultdetail.this);
            builder.setTitle("Fault Created");
            builder.setMessage("The New fault has been assigned successfully.");
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
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(zjefaultdetail.this);
            builder.setTitle("Fault Creation Failed");
            builder.setMessage("The fault assignment was unsucessfull.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // You can add any specific action or leave it empty
                }
            });
            builder.show();

        }

        private void showTokenExpiredAlert() {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(zjefaultdetail.this);
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
            Intent intent = new Intent(zjefaultdetail.this, zjelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
        private void redirectToReqPage() {
            Intent intent = new Intent(zjefaultdetail.this, zjeviewreq.class);
            intent.putExtra("token", token);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }
    }
    @SuppressLint("DefaultLocale")
    private String createJsonBody() {
        try {
            // Include latitude, longitude, and ackno in the JSON body
            return String.format("{\"ackno\":\"%s\", \"eid\":\"%s\"}",
                    ackno,Selected_emp_id
            );

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
    private String createJsonBody2() {
        try {
            // Include latitude, longitude, and ackno in the JSON body
            return String.format("{\"ackno\":\"%s\"}",
                    ackno
            );

        } catch (Exception e) {
            Log.e("JSON Error", "Error creating JSON body: " + e.getMessage());
            return null;
        }
    }
}