package com.veterinary.Account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

public class MainPanel extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private FirebaseAuth auth;
    private ImageView imageIssue;
    private ProgressDialog progressDialog;
    private  Uri imageUri;
    private  StorageReference storageReference;
    private Home home;
    private PasswordReset passwordReset;
    private Toolbar toolbar;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user_id=auth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference();

        toolbar.setTitle("Home->General Posts");

        //initializing the home fragment not even sure
       home = new Home();
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.main_container,home).commitNow();

       //startActivity(new Intent(MainPanel.this,AllGeneralIssues.class));



         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainPanel.this);
                dialog.setTitle("New Issue");
                dialog.setContentView(R.layout.dialog_issue_to_send);

                //initializing dialog items
                imageIssue = (ImageView)dialog.findViewById(R.id.image_issue_to_pick);
                final TextInputEditText mIssue = (TextInputEditText)dialog.findViewById(R.id.edt_issue_main);
                Button mSend = (Button)dialog.findViewById(R.id.btn_send_issue_dialog);

                //picking the isseu image
                imageIssue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(MainPanel.this);
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
                                issueWithImage(issue,user_id,dialog);
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }else {
                                progressDialog.setMessage("Sending your issue...");
                                progressDialog.show();
                                //sendIsueWtithout
                                issueWithoutImage(issue,user_id,dialog);
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        }
                    }
                });

                dialog.show();

            }
        });

       // showingFloatButton(user_id,fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        settingUserDetails(user_id,navigationView);
    }

    private void settingUserDetails(final String User_Id, NavigationView mNavigationView){
        View view = mNavigationView.getHeaderView(0);

        final TextView Username = (TextView)view.findViewById(R.id.tv_username);
        final TextView UserEmail = (TextView)view.findViewById(R.id.tv_registerd_email);
        final CircleImageView UserImage =(CircleImageView)view.findViewById(R.id.user_image_profile);



        //getting details fro Db
        firebaseFirestore.collection("Users").document(User_Id).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                Username.setText(task.getResult().getString("fname")+" "+task.getResult().getString("lname"));
                                UserEmail.setText(auth.getCurrentUser().getEmail());
                                String imageUrl = task.getResult().getString("imageUrl");

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();
                                requestOptions.placeholder(R.color.lightgray);
                                Glide.with(MainPanel.this).load(imageUrl).apply(requestOptions).into(UserImage);
                            }
                        }

                    }
                });
        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainPanel.this,AccountSetting.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_panel, menu);

        final MenuItem vet_requests = menu.findItem(R.id.action_request);

        //seting visibility
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String admin = task.getResult().getString("admin");
                                if (admin.equals("admin")){
                                    vet_requests.setVisible(true);
                                }
                            }
                        }
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_request) {
            Intent intent=new Intent(MainPanel.this,Vetrequest.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_rate_us){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=Veterinary")));

        }if (id == R.id.action_myaccount) {
            startActivity(new Intent(MainPanel.this,AccountSetting.class));
        }if (id == R.id.action_approved_vets) {
            Intent intent = new Intent(MainPanel.this,AllApprovedVets.class);
            intent.putExtra("sendissue","issue");
            startActivity(intent);
        }
        if (id == R.id.action_sendreply_vets) {
            Intent intent = new Intent(MainPanel.this,AllApprovedVets.class);
            intent.putExtra("sendissue","reply");
            startActivity(intent);
        }
        if (id == R.id.acction_share){
            String message = "Hi....!! You can now use Veterinary App to view veterinary services...";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,message);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share Veterinary app via");
            startActivity(sendIntent);

        }
        if (id == R.id.action_help){

        }
        if (id == R.id.action_issues_vets){
            startActivity(new Intent(MainPanel.this,AllMyIssues.class));

        }
        if (id == R.id.action_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exiting...");
            builder.setMessage("Are you sure you want to logout from your account?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    auth.signOut();

                    startActivity(new Intent(MainPanel.this,Login.class));
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_vet) {
            Intent intent = new Intent(MainPanel.this,AllApprovedVets.class);
            intent.putExtra("sendissue","issue");
            startActivity(intent);
        } else if (id == R.id.nav_issues) {
            startActivity(new Intent(MainPanel.this,AllMyIssues.class));

        } else if (id == R.id.nav_share) {
            String message = "Hi....!! You can now use Veterinary App to view veterinary services...";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,message);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share Veterinary app via");
            startActivity(sendIntent);


        } else if (id == R.id.nav_about) {


        }  else if (id == R.id.nav_home) {

            toolbar.setTitle("Home->General Posts");

            //initializing the home fragment not even sure
            home = new Home();
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            fragmentManager.replace(R.id.main_container,home).commitNow();

        }else if (id == R.id.nav_myaccount) {
            startActivity(new Intent(MainPanel.this,AccountSetting.class));
        }else if (id == R.id.nav_password) {
            toolbar.setTitle("Password Reset");
            passwordReset = new PasswordReset();
            FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
            fragmentManager.replace(R.id.main_container,passwordReset).commitNow();
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exiting...");
            builder.setMessage("Are you sure you want to logout from your account?");
// Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    auth.signOut();

                    startActivity(new Intent(MainPanel.this,Login.class));
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.nav_deleteaccount) {
           firebaseFirestore.collection("Users").document(user_id).get()
                   .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                           if (task.isSuccessful()){
                               if (task.getResult().exists()){
                                   String membertype = task.getResult().getString("usetype");
                                   String admin = task.getResult().getString("admin");

                                   if (membertype.equals("Veterinarian") || admin.equals("admin")){
                                       Toast.makeText(MainPanel.this, "You cannot delete your account, contact your administrator", Toast.LENGTH_SHORT).show();
                                   }else {
                                       new AlertDialog.Builder(MainPanel.this)
                                               .setTitle("Account Deletion")
                                               .setMessage("Are you sure you want to DELETE your account?")
                                               .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialogInterface, int i) {
                                                       firebaseFirestore.collection("Users").document(user_id).delete();
                                                       auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {

                                                               Toast.makeText(MainPanel.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                                           }
                                                       }).addOnFailureListener(new OnFailureListener() {
                                                           @Override
                                                           public void onFailure(@NonNull Exception e) {
                                                               Toast.makeText(MainPanel.this, "Error:\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                                                           }
                                                       });
                                                   }
                                               }).show();
                                   }
                               }
                           }
                       }
                   });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected void onStart() {

        super.onStart();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null)
        {
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful())
                    {
                        if (!task.getResult().exists())
                        {
                            startActivity(new Intent(MainPanel.this,AccountSetting.class));

                        }

                    }

                }
            });

        }
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

    private void issueWithImage(final String Issue,final String my_user_Id, final Dialog dialog){
        String randomName = UUID.randomUUID().toString();
        final StorageReference reference = storageReference.child("GeneralImages").child(randomName+".jpg");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String our_image_url = uri.toString();
                            //sending the issue with image to database
                            Map<String, Object> objectMap = new HashMap<>();
                            objectMap.put("issue",Issue);
                            objectMap.put("myuser_id",my_user_Id);
                            objectMap.put("timeStamp", FieldValue.serverTimestamp());
                            objectMap.put("imageUrl",our_image_url);
                            /*
                            * SENDING THE ABOVE DATA TO DB
                            *
                            * */
                            firebaseFirestore.collection("GeneralPosts").add(objectMap).addOnCompleteListener(
                                    new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(MainPanel.this, "Issue successfully submitted", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainPanel.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    private void issueWithoutImage(final String Issue, final String my_user_Id, final Dialog dialog){
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("issue",Issue);
        objectMap.put("myuser_id",my_user_Id);
        objectMap.put("timeStamp", FieldValue.serverTimestamp());
                            /*
                            * SENDING THE ABOVE DATA TO DB
                            *
                            * */
        firebaseFirestore.collection("GeneralPosts").add(objectMap).addOnCompleteListener(
                new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(MainPanel.this, "Issue successfully submitted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainPanel.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showingFloatButton(String user_id, final FloatingActionButton floatingActionButton){
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String membertype = task.getResult().getString("usetype");
                                String admin  = task.getResult().getString("admin");

                                if (membertype.equals("Veterinarian") || admin.equals("admin")){

                                    floatingActionButton.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }
}
