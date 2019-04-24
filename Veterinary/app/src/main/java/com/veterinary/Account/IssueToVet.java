package com.veterinary.Account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.veterinary.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class IssueToVet extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private String fullname, imageUrl,county,postId,idnumber,phoneNumber,my_user_id;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    private CircleImageView mProfile;
    private TextView mDeatails;
    private FloatingActionButton mCalling;
    private Button mSendingIssue;
    private Uri imageUri = null;
    private ImageView imageIssue;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_to_vet);

        auth = FirebaseAuth.getInstance();
        my_user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(IssueToVet.this);

        //initialize out items
        mProfile = (CircleImageView)findViewById(R.id.vet_image_profile_desc);
        mDeatails = (TextView)findViewById(R.id.tv_Details_vet_desc);
        mCalling = (FloatingActionButton)findViewById(R.id.btn_call_vet);
        mSendingIssue = (Button)findViewById(R.id.btn_send_issue);

        //getting the passed values

        fullname = getIntent().getExtras().getString("fname")+" "+getIntent().getExtras().getString("lname");
        imageUrl = getIntent().getExtras().getString("imageUrl");
        county = getIntent().getExtras().getString("mycounty");
        postId = getIntent().getExtras().getString("postId");
        idnumber = getIntent().getExtras().getString("idnumber");
        phoneNumber = getIntent().getExtras().getString("phonenumber");

        setTitle(fullname);

        //setting the image profile
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(IssueToVet.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfile);


        //seting other vet details
        mDeatails.setText("Full Name: "+fullname+"\nID Number: "+idnumber+"\nPhone Number: "+phoneNumber+"\n" +
                "County: "+county);

        //making a call
        mCalling.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent sIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
                sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sIntent);
            }
        });

        //sending an issue
        mSendingIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(IssueToVet.this);
                dialog.setTitle("New Issue");
                dialog.setContentView(R.layout.dialog_issue_to_send);

                //initializing dialog items
                imageIssue = (ImageView)dialog.findViewById(R.id.image_issue_to_pick);
                final TextInputEditText mIssue = (TextInputEditText)dialog.findViewById(R.id.edt_issue_main);
                Button mSend = (Button)dialog.findViewById(R.id.btn_send_issue_dialog);

                //picking the isseu image
                imageIssue.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(IssueToVet.this);
                    }
                });

                mSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String issue = mIssue.getText().toString().trim();
                        if (TextUtils.isEmpty(issue)){
                            mIssue.setError("Enter issue...!");
                        }else {
                            if (imageUri !=null){
                                progressDialog.setMessage("Sending your issue...");
                                progressDialog.show();
                                //sendIsueWith image
                                issueWithImage(issue,postId,my_user_id,dialog);
                                dialog.dismiss();
                            }else {
                                progressDialog.setMessage("Sending your issue...");
                                progressDialog.show();
                                //sendIsueWtithout
                                issueWithoutImage(issue,postId,my_user_id,dialog);
                                dialog.dismiss();
                            }
                        }
                    }
                });

                dialog.show();

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                imageIssue.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void issueWithImage(final String Issue, final String vet_id, final String my_user_Id, final Dialog dialog){
        String randomName = UUID.randomUUID().toString();
        final StorageReference reference = storageReference.child("IssuesImages").child(randomName+".jpg");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String our_image_url = uri.toString();
                            //sending the issue with image to database
                            final Map<String, Object> objectMap = new HashMap<>();
                            objectMap.put("issue",Issue);
                            objectMap.put("myuser_id",my_user_Id);
                            objectMap.put("timeStamp", FieldValue.serverTimestamp());
                            objectMap.put("reply","No reply yet");
                            objectMap.put("imageUrl",our_image_url);
                            objectMap.put("vet_id",vet_id);
                            /*
                            * SENDING THE ABOVE DATA TO DB
                            *
                            * */
                            firebaseFirestore.collection("AllIssues").document(county).collection(county+"Issues").add(objectMap).addOnCompleteListener(
                                    new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){

                                                Toast.makeText(IssueToVet.this, "Issue successfully submitted", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(IssueToVet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    private void issueWithoutImage(final String Issue, final String vet_id, final String my_user_Id, final Dialog dialog){
        final Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("issue",Issue);
        objectMap.put("myuser_id",my_user_Id);
        objectMap.put("timeStamp", FieldValue.serverTimestamp());
        objectMap.put("reply","No reply yet");
        objectMap.put("vet_id",vet_id);
                            /*
                            * SENDING THE ABOVE DATA TO DB
                            *
                            * */
        firebaseFirestore.collection("AllIssues").document(county).collection(county+"Issues").add(objectMap).addOnCompleteListener(
                new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){

                            dialog.dismiss();
                            Toast.makeText(IssueToVet.this, "Issue successfully submitted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(IssueToVet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
