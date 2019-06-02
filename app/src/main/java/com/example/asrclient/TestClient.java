package com.example.asrclient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;


public class TestClient extends AppCompatActivity {
    private Recorder recorder;
    private static final int RequestPermissionCode = 1;
    private int number=0;
    private String File_path;
    private List<String> list_sentence = new ArrayList<>();
    private List<Integer> list_id = new ArrayList<>();
    private List<Float> list_score = new ArrayList<>();
    private String message;
    private String winnowing;
    private long startTime;
    private long endTime;
    private float timeElapsed;
    private String currentArrayScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_client);
        getSupportActionBar().setTitle("Pronunciation Test");

        final Button recordButton = findViewById(R.id.btnRecord);
        final Button nextButton = findViewById(R.id.btnNext);
        final Button prevButton = findViewById(R.id.btnPrevious);
        final Button resendButton = findViewById(R.id.btnSend);
        final Button collectButton = findViewById(R.id.btnCollect);
        final TextView TV = findViewById(R.id.tvSoal);
        final TextView Result = findViewById(R.id.lineResult);
        //final TextView Score = findViewById(R.id.lineScore);
        final TextView AuSize = findViewById(R.id.lineSize);
        final TextView Time = findViewById(R.id.lineTime);
        final TextView TvScore = findViewById(R.id.tvSkor);
        //final TextView TotalChar = findViewById(R.id.lineLetter);
        final ProgressBar loadingbar = findViewById(R.id.asrprogress);
        final TextView Winnowing = findViewById(R.id.lineScoreWin);

        if(number>0) {
            prevButton.setVisibility(View.VISIBLE);
        }
        else{
            prevButton.setVisibility(View.INVISIBLE);
        }

        if(list_sentence.size()==(number+1)){
            nextButton.setText("FINISH");
        }
        else {
            nextButton.setText("NEXT");
        }

        TV.setTextSize(30);

        loadsentence(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                TV.setText(list_sentence.get(number));
            }
            @Override
            public void onFailed(){
                Toast.makeText(getApplicationContext(),"Unable load sentences",Toast.LENGTH_LONG).show();
            }
        });

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    deletefile();
                    setupRecorder();
                    recorder.startRecording();
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    try {
                        recorder.stopRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                    loadingbar.bringToFront();
                    loadingbar.setVisibility(View.VISIBLE);
                    File_path = Environment.getExternalStorageDirectory().getPath()+"/test.wav";
                    
                    final File audio_file =new File(File_path);
                    //Toast.makeText(getApplicationContext(), String.valueOf(audio_file.length()),Toast.LENGTH_LONG).show();
                    if(audio_file.length() >= 22000){
                    waveUpload(File_path, new VolleyCallBackFile() {
                            @Override
                            public void onSuccess(String response, String response2) {
                                timeElapsed = (endTime - startTime)/1000000;
                                double bytes = (audio_file.length());
                                Result.setText(response);
                                loadingbar.setVisibility(View.INVISIBLE);
                                AuSize.setText(String.valueOf(bytes));
                                Time.setText(String.valueOf(timeElapsed));
                                ScoreResult scoreres = scoring(list_sentence.get(number),response);
                                currentArrayScore = String.valueOf(scoreres.getScore());
                                //Score.setText(String.valueOf(scoreres.getScore()));
                                Winnowing.setText(response2);
                                //TotalChar.setText(scoreres.getTokendest() + " / " + scoreres.getTokensource() );
                                if(list_score.isEmpty()){
                                    for(int j=0;j<list_sentence.size();j++){
                                    list_score.add(j,0f);
                                    }
                                }
                                list_score.set(number,Float.parseFloat(response2));
                            }
                            public void onFailed(){
                                loadingbar.setVisibility(View.INVISIBLE);
                            }
                        });


                }
                else{
                    Toast.makeText(getApplicationContext(), "Record Failed",Toast.LENGTH_LONG).show();
                    loadingbar.setVisibility(View.INVISIBLE);
                    }
                }
                return true;

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float total_score=0;
                for(int i=0;i<list_score.size();i++){
                    total_score=total_score+list_score.get(i);
                }
                TvScore.setText(String.valueOf(total_score));
                number++;
                prevButton.setVisibility(View.VISIBLE);
                if(list_sentence.size()==(number+1)){
                    nextButton.setText("FINISH");
                }

                try {
                    TV.setText(list_sentence.get(number));
                    Winnowing.setText("");
                    Result.setText("");
                    AuSize.setText("");
                    Time.setText("");
                    //TotalChar.setText("");
                    //Score.setText("");
                }
                catch (IndexOutOfBoundsException e){
                    number--;
                    Map<String,String> paramss = new HashMap<>();
                    paramss.put("id_user",String.valueOf(SharedPreferencesClient.getId(getApplicationContext())));
                    paramss.put("score",String.valueOf(total_score));
                    JSONObject obj = new JSONObject(paramss);
                    Response.Listener<JSONObject> responselistener = new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String result = response.getString("sub_time");
                                Toast.makeText(getApplicationContext(), "Score submitted at " + result, Toast.LENGTH_LONG).show();
                            }
                            catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Failed to submit score", Toast.LENGTH_LONG).show();
                            }
                            }
                    };

                    Response.ErrorListener errListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Failed to submit score", Toast.LENGTH_LONG).show();
                        }
                    };
                    ScoreCollectRequest scoreCollectRequest = new ScoreCollectRequest(obj,responselistener,errListener);
                    RequestQueue queue = Volley.newRequestQueue(TestClient.this);
                    queue.add(scoreCollectRequest);
                    queue.getCache().invalidate(ConnectClient.getIP()+"/set_score",true);

                    Intent i = new Intent(TestClient.this, ScoreClient.class);
                    //i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.putExtra("total_score",total_score);
                    i.putExtra("max_score",(number+1)*10);
                    TestClient.this.startActivity(i);
                    TestClient.this.finish();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float total_score=0;
                for(int i=0;i<list_score.size();i++){
                    total_score=total_score+list_score.get(i);
                }
                TvScore.setText(String.valueOf(total_score));
                number--;
                if(number<=0){
                    prevButton.setVisibility(View.INVISIBLE);
                }
                if(nextButton.getText()=="FINISH"){
                    nextButton.setText("NEXT");
                }
                try {
                    TV.setText(list_sentence.get(number));
                    Winnowing.setText("");
                    Result.setText("");
                    AuSize.setText("");
                    Time.setText("");
                    //TotalChar.setText("");
                    //Score.setText("");
                }
                catch (IndexOutOfBoundsException e){
                    number++;
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingbar.bringToFront();
                loadingbar.setVisibility(View.VISIBLE);
                File_path = Environment.getExternalStorageDirectory().getPath()+"/test.wav";
                final File audio_file =new File(File_path);
                waveUpload(File_path, new VolleyCallBackFile() {
                    @Override
                    public void onSuccess(String response, String response2) {
                        timeElapsed = (endTime - startTime)/1000000;
                        double bytes = (audio_file.length());
                        Result.setText(response);
                        loadingbar.setVisibility(View.INVISIBLE);
                        AuSize.setText(String.valueOf(bytes));
                        Time.setText(String.valueOf(timeElapsed));
                        ScoreResult scoreres = scoring(list_sentence.get(number),response);
                        currentArrayScore = String.valueOf(scoreres.getScore());
                        //Score.setText(String.valueOf(scoreres.getScore()));
                        Winnowing.setText(response2);
                        //TotalChar.setText(scoreres.getTokendest() + " / " + scoreres.getTokensource() );
                        if(list_score.isEmpty()){
                            for(int j=0;j<list_sentence.size();j++){
                                list_score.add(j,0f);
                            }
                        }
                        list_score.set(number,Float.parseFloat(response2));
                    }
                    public void onFailed(){
                        loadingbar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingbar.bringToFront();
                loadingbar.setVisibility(View.VISIBLE);
                Map<String,String> params = new HashMap<>();
                //params.put("sen_test",TV.getText().toString());
                params.put("sen_res",Result.getText().toString());
                ScoreResult scoreres = scoring(TV.getText().toString(),Result.getText().toString());
                String[] surface_t = new String[scoreres.getToken_t().size()];
                String[] pronouce_t = new String[scoreres.getToken_t().size()];
                for(int i = 0; i < surface_t.length; i++) {
                    surface_t[i] = scoreres.getToken_t().get(i).getSurface();
                    pronouce_t[i] = scoreres.getToken_t().get(i).getPronunciation();
                }
                String[] surface_r = new String[scoreres.getToken_r().size()];
                String[] pronouce_r = new String[scoreres.getToken_r().size()];
                for(int i = 0; i < surface_r.length; i++) {
                    surface_r[i] = scoreres.getToken_r().get(i).getSurface();
                    pronouce_r[i] = scoreres.getToken_r().get(i).getPronunciation();
                }
                //params.put("sum_token_test",String.valueOf(scoreres.getTokensource()));
                params.put("id_sentence",String.valueOf(list_id.get(number)));
                params.put("id_user",String.valueOf(SharedPreferencesClient.getId(getApplicationContext())));
                params.put("sum_token_res",String.valueOf(scoreres.getTokendest()));
                params.put("au_size",AuSize.getText().toString());
                params.put("ex_time",Time.getText().toString());
                //params.put("token_test_sur",Arrays.toString(surface_t));
                //params.put("token_test_pro", Arrays.toString(pronouce_t));
                params.put("token_res_sur",Arrays.toString(surface_r));
                params.put("token_res_pro", Arrays.toString(pronouce_r));
                params.put("score",currentArrayScore);
                JSONObject collect_object = new JSONObject(params);
                //Toast.makeText(getApplicationContext(),collect_object.toString(),Toast.LENGTH_LONG).show();
                collectData(collect_object, new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Successfully collect data",Toast.LENGTH_LONG).show();
                        loadingbar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(getApplicationContext(),"Failed to collect data",Toast.LENGTH_LONG).show();
                        loadingbar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });


    }


    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {}}), file());
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 16000
                )
        );
    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "test.wav");
    }

    private void deletefile(){
        File_path = Environment.getExternalStorageDirectory().getPath()+"/test.wav";
        File fdelete = new File(File_path);
        if (fdelete.exists()) {
            fdelete.delete();
        }
    }

    public ScoreResult scoring(String question, String test){
        test = test.replaceAll("\\s+","");
        test = test.replaceAll("。","");
        test = test.replaceAll("、","");
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(question);
        List<Token> tokens_res = tokenizer.tokenize(test);
        int j=0;

        int k=0;
        float score=0;
        float acc_score=0;
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
        acc_score = score/tokens.size();
        ScoreResult rest_sc = new ScoreResult(acc_score,tokens,tokens_res,tokens.size(),tokens_res.size());
        return rest_sc;
    }

    public void loadsentence(final VolleyCallBack callBack){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            public void onResponse(String response){
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray array_lists = obj.getJSONArray("list_sentence");
                    for(int i = 0 ; i < array_lists.length() ; i++){
                        list_sentence.add(array_lists.getJSONObject(i).getString("sentence"));
                        list_id.add(array_lists.getJSONObject(i).getInt("id"));
                    }
                    callBack.onSuccess();

                }
                catch(JSONException ee){
                    ee.printStackTrace();
                    callBack.onFailed();
                }

            }

        };
        TestRequest testRequest = new TestRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(TestClient.this);
        queue.add(testRequest);
        queue.getCache().invalidate(ConnectClient.getIP()+"/get_sentences",true);
    }

    private void waveUpload(final String wavePath, final VolleyCallBackFile callBack) {
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST,ConnectClient.getIP() + "/asr_julius"  ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        endTime = System.nanoTime();
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            message = jObj.getString("asr_result");
                            winnowing = jObj.getString("winnowing_score");
                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        callBack.onSuccess(message,winnowing);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                callBack.onFailed();
            }
        });
        smr.addFile("myFile", wavePath);
        //smr.addMultipartParam("body", "text/plain", String.valueOf(number+1));
        smr.addStringParam("paramstring",String.valueOf(number+1));
        RequestQueue multireq = Volley.newRequestQueue(getApplicationContext());
        multireq.add(smr);
        startTime = System.nanoTime();
        multireq.start();
    }

    private void collectData(JSONObject parameters,final VolleyCallBack callBack){
        JsonObjectRequest Jreq = new JsonObjectRequest(Request.Method.POST, ConnectClient.getIP() + "/collect_data",parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("success");
                            //Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                            callBack.onSuccess();
                        } catch (JSONException ee) {
                            ee.printStackTrace();
                            callBack.onFailed();
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailed();
            }
        });

        RequestQueue queues = Volley.newRequestQueue(getApplicationContext());
        queues.add(Jreq);
        queues.getCache().invalidate(ConnectClient.getIP()+"/collect_data",true);
    }



}
