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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.veterinary.R;

public class Signup extends AppCompatActivity {
    private TextInputEditText semail,spassword,sconfirmpassword;
  private Button ssignup,slogin;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        semail=(TextInputEditText) findViewById(R.id.signup_email);
        spassword=(TextInputEditText) findViewById(R.id.signup_password);
        sconfirmpassword=(TextInputEditText) findViewById(R.id.signup_confirm_password);
        ssignup=(Button)findViewById(R.id.signup_btn);
        slogin=(Button)findViewById(R.id.signup_login);
        progressDialog=new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();

        slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Signup.this,Login.class);
                startActivity(intent);
            }
        });


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_signup);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

       ssignup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String email=semail.getText().toString().trim();
               String password=spassword.getText().toString().trim();
               String cpassword=sconfirmpassword.getText().toString().trim();
               if (TextUtils.isEmpty(email)){
                   Toast.makeText(Signup.this, "enter email", Toast.LENGTH_SHORT).show();
               }else if(TextUtils.isEmpty(password)){
                   Toast.makeText(Signup.this, "password is empty", Toast.LENGTH_SHORT).show();
               }else if(TextUtils.isEmpty(cpassword)){
                   Toast.makeText(Signup.this, "Confirm your password", Toast.LENGTH_SHORT).show();
               }else if(!password.equals(cpassword)){
                   Toast.makeText(Signup.this, "Password don't match", Toast.LENGTH_SHORT).show();
               }else if(password.length()<8){
                   Toast.makeText(Signup.this, "Your password must be at least 8 characters", Toast.LENGTH_SHORT).show();
               }else {
                   progressDialog.setMessage("Submitting your registration details");
                   progressDialog.show();
                   signup(email,password);
               }
           }
       });
    }
    private void signup(final String email,final String password){
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.setMessage("Account successifully created");
                            Toast.makeText(Signup.this, "Account successifully created", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Signup.this,MainPanel.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(Signup.this, "Something went wrong\n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Signup.this, "Error\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
