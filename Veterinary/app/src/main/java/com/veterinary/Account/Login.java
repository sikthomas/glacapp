package com.veterinary.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.veterinary.R;

public class Login extends AppCompatActivity {
    private TextInputEditText memail,mpassword;
    private Button mlogin,maccount;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        memail=(TextInputEditText) findViewById(R.id.login_email);
        mpassword=(TextInputEditText) findViewById(R.id.login_password);
        mlogin=(Button)findViewById(R.id.login_login);
        maccount=(Button)findViewById(R.id.login_create_account);
        progressDialog=new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_login);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        maccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,Signup.class);
                        startActivity(intent);
            }
        });

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=memail.getText().toString().trim();
                String password=mpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Enter your password", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();
                    signin(email,password);
                }
            }
        });
    }

    private void signin(final String email, final String password){
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent=new Intent(Login.this,MainPanel.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(Login.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=auth.getCurrentUser();
        if (firebaseUser!=null){
            Intent intent=new Intent(Login.this,MainPanel.class);
            startActivity(intent);
            finish();
        }
    }
}
