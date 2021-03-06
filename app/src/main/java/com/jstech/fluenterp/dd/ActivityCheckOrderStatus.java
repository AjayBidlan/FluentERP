package com.jstech.fluenterp.dd;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jstech.fluenterp.R;
import com.jstech.fluenterp.adapters.AdapterOrderStatus;
import com.jstech.fluenterp.models.SalesOrder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityCheckOrderStatus extends AppCompatActivity {

    ProgressBar progressBar;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<SalesOrder> data;

    void initViews(){
        requestQueue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBarCOS);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_order_status);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_colour));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCOS);
        setSupportActionBar(toolbar);
        setTitle("Check Order Status");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        initRecyclerView();
        retrieveRecords();
    }


    void initRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSalesOrderStatus);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<SalesOrder>();
    }

    void retrieveRecords(){
        progressBar.setVisibility(View.VISIBLE);
        stringRequest = new StringRequest(Request.Method.POST, "https://jaspreettechnologies.000webhostapp.com/retrieveOrderStatus.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");
                            long sdn = 0;
                            double p = 0;
                            String os = "";
                            if(success == 1){
                                JSONArray jsonArray = jsonObject.getJSONArray("sales_orders_list");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jObj = jsonArray.getJSONObject(i);
                                    sdn = jObj.getLong("sales_doc_no");
                                    p = jObj.getDouble("bill_price");
                                    os = jObj.getString("order_status");
                                    SalesOrder so = new SalesOrder(sdn, 0, "0", p, os);
                                    data.add(so);
                                }
                                progressBar.setVisibility(View.GONE);
                                adapter = new AdapterOrderStatus(data);
                                recyclerView.setAdapter(adapter);
                            }else{
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Some Exception: "+e,Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley Error: "+error,Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
        )
        ;
        requestQueue.add(stringRequest);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
