package com.example.asrclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.ApplicationController;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ConnectClient extends AppCompatActivity {

    private static String ipserver = SharedPreferencesClient.getIP(MyApplication.getAppContext());
    private static int portnumber = SharedPreferencesClient.getPort(MyApplication.getAppContext());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_client);
        getSupportActionBar().setTitle("Connection Settings");

        final EditText server = (EditText) findViewById(R.id.ipadd);
        final EditText port = (EditText) findViewById(R.id.port);
        final Button okay = (Button) findViewById(R.id.ok);
        final Button apply = (Button) findViewById(R.id.apply);
        final ProgressBar loading = (ProgressBar) findViewById(R.id.loadlogin);
        server.setText(ipserver);
        port.setText(String.valueOf(portnumber));



        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipserver = server.getText().toString();
                portnumber = Integer.parseInt(port.getText().toString());
                SharedPreferencesClient.setIP(getApplicationContext(),ipserver);
                SharedPreferencesClient.setPort(getApplicationContext(),portnumber);
                Intent i = new Intent(ConnectClient.this, LoginClient.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipserver = server.getText().toString();
                portnumber = Integer.parseInt(port.getText().toString());
                SharedPreferencesClient.setIP(getApplicationContext(),ipserver);
                SharedPreferencesClient.setPort(getApplicationContext(),portnumber);
                Toast.makeText(getApplicationContext(), "Try to connect "+getIP(), Toast.LENGTH_LONG).show();
                loading.bringToFront();
                loading.setVisibility(View.VISIBLE);
                //HttpsTrustManager.allowAllSSL();
                String url = getIP()+"/test_conn";
//                HurlStack hurlStack = new HurlStack() {
//                    @Override
//                    protected HttpURLConnection createConnection(URL url) throws IOException {
//                        //Toast.makeText(getApplicationContext(),url +"",Toast.LENGTH_LONG).show();
//                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
//                        //Toast.makeText(getApplicationContext(),url +"",Toast.LENGTH_LONG).show();
//                        try {
//                            httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
//                            httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return httpsURLConnection;
//                    }
//                };
//                Toast.makeText(getApplicationContext(),hurlStack+"",Toast.LENGTH_LONG).show();

                testconn(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Successfully connect to server", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailed() {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Unable connect to server", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
    }

    public static String getIP(){
        return ("http://" + ipserver + ":" + portnumber);
    }

    public void testconn(final VolleyCallBack callBack){
        Map<String,String> param = new HashMap<>();
        param.put("testing" , System.currentTimeMillis() + " ");
        JSONObject parameter = new JSONObject(param);
        JsonObjectRequest testRequest = new JsonObjectRequest(Request.Method.POST,getIP()+"/test_conn", parameter,
                new Response.Listener<JSONObject>(){
            public void onResponse(JSONObject response){
                try{
                    JSONObject result = response.getJSONObject("success");
                    //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
                    callBack.onSuccess();
                }
                catch(JSONException ee){
                    ee.printStackTrace();
                    callBack.onFailed();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailed();
                //Toast.makeText(getApplicationContext(), "Unable connect to server " + String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(ConnectClient.this);
        queue.add(testRequest);
        queue.getCache().invalidate(getIP()+"test_conn",true);
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                //return hv.verify(ipserver, session);
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.ca); // this cert file stored in \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }
}
