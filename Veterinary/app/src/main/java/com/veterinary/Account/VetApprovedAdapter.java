package com.veterinary.Account;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sikanga on 03/03/2019.
 */

public class VetApprovedAdapter extends RecyclerView.Adapter<VetApprovedAdapter.ViewHolder> implements Filterable{
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private List<ApproveList> approveLists;
    private List<ApproveList> filtered_vets;
    private String user_ID;


    public VetApprovedAdapter(List<ApproveList> approveLists) {

        this.approveLists = approveLists;
        this.filtered_vets = approveLists;
    }

    @Override
    public VetApprovedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vet_list_display,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VetApprovedAdapter.ViewHolder holder, int position) {
        String vet_user_id=filtered_vets.get(position).getUser_ID();
        final String PostId = filtered_vets.get(position).postID;

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
                                final String imageUrl  = task.getResult().getString("imageUrl");

                                holder.setDedails(fname+" "+lname,county,imageUrl);

                                holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i=new Intent(context,IssueToVet.class);
                                        i.putExtra("fname",fname);
                                        i.putExtra("lname",lname);
                                        i.putExtra("idnumber",idnumber);
                                        i.putExtra("phonenumber",phonenumber);
                                        i.putExtra("mycounty",county);
                                        i.putExtra("postId",PostId);
                                        i.putExtra("imageUrl",imageUrl);
                                        view.getContext().startActivity(i);
                                    }
                                });
                            }
                        }

                    }
                });
    }

    @Override
    public int getItemCount()
    {
        return filtered_vets.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtered_vets = approveLists;
                } else {
                    List<ApproveList> filteredList = new ArrayList<>();
                    for (ApproveList row : approveLists) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getFullname().toLowerCase().contains(charString.toLowerCase()) ||row.getFullname().toUpperCase().contains(charString.toUpperCase()) || row.getMycounty().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filtered_vets = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_vets;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered_vets = (ArrayList<ApproveList>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,county;
        private CircleImageView profileImage;
        private ConstraintLayout mConstraintLayout;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            mConstraintLayout=(ConstraintLayout) view.findViewById(R.id.constraint_vet);
        }
        public void setDedails(String vetName,String County,String imageUrl){
            name=(TextView)view.findViewById(R.id.tv_vet_fullname);
            county=(TextView)view.findViewById(R.id.tv_vet_county);
            profileImage = (CircleImageView)view.findViewById(R.id.vet_image_profile);

            name.setText(vetName);
            county.setText(County);

            //setting image to imageView
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(profileImage);

        }

    }
}
