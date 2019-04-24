package com.veterinary.Account;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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

public class  Vetrequest extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private List<ApproveList>approveLists;
    private ApproveAdapter approveAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vetrequest);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_vet_list);
        approveLists = new ArrayList<>();
        approveAdapter = new ApproveAdapter(approveLists);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(approveAdapter);


        Query query = firebaseFirestore.collection("Veterinary").document("AllVeterinary")
                .collection("Requests").orderBy("timeStamp");
        query.addSnapshotListener(Vetrequest.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        ApproveList loanList = doc.getDocument().toObject(ApproveList.class).withId(postID);

                        approveLists.add(loanList);

                        approveAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(Vetrequest.this, "No veterinarian request ", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }
}
