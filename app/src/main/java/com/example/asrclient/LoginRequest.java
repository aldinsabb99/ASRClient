package com.example.asrclient;

import com.android.volley.request.JsonObjectRequest;
import com.android.volley.Response;

import org.json.JSONObject;


public class LoginRequest extends JsonObjectRequest {
    private static String Login_URL = ConnectClient.getIP()+"/userval";

    public LoginRequest(JSONObject parameters,
                        Response.Listener<JSONObject> listener, Response.ErrorListener errlistener){
        super(Method.POST,Login_URL,parameters,listener,errlistener);
    }

}
