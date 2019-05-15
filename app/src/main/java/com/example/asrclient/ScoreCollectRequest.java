package com.example.asrclient;

import com.android.volley.Response;
import com.android.volley.request.JsonObjectRequest;

import org.json.JSONObject;

public class ScoreCollectRequest extends JsonObjectRequest {
    private static String Score_URL = ConnectClient.getIP()+"/set_score";

    public ScoreCollectRequest(JSONObject parameters,
                        Response.Listener<JSONObject> listener, Response.ErrorListener errlistener){
        super(Method.POST,Score_URL,parameters,listener,errlistener);
    }

}
