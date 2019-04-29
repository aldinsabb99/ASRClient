package com.example.asrclient;

import com.android.volley.Request;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.JsonRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends JsonObjectRequest {
    private static String Login_URL = ConnectClient.getIP()+"/userval";

    public LoginRequest(JSONObject parameters,
                        Response.Listener<JSONObject> listener, Response.ErrorListener errlistener){
        super(Method.POST,Login_URL,parameters,listener,errlistener);
    }

}
