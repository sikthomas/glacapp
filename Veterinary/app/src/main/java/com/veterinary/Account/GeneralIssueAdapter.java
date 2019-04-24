package com.veterinary.Account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.veterinary.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sikanga on 03/03/2019.
 */

public class GeneralIssueAdapter extends RecyclerView.Adapter<GeneralIssueAdapter.ViewHolder> {
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<IssueList> issueLists;
    private String user_ID;


    public GeneralIssueAdapter(List<IssueList> approveLists) {
        this.issueLists = approveLists;
    }

    @Override
    public GeneralIssueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.general_issue_item_display,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GeneralIssueAdapter.ViewHolder holder, int position) {
        String issue_owner_user_id=issueLists.get(position).getMyuser_id();
        final String imageUrl = issueLists.get(position).getImageUrl();
        final String issue = issueLists.get(position).getIssue();

        firebaseFirestore.collection("Users").document(issue_owner_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult().exists()){
                                final String fname=task.getResult().getString("fname");
                                final String lname=task.getResult().getString("lname");
                                String membetype = task.getResult().getString("usetype");
                                final String phonenumber=task.getResult().getString("phonenumber");
                                final String image_url=task.getResult().getString("imageUrl");


                                if (imageUrl !=null){

                                    holder.setDedails(fname+" "+lname,membetype,issue,imageUrl,image_url);
                                    holder.imageIsue.setVisibility(View.VISIBLE);

                                }else {


                                    holder.setDedailsWithoutImage(fname+" "+lname,membetype,issue,image_url);
                                }
                                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent=new Intent(context,PosterDetails.class);
                                        intent.putExtra("fname",fname);
                                        intent.putExtra("lname",lname);
                                        intent.putExtra("phonenumber",phonenumber);
                                        intent.putExtra("imageUrll",image_url);
                                        view.getContext().startActivity(intent);
                                    }
                                });

                            }
                        }

                    }
                });

    }

    @Override
    public int getItemCount() {
        return issueLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,type,issue;
        private ImageView imageIsue;
        private CircleImageView circleImageView;
        private LinearLayout linearLayout;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            imageIsue = (ImageView)view.findViewById(R.id.image_issue_sent_general);
            circleImageView=(CircleImageView)view.findViewById(R.id.general_image);
            linearLayout=(LinearLayout)view.findViewById(R.id.poster_detail);
        }
        public void setDedails(String vetName,String Type,String Issue,String imageUrl,String profile){
            name=(TextView)view.findViewById(R.id.tv_fullname_sent_issue_general);
            type=(TextView)view.findViewById(R.id.tv_membertype_sent_issue_general);
            issue=(TextView)view.findViewById(R.id.tv_isseu_display_genera);
            imageIsue = (ImageView)view.findViewById(R.id.image_issue_sent_general);
            circleImageView=(CircleImageView)view.findViewById(R.id.general_image);

            name.setText(vetName);
            type.setText(Type);
            issue.setText(Issue);

            //Settig image
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.fitCenter();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(imageIsue);


            RequestOptions requestOption = new RequestOptions();
            requestOption.centerCrop();
            requestOption.fitCenter();
            Glide.with(context).applyDefaultRequestOptions(requestOption).load(profile).into(circleImageView);

        }
        public void setDedailsWithoutImage(String vetName,String Type,String Issue,String mProfile){
            name=(TextView)view.findViewById(R.id.tv_fullname_sent_issue_general);
            type=(TextView)view.findViewById(R.id.tv_membertype_sent_issue_general);
            issue=(TextView)view.findViewById(R.id.tv_isseu_display_genera);
            imageIsue = (ImageView)view.findViewById(R.id.image_issue_sent_general );

            name.setText(vetName);
            type.setText(Type);
            issue.setText(Issue);


            RequestOptions requestOption = new RequestOptions();
            requestOption.centerCrop();
            requestOption.fitCenter();
            Glide.with(context).applyDefaultRequestOptions(requestOption).load(mProfile).into(circleImageView);

        }

    }
}
