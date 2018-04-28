package com.example.khalaf.bookstore.activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.util.ActivityLauncter;
import com.example.khalaf.bookstore.util.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etemail, etpassword;
    Button btnLogin, btnRegister;
    TextView forgrtpassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initview();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // da mn el a5er lw 3ml sign in el mauth ha fire fa hayegy hena
                if (user != null) {
                    // User is signed in

                    ActivityLauncter.openMyBooksActivity(LoginActivity.this);

                } else {
                    // User is signed out
                }

            }
        };


        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        forgrtpassword.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // find view by id
    private void initview() {
        etemail = (EditText) findViewById(R.id.et_email);
        etpassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        forgrtpassword = (TextView) findViewById(R.id.tv_forget_password);
    }

    // on click buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                //ActivityLauncter.openMyBooksActivity(this);
                break;
            case R.id.btn_register:
                ActivityLauncter.openRegisterationActivity(this);
                break;
            case R.id.tv_forget_password:
                sendforegetpasswordemail();
                break;

        }
    }

    private void sendforegetpasswordemail() {
        String Email = etemail.getText().toString();
        if (Email.isEmpty()) {
            etemail.setError(getString(R.string.Enter_email));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            etemail.setError(getString(R.string.Email_Not_formatted_well));
        } else {
            Utilities.showLoadingDialog(this, Color.WHITE);
            mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {


                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Utilities.dismissLoadingDialog();

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, R.string.Check_your_mail, Toast.LENGTH_LONG).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this, R.string.User_not_found, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.Errore_connection, Toast.LENGTH_LONG).show();
                    }


                }
            });
        }
    }

    private void login() {
        String Email = etemail.getText().toString();
        String Password = etpassword.getText().toString();

        if (Password.length() < 6) {
            etpassword.setError(getString(R.string.Password_must_be_pluse_6));
        } else if (Email.isEmpty()) {
            etemail.setError(getString(R.string.Enter_email));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            etemail.setError(getString(R.string.Email_Not_formatted_well));
        } else {
            Utilities.showLoadingDialog(this, Color.WHITE);
            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                // handle el unsuccesful log in here
                // lw 3ml log in hay fire foooo2
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Utilities.dismissLoadingDialog();
                    if (!task.isSuccessful()) {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException
                                || task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginActivity.this, R.string.Wrong_email_or_password, Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(LoginActivity.this, R.string.Errore_connection, Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
