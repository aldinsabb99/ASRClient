package com.example.asrclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;


public class TotalScoreClient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_score_client);
        final TextView show = (TextView) findViewById(R.id.shows);
        final TextView show2 = (TextView) findViewById(R.id.shows2);
        final TextView show3 = (TextView) findViewById(R.id.shows3);
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize("お茶を飲みます");
        //Question
        String[] surface = new String[tokens.size()];
        String[] pronouce = new String[tokens.size()];
        for(int i = 0; i < tokens.size(); i++) {
            surface[i] = tokens.get(i).getSurface();
            pronouce[i] = tokens.get(i).getPronunciation();
        }
        //Result
        List<Token> tokens_res = tokenizer.tokenize("お茶飲みます");
        String[] surface_res = new String[tokens_res.size()];
        String[] pronouce_res = new String[tokens_res.size()];
        for(int i = 0; i < tokens_res.size(); i++) {
            surface_res[i] = tokens_res.get(i).getSurface();
            pronouce_res[i] = tokens_res.get(i).getPronunciation();
        }

        show.setText(Arrays.toString(surface));
        show2.setText(Arrays.toString(surface_res));

        //accuray algorithm
        int j=0;
        int k=0;
        float score=0;
        for(int i=0;i<tokens.size();i++){
            for (j=k;j<tokens_res.size();j++){
                if(tokens.get(i).getSurface().equals(tokens_res.get(j).getSurface())){
                    score++;
                    k=j+1;
                    break;
                }
                else if(tokens.get(i).getPronunciation().equals(tokens_res.get(j).getSurface())){
                    score++;
                    k=j+1;
                    break;
                }
            }
        }
        float result = score/tokens.size();
        show3.setText(result + " ");

    }
}
