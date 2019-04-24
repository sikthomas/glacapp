package com.veterinary.Account;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.veterinary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordReset extends Fragment {
    private View  view;

    private FirebaseAuth auth;
    private String my_user_id;
    private Button mSendLink;
    private EditText mEmail;
    private String email;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_password_reset, container, false);

        auth = FirebaseAuth.getInstance();
        my_user_id = auth.getCurrentUser().getUid();
        mSendLink = (Button)view.findViewById(R.id.btn_rest_password);
        mEmail = (EditText)view.findViewById(R.id.edt_email_reset);
        progressDialog = new ProgressDialog(getContext());

        //sending the link
        mSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Enter your email...");
                }else {

                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure you want to proceed with this email\n"+email+"?")
                            .setTitle("Email Confirmation")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    progressDialog.setMessage("Sending reset link...");
                                    progressDialog.show();
                                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(), "Link successfully sent", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        });



        return view;
    }

}
