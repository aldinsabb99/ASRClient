package com.example.asrclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TotalScoreClient extends AppCompatActivity {
private String score[];
private String time[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_score_client);
        getSupportActionBar().setTitle("Score History");

        Map<String, String> params = new HashMap<>();
        params.put("id_user",String.valueOf(SharedPreferencesClient.getId(getApplicationContext())));
        final JSONObject jsonobj = new JSONObject(params);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject jsonResponse) {
                try{
                    if(jsonResponse!=null) {
                        JSONArray list = jsonResponse.getJSONArray("list_score");
                        String score[] = new String[list.length()];
                        String time[] = new String[list.length()];
                        for(int i = 0 ; i < list.length() ; i++){
                            score[i] =  (list.getJSONObject(i).getString("score") + "\n" +list.getJSONObject(i).getString("sub_time"));
                            time[i]  = (list.getJSONObject(i).getString("sub_time"));
                        }
                        ArrayAdapter adapter1 = new ArrayAdapter<String>(TotalScoreClient.this,
                                R.layout.activity_listscore,score);

                        ArrayAdapter adapter2 = new ArrayAdapter<String>(TotalScoreClient.this,
                                R.layout.activity_listscore,time);

                        ListView listScore = findViewById(R.id.score_list);
                        //ListView listTime = findViewById(R.id.time_list);
                        listScore.setAdapter(adapter1);
                        //listTime.setAdapter(adapter2);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Unable to load Score", Toast.LENGTH_LONG).show();
                }
            }
        };
        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Unable to load Score", Toast.LENGTH_LONG).show();
            }
        };
        TotalScoreRequest scoreRequest = new TotalScoreRequest(jsonobj,responseListener,errListener);
        RequestQueue queue = Volley.newRequestQueue(TotalScoreClient.this);
        queue.add(scoreRequest);
        queue.getCache().invalidate(ConnectClient.getIP()+"/get_scores",true);




    }
}

//    final TextView show = (TextView) findViewById(R.id.shows);
//    final TextView show2 = (TextView) findViewById(R.id.shows2);
//    final TextView show3 = (TextView) findViewById(R.id.shows3);
//    Tokenizer tokenizer = new Tokenizer();
//    List<Token> tokens = tokenizer.tokenize("お茶を飲みます");
//    //Question
//    String[] surface = new String[tokens.size()];
//    String[] pronouce = new String[tokens.size()];
//        for(int i = 0; i < tokens.size(); i++) {
//        surface[i] = tokens.get(i).getSurface();
//        pronouce[i] = tokens.get(i).getPronunciation();
//    }
//    //Result
//    List<Token> tokens_res = tokenizer.tokenize("お茶飲みます");
//    String[] surface_res = new String[tokens_res.size()];
//    String[] pronouce_res = new String[tokens_res.size()];
//        for(int i = 0; i < tokens_res.size(); i++) {
//        surface_res[i] = tokens_res.get(i).getSurface();
//        pronouce_res[i] = tokens_res.get(i).getPronunciation();
//    }
//
//        show.setText(Arrays.toString(surface));
//        show2.setText(Arrays.toString(surface_res));
//
//    //accuray algorithm
//    int j=0;
//    int k=0;
//    float score=0;
//        for(int i=0;i<tokens.size();i++){
//        for (j=k;j<tokens_res.size();j++){
//            if(tokens.get(i).getSurface().equals(tokens_res.get(j).getSurface())){
//                score++;
//                k=j+1;
//                break;
//            }
//            else if(tokens.get(i).getPronunciation().equals(tokens_res.get(j).getSurface())){
//                score++;
//                k=j+1;
//                break;
//            }
//        }
//    }
//    float result = score/tokens.size();
//        show3.setText(result + " ");
//
//}