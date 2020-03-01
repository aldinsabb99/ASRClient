package com.example.asrclient;

import com.android.volley.request.JsonObjectRequest;
import com.android.volley.Response;

import org.json.JSONObject;


public class RegisterRequest extends JsonObjectRequest {
    private static String Regis_URL = ConnectClient.getIP()+"/useradd";

    public RegisterRequest(JSONObject parameters,
                        Response.Listener<JSONObject> listener, Response.ErrorListener errlistener){
        super(Method.POST,Regis_URL,parameters,listener,errlistener);
    }
}
