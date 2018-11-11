package com.example.navkaran.easyattendance;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CheckAttendanceActivity extends AppCompatActivity {

    private Runnable runnable;
    private ArrayList<String> classList;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String classID;
    private Button refresh;
    private Button sign_attendance;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_ckeck_attendance);

        spinner = findViewById(R.id.spinner);
        refresh = findViewById(R.id.btn);
        sign_attendance = findViewById(R.id.btn_iamhere);
        classList = new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,classList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(select_class);
        refresh.setOnClickListener(f5);
        sign_attendance.setOnClickListener(sign);

        runnable = new Runnable() {
            @Override
            public void run() {
                getClassList();
            }
        };

        Thread thread = new Thread(null,runnable,"background");
        thread.start();
    }
    View.OnClickListener f5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"Searching",Toast.LENGTH_SHORT).show();
            runnable = new Runnable() {
                @Override
                public void run() {
                    getClassList();
                }
            };

            Thread thread = new Thread(null,runnable,"background");
            thread.start();
        }
    };

    View.OnClickListener sign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText
                    (getApplicationContext(), "Selected : " + classID, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    AdapterView.OnItemSelectedListener select_class = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedItemText = (String) parent.getItemAtPosition(position);
            classID = selectedItemText;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void getClassList(){
        final String url = "https://web.cs.dal.ca/~stang/csci5708/get_lecture_list.php";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                        classList.clear();
                        try{
                            for(int i=0; i<response.length(); i++){
                                String class_id = response.getJSONObject(i).getString("class_id");
                                String class_name = response.getJSONObject(i).getString("class_name");
                                classList.add("("+class_id + ") " + class_name);
                            }
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),"Error retrieving data",Toast.LENGTH_SHORT).show();

            }
        }
        );
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
