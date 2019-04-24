package com.veterinary.Account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.veterinary.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class AllGeneralIssues extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private List<IssueList> issueLists;
    private GeneralIssueAdapter issueAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_general_issues);

        firebaseFirestore = FirebaseFirestore.getInstance();
        issueLists = new ArrayList<>();
        issueAdapter = new GeneralIssueAdapter(issueLists);
        auth = FirebaseAuth.getInstance();

        setTitle("Home->General Posts");

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_general_issues);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllGeneralIssues.this));
        mRecyclerView.setAdapter(issueAdapter);

        Query query =  firebaseFirestore.collection("GeneralPosts").orderBy("timeStamp");
        query.addSnapshotListener(AllGeneralIssues.this, new EventListener<QuerySnapshot>() {
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
