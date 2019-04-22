package com.example.asrclient;

import com.android.volley.Response;
import com.android.volley.request.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TestRequest extends StringRequest {
    private static final String Main_URL = "http://192.168.1.6:8000/get_sentences";
    private Map<String, String> params;

    public TestRequest(Response.Listener<String> listener) {
        super(Method.GET, Main_URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
