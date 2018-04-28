package com.example.khalaf.bookstore.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.util.ActivityLauncter;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // shift f6 to chang the name of activity
    // firebase de zay webservice mmkn 23mel beha 7agat kteer neeek
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // delw2ty 3ayzeen nrbooot el app bel firebase beta3tnaaa hanshoof 2zaaay
        //tools firebase follow steps
        //firebaseauth class kol 7aga leha 3laka bel auth 3shan feh el sign in we logout we forgrtpassword
        //authststelistener da lw 3ayez 2smaa3 eno 3amel login maslan

        // authuntication de services mn el servises el kteeer ll firebase
        mAuth = FirebaseAuth.getInstance();
        // betrag3 firebase user t3ref enk keda login if null now no noe is login


        // de 3shan 225er el splash activity shwaya
        // wait 3000 msec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // if msfish 7d login
                if (mAuth.getCurrentUser() == null) {
                    // open login activity
                    ActivityLauncter.openLoginActivity(SplashActivity.this);

                } else {
                    // open el bookstore activity
                    ActivityLauncter.openMyBooksActivity(SplashActivity.this);
                }
                finish();
            }
        }, 3000);
    }

}
