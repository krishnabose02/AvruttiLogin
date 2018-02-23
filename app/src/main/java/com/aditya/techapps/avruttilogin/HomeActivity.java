package com.aditya.techapps.avruttilogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        String mail = pref.getString("user","qqq");
        if(!mail.equals("qqq"))
            ((TextView)findViewById(R.id.creds)).setText("Welcome, "+mail+"!");
    }

    public void logout(View view) {
        SharedPreferences pref = getSharedPreferences("MyData",MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("user", "qqq");
        edit.commit();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
