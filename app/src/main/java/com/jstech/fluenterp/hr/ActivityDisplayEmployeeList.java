package com.jstech.fluenterp.hr;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jstech.fluenterp.MainActivity;
import com.jstech.fluenterp.R;
import com.jstech.fluenterp.models.Employee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDisplayEmployeeList extends AppCompatActivity {

    ListView listViewEmployees;
    ArrayList<Employee> arrEmployees;
    ArrayAdapter<String> empNameAdapter;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ProgressBar progressBar;

    void initViews(){
        progressBar = findViewById(R.id.progressBarDEL);
        progressBar.setVisibility(View.GONE);
        listViewEmployees = findViewById(R.id.listViewEmployees);
        empNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        arrEmployees = new ArrayList<>();
        listViewEmployees.setAdapter(empNameAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_employee_list);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_colour));
        }
        requestQueue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDEL);
        setSupportActionBar(toolbar);
        setTitle("Display Employees List");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        showEmployeeList();
    }
    void showEmployeeList(){
        progressBar.setVisibility(View.VISIBLE);
        stringRequest = new StringRequest(Request.Method.GET, "https://jaspreettechnologies.000webhostapp.com/retrieveEmployees.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            int empId = 0;
                            String empName ="";
                            String empAddress = "";
                            String empType = "";
                            long empPhone = 0;
                            String dob = "";
                            String doj = "";
                            if(success == 1){
                                JSONArray jsonArray = jsonObject.getJSONArray("employees");
                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject jObj = jsonArray.getJSONObject(i);

                                    empId = jObj.getInt("emp_id");
                                    empName = jObj.getString("emp_name");
                                    empAddress = jObj.getString("emp_address");
                                    empType = jObj.getString("emp_type");
                                    empPhone = jObj.getLong("emp_phone");
                                    dob = jObj.getString("dob");
                                    doj = jObj.getString("doj");

                                    Employee empTemp = new Employee(empId, empName, empAddress, empType, empPhone, dob, doj);
                                    arrEmployees.add(empTemp);
                                    empNameAdapter.add(empTemp.getEmpId()+": "+empTemp.getEmpName());
                                    progressBar.setVisibility(View.GONE);
                                }
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"None Found",Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Some Exception: "+e,Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley Error: "+error,Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
