package com.example.asrclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class MainActivity extends AppCompatActivity {

    LinearLayout mlineTest, mlineScore;
    TextView mlineLogout,mName;
    private static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermission())
        {
            requestPermission();
        }


        mlineTest = findViewById(R.id.lineTest);
        mlineScore = findViewById(R.id.lineScore);
        mlineLogout = findViewById(R.id.logout);
        mName = findViewById(R.id.name);


        mName.setText(SharedPreferencesClient.getName(getApplicationContext()));

        mlineTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TestClient.class);
                startActivity(i);
            }
        });

        mlineScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TotalScoreClient.class);
                startActivity(i);
            }
        });

        mlineLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure want to Log Out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout();
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO,READ_EXTERNAL_STORAGE,ACCESS_NETWORK_STATE}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean ReadPermission = grantResults[2] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean AccessNetwork = grantResults[3] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission && ReadPermission && AccessNetwork) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_NETWORK_STATE);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void logout(){
        Intent i = new Intent(MainActivity.this, LoginClient.class);
        SharedPreferencesClient.setId(getApplicationContext(),0);
        SharedPreferencesClient.setName(getApplicationContext(),null);
        SharedPreferencesClient.setToken(getApplicationContext(),null);
        SharedPreferencesClient.setLoggedIn(getApplicationContext(),false);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK|FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
}