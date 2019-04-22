package com.example.asrclient;

import com.android.volley.request.StringRequest;
import com.android.volley.Response;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    private static final String Login_URL = "http://192.168.43.30:8080/userval";
    private Map<String, String> params;

    public LoginRequest(String username, String password,
                        Response.Listener<String> listener) {
        super(Method.POST, Login_URL, listener, null);
        params = new HashMap<>();
        params.put("username",username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
