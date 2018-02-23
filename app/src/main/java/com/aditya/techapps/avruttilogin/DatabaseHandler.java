package com.aditya.techapps.avruttilogin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aditya on 15-02-2018.
 */

public class DatabaseHandler {

private static String passwordcache;
private static boolean found;
private static boolean executed;


    public static void login(final MainActivity mainActivity, final String mailID, final String pass, final LoginStatus status) {
        //do stuff here
        passwordcache = "";
        isRegistered(mainActivity, mailID, new LoginStatus() {
            @Override
            public void onReceivingStatus(User user) {
                if(user != null)
                {
                    Log.e("User not null","password is "+passwordcache);
                    if(passwordcache.equals(pass))
                    {
                        status.onReceivingStatus(new User(mailID, pass));
                    }
                    else
                        Toast.makeText(mainActivity, "Password mismatch, try again", Toast.LENGTH_SHORT).show();
                }
                else
                status.onReceivingStatus(null);
            }
        });
    }





    public static void isRegistered(final MainActivity mainActivity, final String mailID, final LoginStatus status) {
        //do more stuff here
        found = executed = false;

        String URI = "https://arcane-brushlands-37867.herokuapp.com/todos/"+mailID;

        StringRequest request = new StringRequest(Request.Method.GET,
                URI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //listen to responses
                        Log.e("response",response);
                        if(response.length()>13)
                        {
                            found = true;
                            try {
                                JSONObject object = new JSONObject(response);
                                passwordcache = object.get("password").toString();
                            } catch (JSONException e) {
                                String t = response.substring(response.indexOf("password")+11);
                                passwordcache = t.substring(0,t.indexOf("\""));
                            }
                            status.onReceivingStatus(new User(mailID,passwordcache));
                        }
                        else status.onReceivingStatus(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Life and","errors"+error.getMessage());
                        //and to errors
                        status.onReceivingStatus(null);
                        Toast.makeText(mainActivity, "Probable network failure", Toast.LENGTH_SHORT).show();
                    }
                }

        );
        Volley.newRequestQueue(mainActivity).add(request);

    }



    public static void createUser2(final MainActivity activity, final String mailID, final String pass, final LoginStatus status)
    {
        String URI = "https://arcane-brushlands-37867.herokuapp.com/todos";
        Map<String, String> params = new HashMap<>();
        params.put("email",mailID);
        params.put("password",pass);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URI,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response -->> ", response.toString());
                        String received = response.toString();
                        if(received.contains(mailID) && received.contains(pass))
                            status.onReceivingStatus(new User(mailID,pass));
                        else
                            status.onReceivingStatus(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("change Pass response -->> " ,error.toString());
                        status.onReceivingStatus(null);
                        Toast.makeText(activity, "Probable network failure", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("email",mailID);
                params.put("password",pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);
    }
}
