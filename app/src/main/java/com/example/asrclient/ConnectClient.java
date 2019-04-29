package com.example.asrclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConnectClient extends AppCompatActivity {

    private static String ipserver = "192.168.43.30";
    private static int portnumber = 8000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_client);
        getSupportActionBar().setTitle("Connection Settings");

        final EditText server = (EditText) findViewById(R.id.ipadd);
        final EditText port = (EditText) findViewById(R.id.port);
        final Button okay = (Button) findViewById(R.id.ok);
        final Button apply = (Button) findViewById(R.id.apply);
        final ProgressBar loading = (ProgressBar) findViewById(R.id.loadlogin);
        server.setText(ipserver);
        port.setText(String.valueOf(portnumber));



        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipserver = server.getText().toString();
                portnumber = Integer.parseInt(port.getText().toString());
                Intent i = new Intent(ConnectClient.this, LoginClient.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipserver = server.getText().toString();
                portnumber = Integer.parseInt(port.getText().toString());
                Toast.makeText(getApplicationContext(), "Try to connect "+getIP(), Toast.LENGTH_LONG).show();
                loading.bringToFront();
                loading.setVisibility(View.VISIBLE);
                testconn(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Successfully connect to server", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailed() {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Unable connect to server", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
    }

    public static String getIP(){
        return ("http://" + ipserver + ":" + portnumber);
    }

    public void testconn(final VolleyCallBack callBack){
        Map<String,String> param = new HashMap<>();
        param.put("testing" , System.currentTimeMillis() + " ");
        JSONObject parameter = new JSONObject(param);
        JsonObjectRequest testRequest = new JsonObjectRequest(Request.Method.POST,getIP()+"/test_conn", parameter,
                new Response.Listener<JSONObject>(){
            public void onResponse(JSONObject response){
                try{
                    JSONObject result = response.getJSONObject("success");
                    //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
                    callBack.onSuccess();
                }
                catch(JSONException ee){
                    ee.printStackTrace();
                    callBack.onFailed();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailed();
                //Toast.makeText(getApplicationContext(), "Unable connect to server " + String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(ConnectClient.this);
        queue.add(testRequest);
    }
}
