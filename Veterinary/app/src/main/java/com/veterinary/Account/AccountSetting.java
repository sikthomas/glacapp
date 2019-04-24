package com.veterinary.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class AccountSetting extends AppCompatActivity {
    private TextInputEditText firstname, lastname, idnumber, phonenumber;
    private Spinner type,mcounty;
    private Button button;
    private Uri imageUri = null;
    private CircleImageView profile;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private String user_id, downloadUrl;
    private StorageReference storageReference;
    private String randomname= UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firstname = (TextInputEditText) findViewById(R.id.first_name);
        lastname = (TextInputEditText) findViewById(R.id.last_name);
        idnumber = (TextInputEditText) findViewById(R.id.id_number);
        phonenumber = (TextInputEditText) findViewById(R.id.phone_number);
        button = (Button) findViewById(R.id.submit_btn);
        type = (Spinner) findViewById(R.id.usertype);
        mcounty=(Spinner)findViewById(R.id.county);
        firebaseFirestore=FirebaseFirestore.getInstance();
        profile = (CircleImageView) findViewById(R.id.imgProfile_account);
        progressDialog = new ProgressDialog(this);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_account);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        gettingDetails();


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(AccountSetting.this);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstName = firstname.getText().toString().trim();
                final String lastName = lastname.getText().toString().trim();
                final String idNumber = idnumber.getText().toString().trim();
                final String phoneNumber = phonenumber.getText().toString().trim();
                final String spinner = type.getSelectedItem().toString();
                final String county=mcounty.getSelectedItem().toString();

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(AccountSetting.this, "First name is empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(AccountSetting.this, "Last name is empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(AccountSetting.this, "phone number is empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(idNumber)) {
                    Toast.makeText(AccountSetting.this, "ID number is empty", Toast.LENGTH_SHORT).show();
                } else if (type.getSelectedItemPosition() == 0) {
                    Toast.makeText(AccountSetting.this, "Select your menber type", Toast.LENGTH_SHORT).show();
                }else  if(mcounty.getSelectedItemPosition()==0){
                    Toast.makeText(AccountSetting.this, "Select your county", Toast.LENGTH_SHORT).show();
                } else {

                    if(type.getSelectedItemPosition()==1){
                        final AlertDialog.Builder builder=new AlertDialog .Builder(AccountSetting.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to register as a veterinarian?\n When you accept you will have to wait for approval.");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setMessage("Creating account..");
                                progressDialog.show();
                                veterinaryRequest(user_id,county,firstName+" "+lastName);
                                uploadImage();
                                fileUpload(null,county,firstName,lastName,phoneNumber,idNumber,spinner);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();

                   }else{
                        progressDialog.setMessage("Creating account..");
                        progressDialog.show();
                       uploadImage();
                       fileUpload(null,county,firstName,lastName,phoneNumber,idNumber,spinner);
                   }
                }
            }
        });

    }
    private void uploadImage(){
        if(imageUri!=null){
            StorageReference reference=storageReference.child("profileImages").child(randomname+".jpg");
            reference.putFile(imageUri);
        }else {
            Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void fileUpload(Task<UploadTask.TaskSnapshot>task,final String county, final String firstName, final String lastName, final String phoneNumber, final String idNumber, final String spinner){
        StorageReference reference=storageReference.child("profileImages").child(randomname+".jpg");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Map<String,String>stringMap=new HashMap<>();
                stringMap.put("fname",firstName);
                stringMap.put("lname",lastName);
                stringMap.put("phonenumber",phoneNumber);
                stringMap.put("idnumber",idNumber);
                stringMap.put("usetype",spinner);
                stringMap.put("mycounty",county);
                stringMap.put("imageUrl",uri.toString());
                stringMap.put("admin","not_admin");

                firebaseFirestore.collection("Users").document(user_id).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AccountSetting.this, "Account details uploaded..", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            Toast.makeText(AccountSetting.this, "An error occured during submision..", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                profile.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

}   private void veterinaryRequest(String user_ID,String county,String Fullname){
        Map<String,Object>stringMap=new HashMap<>();
        stringMap.put("user_ID",user_ID);
        stringMap.put("timeStamp", FieldValue.serverTimestamp());
        stringMap.put("mycounty",county);
        stringMap.put("fullname",Fullname);

        firebaseFirestore.collection("Veterinary").document("AllVeterinary")
                .collection("Requests").document(user_ID).set(stringMap);
    }

    public void gettingDetails(){
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().exists()){
                        String fname=task.getResult().getString("fname");
                        String lname=task.getResult().getString("lname");
                        String phonnumber=task.getResult().getString("phonenumber");
                        String Idnumber=task.getResult().getString("idnumber");
                        String usertype=task.getResult().getString("usetype");
                        String county=task.getResult().getString("mycounty");
                        String imageurl=task.getResult().getString("imageUrl");

                        RequestOptions requestOptions=new RequestOptions();
                        requestOptions.centerCrop();
                        requestOptions.fitCenter();
                        Glide.with(AccountSetting.this).applyDefaultRequestOptions(requestOptions).load(imageurl).into(profile);

                        firstname.setText(fname);
                        lastname.setText(lname);
                        idnumber.setText(Idnumber);
                        phonenumber.setText(phonnumber);

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AccountSetting.this, R.array.spinneritems, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mcounty.setAdapter(adapter);
                        if (county != null) {
                            int spinnerPosition = adapter.getPosition(county);
                            mcounty.setSelection(spinnerPosition);
                        }
                        ArrayAdapter<CharSequence> kadapter = ArrayAdapter.createFromResource(AccountSetting.this, R.array.membertype, android.R.layout.simple_spinner_item);
                        kadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        type.setAdapter(kadapter);
                        if (usertype != null) {
                            int spinnerPosition = kadapter.getPosition(usertype);
                            type.setSelection(spinnerPosition);
                        }

                    }
                }
            }
        });

    }
}



