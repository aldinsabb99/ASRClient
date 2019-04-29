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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Class Activity untuk login pelanggan
 * @author Gifari Ramadhan
 * @version 2018.05.15
 */
public class LoginClient extends AppCompatActivity {

    /**
     * Method yang digunakan saat pertama kali membuka activity
     * @param savedInstanceState menyimpan state instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);
        final ImageButton settingconn = (ImageButton) findViewById(R.id.connect);
        final EditText usernameInput = (EditText) findViewById(R.id.inputusername);
        final EditText passInput = (EditText) findViewById(R.id.inputPass);
        final Button loginButton = (Button) findViewById(R.id.buttonLogin);
        final ProgressBar loading = (ProgressBar) findViewById(R.id.load);
        deleteCache(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passInput.getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("username",username);
                params.put("password", password);
                JSONObject jsonobj = new JSONObject(params);
                Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject> () {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try{
                            if(jsonResponse!=null) {
                                int id = jsonResponse.getInt("id_user");
                                loading.setVisibility(View.INVISIBLE);
                                Intent mainInt = new Intent(LoginClient.this, MainActivity.class);
                                mainInt.putExtra("id_user",id);
                                LoginClient.this.startActivity(mainInt);
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
                                builder.setMessage("Login Success " + id)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
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
                Toast.makeText(getApplicationContext(),ConnectClient.getIP() + " ",Toast.LENGTH_LONG).show();
                loading.bringToFront();
                loading.setVisibility(View.VISIBLE);
                LoginRequest loginRequest = new LoginRequest(jsonobj,responseListener,errListener);
                RequestQueue queue = Volley.newRequestQueue(LoginClient.this);
                queue.add(loginRequest);
            }
        });

        settingconn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginClient.this, ConnectClient.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anda yakin ingin keluar ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog quit = builder.create();
        quit.show();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


}

