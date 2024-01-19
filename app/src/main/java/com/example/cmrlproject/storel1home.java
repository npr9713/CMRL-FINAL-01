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
import android.widget.Spinner;
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

public class storel1home extends AppCompatActivity {
    private JSONArray successArray;
    ImageButton b1,b2,b3;
    String faulty_type,selected_status;
    String token;
    private ListView faultListView;
    private List<String> faultList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storel1home);
        Intent i = getIntent();
        token=  i.getStringExtra("token");
        faultListView = findViewById(R.id.faultListView);
        faultList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, faultList);
        faultListView.setAdapter(adapter);
        new storel1home.HttpRequestTask().execute("https://98bb-2401-4900-6323-51b1-741b-7ac2-15bb-9d07.ngrok-free.app/store_view");


        b1=findViewById(R.id.sparemov);
        b2=findViewById(R.id.dashboardbut);
        b3=findViewById(R.id.profilebut);
        Spinner faulttypeSpinner=findViewById(R.id.fault_type_Spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spare_request_type_codes, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        faulttypeSpinner.setAdapter(adapter);
        faulttypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the spinner
                faulty_type = parent.getItemAtPosition(position).toString();
                Log.d("faulty_type",faulty_type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String not_selected_fault = "true";
            }
        });
        Spinner statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.store_status_codes, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter1);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the spinner
                selected_status = parent.getItemAtPosition(position).toString();
                ApplyFilterStatus(selected_status);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String not_selected_status = "true";
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1home.this, storel1dashboard.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1home.this, storel1_sparemov.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(storel1home.this, storel1profile.class);
                i.putExtra("token",token);
                startActivity(i);
            }
        });
        faultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // Get the selected fault string from the filtered list
                    String selectedFault = faultList.get(position);

                    // Iterate through the original unfiltered array to find the corresponding JSON object
                    for (int i = 0; i < successArray.length(); i++) {
                        JSONObject successObject = successArray.getJSONObject(i);

                        // Get values from JSON
                        String station = successObject.getString("station");
                        String status = successObject.getString("status");
                        String mr_id = successObject.getString("mr_id");
                        String request_type = successObject.getString("request_type");

                        String fault = station + ": " + status+": "+mr_id+": "+request_type;

                        if (fault.equals(selectedFault)) {
                            // Found the corresponding JSON object, open fault details
                            openFaultDetails(successObject);
                            break; // Break the loop once found
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void openFaultDetails(JSONObject successObject) throws JSONException {
        Intent intent = new Intent(this, storel1_spare_reqdetail.class);
        String ackno = successObject.getString("ackno");
        Log.d("ACKNO",ackno);
        String date = successObject.getString("time");
        String station = successObject.getString("station");
        String mr_id = successObject.getString("mr_id");
        String sle_name = successObject.getString("sle_name");
        String sle_no = successObject.getString("sle_no");
        String unit = successObject.getString("unit");
        String need_spare = successObject.getString("need_spare");
        String por = successObject.getString("por");
        String fsno = successObject.getString("fsno");
        String description = successObject.getString("description");
        String status = successObject.getString("status");
        String type = successObject.getString("request_type");


        intent.putExtra("token",token);
        intent.putExtra("ackno", ackno);
        intent.putExtra("date", date);
        intent.putExtra("station", station);
        intent.putExtra("mr_id", mr_id);
        intent.putExtra("sle_name", sle_name);
        intent.putExtra("sle_no", sle_no);
        intent.putExtra("unit", unit);
        intent.putExtra("need_spare", need_spare);
        intent.putExtra("por", por);
        intent.putExtra("fsno", fsno);
        intent.putExtra("description", description);
        intent.putExtra("status", status);
        intent.putExtra("request_type", type);
        startActivity(intent);
    }
    private void ApplyFilterStatus(String SStatus)
    {
        if (successArray == null) {
            // Handle the case where successArray is not initialized

            return;
        }


        faultList.clear();
        for (int i = 0; i < successArray.length(); i++) {
            try {
                JSONObject successObject = successArray.getJSONObject(i);

                // Get values from JSON
                String station = successObject.getString("station");
                String status = successObject.getString("status");
                String mr_id = successObject.getString("mr_id");
                String request_type = successObject.getString("request_type");

                // Add station and ackno to the list


                if(SStatus.equalsIgnoreCase("All Status"))
                {
                    String fault = station + ": " + status+": "+mr_id+": "+request_type;
                    faultList.add(fault);

                }
                if (status.equalsIgnoreCase(SStatus)) {
                    // Add station and ackno to the list
                    String fault = station + ": " + status+": "+mr_id+": "+request_type;
                    faultList.add(fault);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
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
                Toast.makeText(storel1home.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
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
                    String request_type = successObject.getString("request_type");

                    // Add station and ackno to the list
                    String fault = station + ": " + status+": "+mr_id+": "+request_type;
                    faultList.add(fault);
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private void showTokenExpiredAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(storel1home.this); // Pass the context of l1home activity
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
            Intent intent = new Intent(storel1home.this, storelogin.class);
            startActivity(intent);
            finish();  // Optional: Close the current activity if needed
        }

    }
}
