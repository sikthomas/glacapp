package com.veterinary.Account;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.veterinary.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class AllMyIssues extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private List<IssueList> issueLists;
    private IssueAdapter issueAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth auth;
    private String my_current_user_id,membertype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_issues);

        firebaseFirestore = FirebaseFirestore.getInstance();
        issueLists = new ArrayList<>();
        issueAdapter = new IssueAdapter(issueLists);
        auth = FirebaseAuth.getInstance();
        my_current_user_id = auth.getCurrentUser().getUid();


        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_my_issues);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllMyIssues.this));
        mRecyclerView.setAdapter(issueAdapter);

        firebaseFirestore.collection("Users").document(my_current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        membertype=task.getResult().getString("usetype");
                        String county=task.getResult().getString("mycounty");

                        if (membertype.equals("Farmer")){

                            Query query =  firebaseFirestore.collection("AllIssues")
                                    .document(county).collection(county+"Issues").whereEqualTo("myuser_id",my_current_user_id).orderBy("timeStamp");
                            query.addSnapshotListener(AllMyIssues.this, new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                        if (doc.getType() == DocumentChange.Type.ADDED) {

                                            String postID = doc.getDocument().getId();
                                            IssueList issueList = doc.getDocument().toObject(IssueList.class).withId(postID);

                                            issueLists.add(issueList);

                                            issueAdapter.notifyDataSetChanged();

                                        }
                                    }


                                }
                            });


                        }else {

                            Query query =  firebaseFirestore.collection("AllIssues")
                                    .document(county).collection(county+"Issues").whereEqualTo("vet_id",my_current_user_id).orderBy("timeStamp");
                            query.addSnapshotListener(AllMyIssues.this, new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                        if (doc.getType() == DocumentChange.Type.ADDED) {

                                            String postID = doc.getDocument().getId();
                                            IssueList issueList = doc.getDocument().toObject(IssueList.class).withId(postID);

                                            issueLists.add(issueList);

                                            issueAdapter.notifyDataSetChanged();

                                        }
                                    }


                                }
                            });

                        }

                    }
                }
            }
        });





    }


    private void getAllIssues(String Membertype)
    {
        if(Membertype.equals("Farmer")){

            Query query =  firebaseFirestore.collection("Users").document(my_current_user_id).collection("MyIssues").orderBy("timeStamp");
            query.addSnapshotListener(AllMyIssues.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postID = doc.getDocument().getId();
                            IssueList issueList = doc.getDocument().toObject(IssueList.class).withId(postID);

                            issueLists.add(issueList);

                            issueAdapter.notifyDataSetChanged();

                        }
                    }


                }
            });

        }else {
            Query query =  firebaseFirestore.collection("Veterinary").document("AllVeterinary").collection("Approved").document(my_current_user_id)
                .collection("Issues").orderBy("timeStamp");
            query.addSnapshotListener(AllMyIssues.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String postID = doc.getDocument().getId();
                            IssueList issueList = doc.getDocument().toObject(IssueList.class).withId(postID);

                            issueLists.add(issueList);

                            issueAdapter.notifyDataSetChanged();

                        }
                    }


                }
            });



        }
    }
}
