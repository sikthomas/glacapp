package com.veterinary.Account;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.veterinary.R;

import java.util.List;

/**
 * Created by sikanga on 03/03/2019.
 */

public class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ViewHolder> {
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<ApproveList> approveLists;
    private String user_ID;


    public ApproveAdapter(List<ApproveList> approveLists) {

        this.approveLists = approveLists;
    }

    @Override
    public ApproveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vet_item_display,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ApproveAdapter.ViewHolder holder, int position) {
        String vet_user_id=approveLists.get(position).getUser_ID();
        final String PostId = approveLists.get(position).postID;

        firebaseFirestore.collection("Users").document(vet_user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult().exists()){
                                final String fname=task.getResult().getString("fname");
                                final String lname=task.getResult().getString("lname");
                                final String idnumber=task.getResult().getString("idnumber");
                                final String phonenumber=task.getResult().getString("phonenumber");
                                final String county=task.getResult().getString("mycounty");

                                holder.setDedails(fname+" "+lname,county);

                                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i=new Intent(context,Approval.class);
                                        i.putExtra("fname",fname);
                                        i.putExtra("lname",lname);
                                        i.putExtra("idnumber",idnumber);
                                        i.putExtra("phonenumber",phonenumber);
                                        i.putExtra("mycounty",county);
                                        i.putExtra("postId",PostId);
                                        view.getContext().startActivity(i);
                                    }
                                });
                            }
                        }

                    }
                });
    }

    @Override
    public int getItemCount() {
        return approveLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,county;
        private LinearLayout linearLayout;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            linearLayout=(LinearLayout)view.findViewById(R.id.approve_vet);
        }
        public void setDedails(String vetName,String County){
            name=(TextView)view.findViewById(R.id.vet_name);
            county=(TextView)view.findViewById(R.id.vet_county);
              name.setText(vetName);
            county.setText(County);

        }

    }
}
