package com.veterinary.Account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.veterinary.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sikanga on 03/03/2019.
 */

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<IssueList> issueLists;
    private String user_ID;


    public IssueAdapter(List<IssueList> approveLists) {
        this.issueLists = approveLists;
    }

    @Override
    public IssueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.isue_item_display,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IssueAdapter.ViewHolder holder, int position) {
        final String issue_owner_user_id=issueLists.get(position).getMyuser_id();
        final String isseu_document_id = issueLists.get(position).postID;
        final String imageUrl = issueLists.get(position).getImageUrl();
        final String issue = issueLists.get(position).getIssue();
        final String current_user_id = auth.getCurrentUser().getUid();
        final String mreply=issueLists.get(position).getReply();

        firebaseFirestore.collection("Users").document(issue_owner_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult().exists()){
                                final String fname=task.getResult().getString("fname");
                                final String lname=task.getResult().getString("lname");
                                final String phonenumber=task.getResult().getString("phonenumber");
                                String membetype = task.getResult().getString("usetype");
                                if (imageUrl !=null){

                                    holder.setDedails(fname+" "+lname,membetype,issue,mreply,imageUrl);
                                    holder.imageIsue.setVisibility(View.VISIBLE);

                                }else {


                                    holder.setDedailsWithoutImage(fname+" "+lname,membetype,mreply,issue);
                                }


                                //makiing a call
                                holder.mCalling.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        holder.makeCall(phonenumber,view);
                                    }
                                });
                            }
                        }

                    }
                });



        //Done with the issue

        holder.mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Issue Deletion")
                        .setMessage("Are you sure you want to delete the issue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                holder.doneWithIssue(isseu_document_id,current_user_id);
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        //Done with  issue ends here

    }

    @Override
    public int getItemCount() {
        return issueLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,type,issue,reply;
        private ImageView imageIsue;
        private FloatingActionButton mCalling;
        private Button mDone,mMessage;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;

            mCalling = (FloatingActionButton)view.findViewById(R.id.btn_call_issue);
            mDone = (Button)view.findViewById(R.id.btn_done_with_issue);
            imageIsue = (ImageView)view.findViewById(R.id.image_issue_sent);
            mMessage=(Button)view.findViewById(R.id.btn_reply);
        }
        public void setDedails(String vetName,String Type,String Issue,String Reply,String imageUrl){
            name=(TextView)view.findViewById(R.id.tv_fullname_sent_issue);
            type=(TextView)view.findViewById(R.id.tv_membertype_sent_issue);
            issue=(TextView)view.findViewById(R.id.tv_isseu_display);
            reply=(TextView)view.findViewById(R.id.tv_reply_display);
            imageIsue = (ImageView)view.findViewById(R.id.image_issue_sent);

            name.setText(vetName);
            type.setText(Type);
            issue.setText(Issue);
            reply.setText(Reply);

            //Settig image
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.fitCenter();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(imageIsue);

        }
        public void setDedailsWithoutImage(String vetName,String Type,String Reply,String Issue){
            name=(TextView)view.findViewById(R.id.tv_fullname_sent_issue);
            type=(TextView)view.findViewById(R.id.tv_membertype_sent_issue);
            issue=(TextView)view.findViewById(R.id.tv_isseu_display);
            reply=(TextView)view.findViewById(R.id.tv_reply_display);
            imageIsue = (ImageView)view.findViewById(R.id.image_issue_sent);

            name.setText(vetName);
            type.setText(Type);
            issue.setText(Issue);
            reply.setText(Reply);

        }

        @SuppressLint("MissingPermission")
        public void makeCall(String Phonenumber, View view){
            Intent sIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+Phonenumber));
            sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(sIntent);

        }

        public void doneWithIssue(String issue_id,String the_Current_user_id){

            firebaseFirestore.collection("Veterinary").document("AllVeterinary").collection("Approved").document(the_Current_user_id)
                    .collection("Issues").document(issue_id).delete();



        }

        public void replyIssueVet(String owner_userid,String documentId,String Reply){
            Map<String,Object> objectMap=new HashMap<>();
            objectMap.put("reply",Reply);
            objectMap.put("timeStamp", FieldValue.serverTimestamp());
            firebaseFirestore.collection("Veterinary").document("AllVeterinary").collection("Approved").document(auth.getCurrentUser().getUid())
                    .collection("Issues").document(documentId)
                    .update(objectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(context, "Reply sent successfully", Toast.LENGTH_SHORT).show();
                    }else
                    {

                        Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

    }

}
