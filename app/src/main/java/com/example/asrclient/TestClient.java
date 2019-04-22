package com.example.asrclient;

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
import com.android.volley.request.SimpleMultiPartRequest;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;
import omrecorder.WriteAction;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TestClient extends AppCompatActivity {
    private Recorder recorder;
    private static final int RequestPermissionCode = 1;
    private int currentUserId;
    private static int number=0;
    private String File_path;
    private List<String> list_sentence = new ArrayList<String>();
    private String message;
    private long startTime;
    private long endTime;
    private float timeElapsed;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_client);
        getSupportActionBar().setTitle("Pronunciation Test");

        Intent i = getIntent();
        Bundle b = i.getExtras();
        if (b != null) {
            currentUserId = b.getInt("id_user");
        }

        final Button recordButton = (Button) findViewById(R.id.btnRecord);
        final Button nextButton = (Button) findViewById(R.id.btnNext);
        final Button resendButton = (Button) findViewById(R.id.btnSend);
        final TextView TV = (TextView) findViewById(R.id.tvSoal);
        final TextView Result = (TextView) findViewById(R.id.lineResult);
        final TextView Score = (TextView) findViewById(R.id.lineScore);
        final TextView AuSize = (TextView) findViewById(R.id.lineSize);
        final TextView Time = (TextView) findViewById(R.id.lineTime);
        final TextView TotalChar = (TextView) findViewById(R.id.lineLetter);
        final ProgressBar loadingbar = (ProgressBar) findViewById(R.id.asrprogress);

        TV.setTextSize(30);

        loadsentence(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                TV.setText(list_sentence.get(number));
            }});

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
                    Toast.makeText(getApplicationContext(), "Recorded", Toast.LENGTH_LONG).show();
                    loadingbar.bringToFront();
                    loadingbar.setVisibility(View.VISIBLE);
                    File_path = Environment.getExternalStorageDirectory().getPath()+"/test.wav";
                    final File audio_file =new File(File_path);
                    waveUpload(File_path, new VolleyCallBackFile() {
                            @Override
                            public void onSuccess(String response) {
                                timeElapsed = (endTime - startTime)/1000000;
                                double bytes = (audio_file.length());
                                double kilobytes = (bytes/1024);
                                Result.setText(response);
                                loadingbar.setVisibility(View.INVISIBLE);
                                AuSize.setText(bytes + " Bytes");
                                Time.setText(timeElapsed + " ms");
                                ScoreResult scoreres = scoring(list_sentence.get(number),response);
                                Score.setText(scoreres.getScore() + " ");
                                TotalChar.setText(scoreres.getTokendest() + " / " + scoreres.getTokensource() );

                            }
                        });


                }
                return true;

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                number++;
                try {
                    TV.setText(list_sentence.get(number));
                    Result.setText("");
                    AuSize.setText("");
                    Time.setText("");
                    TotalChar.setText("");
                    Score.setText("");
                }
                catch (IndexOutOfBoundsException e){
                    number--;
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
                    public void onSuccess(String response) {
                        timeElapsed = (endTime - startTime)/1000000;
                        double bytes = (audio_file.length());
                        double kilobytes = (bytes/1024);
                        Result.setText(response);
                        loadingbar.setVisibility(View.INVISIBLE);
                        AuSize.setText(bytes + " Bytes");
                        Time.setText(timeElapsed + " ms");
                        ScoreResult scoreres = scoring(list_sentence.get(number),response);
                        Score.setText(scoreres.getScore() + " ");
                        TotalChar.setText(scoreres.getTokendest() + " / " + scoreres.getTokensource() );
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
        ScoreResult rest_sc = new ScoreResult(acc_score,tokens.size(),tokens_res.size());
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
                    }
                    callBack.onSuccess();

                }
                catch(JSONException ee){
                    ee.printStackTrace();

                }

            }

        };
        TestRequest testRequest = new TestRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(TestClient.this);
        queue.add(testRequest);
    }

    private void waveUpload(final String wavePath, final VolleyCallBackFile callBack) {
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST,"http://192.168.1.6:8000/asr_julius"  ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        endTime = System.nanoTime();
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            message = jObj.getString("asr_result");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        callBack.onSuccess(message);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        smr.addFile("myFile", wavePath);
        //UploadWaveFile.getInstance().addToRequestQueue(smr);
        RequestQueue multireq = Volley.newRequestQueue(getApplicationContext());
        multireq.add(smr);
        startTime = System.nanoTime();
        multireq.start();

    }


}
