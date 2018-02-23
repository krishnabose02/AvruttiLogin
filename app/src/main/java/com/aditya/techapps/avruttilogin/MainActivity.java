package com.aditya.techapps.avruttilogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity {



boolean login = true;
EditText email, password, confirmpass;
CardView confirmButton;
TextView buttonText;
ConstraintLayout root;
CardView passcard;
private String mailID, pass;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        if(bar != null)
            bar.hide();
        // Set up the login form.

        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        String name = pref.getString("user", "qqq");
        if(!name.equals("qqq"))
        {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        root = findViewById(R.id.loginroot);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpass = findViewById(R.id.confirmpassword);
        passcard = findViewById(R.id.confirm_password_card);

        confirmButton = findViewById(R.id.signincard);
        buttonText = findViewById(R.id.signinbutton);
    }

    @Override
    public void onBackPressed()
    {
        //do something
        //startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void showRegister(View view) {
        passcard.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        (findViewById(R.id.loginText)).setVisibility(View.VISIBLE);
        buttonText.setText("Register me");
        login = false;
    }

    public void showLogin(View view) {
        passcard.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        (findViewById(R.id.registerText)).setVisibility(View.VISIBLE);
        buttonText.setText("Sign in");
        login = true;
    }



    public void proceed(View view) {

        //these are checking common to both login and sign up
        mailID = email.getText().toString().trim();
        if(!mailID.contains("@") && !mailID.contains(".")) {
            email.setError("Invalid Email");
            return;
        }

        pass = password.getText().toString();
        if(pass.isEmpty())
        {
            password.setError("This field is required");
            return;
        }

        if(login)
        {
            //proceed to log in existing user

            //verify and login credentials

            DatabaseHandler.login(this, mailID, pass, new LoginStatus() {
                @Override
                public void onReceivingStatus(User user) {
                    if(user == null)
                    {
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    else logInUser(user);
                }
            });
        }
        else
        {
            //register new user
            String repass = confirmpass.getText().toString();
            if(!pass.equals(repass))
            {
                confirmpass.setError("Password doesn't match");
                return;
            }

            //so password matches

            //if user have already registered and trying to re-register
            DatabaseHandler.isRegistered(this, mailID, new LoginStatus() {
                @Override
                public void onReceivingStatus(User user) {
                    if(user!=null)
                        snackIt("User already exists, try logging in");
                    else {
                        //so user is new to our system
                        //try to make a new entry
                        try {
                            DatabaseHandler.createUser2(MainActivity.this, mailID, pass,
                                    new LoginStatus() {
                                        @Override
                                        public void onReceivingStatus(User user) {
                                            if(user == null)
                                            {
                                                Toast.makeText(MainActivity.this, "Probable network failure", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Log.e("user received",user.mail);
                                            logInUser(user);
                                        }
                                    });
                        } catch (Exception e) {
                            snackIt(e.getMessage());
                            snackIt("Internal Error, please retry");
                        }
                    }
                }
            });
        }
    }

    private void logInUser(User user) {
        if(user == null)
        {
            Toast.makeText(this, "Error signing in!", Toast.LENGTH_SHORT).show();
            return;
        }
        //code to log in user and store his/her credentials
        SharedPreferences pref = getSharedPreferences("MyData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user",user.mail);
        editor.commit();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void snackIt(String s) {
        //Design dependency was not added so replacing snackbar with Toast
        //Snackbar.make(root, s, Snackbar.LENGTH_SHORT).show();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}