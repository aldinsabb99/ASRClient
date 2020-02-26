package com.example.asrclient;

import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONObject;

public class TotalScoreRequest extends JsonObjectRequest {
    private static String Score_URL = ConnectClient.getIP()+"/get_scores";

    public TotalScoreRequest(JSONObject parameters,
                               Response.Listener<JSONObject> listener, Response.ErrorListener errlistener){
        super(Method.POST,Score_URL,parameters,listener,errlistener);
    }
}
