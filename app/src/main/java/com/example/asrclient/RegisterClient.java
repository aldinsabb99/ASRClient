package com.example.asrclient;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ApplicationController;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;


/**
 * Class Activity untuk login pelanggan
 * @author Gifari Ramadhan
 * @version 2018.05.15
 */
public class RegisterClient extends AppCompatActivity {

    /**
     * Method yang digunakan saat pertama kali membuka activity
     * @param savedInstanceState menyimpan state instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);
        final ImageButton settingconn = (ImageButton) findViewById(R.id.connect);
        final EditText surenameInput = (EditText) findViewById(R.id.signup_inputsurename);
        final EditText usernameInput = (EditText) findViewById(R.id.signup_inputusername);
        final EditText passInput = (EditText) findViewById(R.id.signup_inputPass);
        final Button registerButton = (Button) findViewById(R.id.buttonRegister);
        final ProgressBar loading = (ProgressBar) findViewById(R.id.load);

        if(SharedPreferencesClient.getLoggedStatus(getApplicationContext())) {
            Intent directInt = new Intent(getApplicationContext(), MainActivity.class);
            RegisterClient.this.startActivity(directInt);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterClient.this, RegisterClient.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surename = surenameInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passInput.getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("surename",surename);
                params.put("username",username);
                params.put("password", MD5encypt.encypt(password));
                JSONObject jsonobj = new JSONObject(params);
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject> () {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try{
                            if(jsonResponse!=null) {
                                int id = jsonResponse.getInt("id_user");
                                String name = jsonResponse.getString("name");
                                loading.setVisibility(View.INVISIBLE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClient.this);
                                builder.setMessage("Register Success " + id)
                                        .create()
                                        .show();
                                Intent mainInt = new Intent(RegisterClient.this, MainActivity.class);
                                mainInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
                                SharedPreferencesClient.setId(getApplicationContext(),id);
                                SharedPreferencesClient.setName(getApplicationContext(),name);
                                SharedPreferencesClient.setToken(getApplicationContext(),"TESTOKEN");
                                SharedPreferencesClient.setLoggedIn(getApplicationContext(),true);
                                RegisterClient.this.startActivity(mainInt);
                            }
                        } catch (JSONException e){
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterClient.this);
                            loading.setVisibility(View.INVISIBLE);
                            builder.setMessage("Login Failed.")
                                    .create()
                                    .show();
                        }
                    }
                };
                Response.ErrorListener errListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Unable connect to server", Toast.LENGTH_LONG).show();
                    }
                };
                //Toast.makeText(getApplicationContext(),ConnectClient.getIP() + " ",Toast.LENGTH_LONG).show();
                loading.bringToFront();
                loading.setVisibility(View.VISIBLE);
                LoginRequest loginRequest = new LoginRequest(jsonobj,responseListener,errListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterClient.this);
                queue.add(loginRequest);
                queue.getCache().invalidate(ConnectClient.getIP()+"/userval",true);
            }
        });

        settingconn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterClient.this, ConnectClient.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog quit = builder.create();
        quit.show();
    }

}

