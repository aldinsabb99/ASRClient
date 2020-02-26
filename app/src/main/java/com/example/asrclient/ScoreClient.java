package com.example.asrclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ScoreClient extends AppCompatActivity {
    private float totalScore;
    private int maxScore;
    String numberAsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_client);

        final TextView total_score = findViewById(R.id.totalScore);
        final TextView max_score = findViewById(R.id.maxScore);
        final Button btn_try = findViewById(R.id.trybutton);
        final Button btn_main = findViewById(R.id.endbutton);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            totalScore = b.getFloat("total_score");
            maxScore = b.getInt("max_score");
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            numberAsString = decimalFormat.format(totalScore);
            total_score.setText(numberAsString);
            max_score.setText(String.valueOf(maxScore));
        }



        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScoreClient.this, TestClient.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScoreClient.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }


}
