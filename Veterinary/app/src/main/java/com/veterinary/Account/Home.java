package com.veterinary.Account;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private View view;
    private FirebaseFirestore firebaseFirestore;
    private List<IssueList> issueLists;
    private GeneralIssueAdapter issueAdapter;
    private RecyclerView mRecyclerView;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        issueLists = new ArrayList<>();
        issueAdapter = new GeneralIssueAdapter(issueLists);
        progressDialog=new ProgressDialog(getContext());
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_general_issues);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(issueAdapter);

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Query query =  firebaseFirestore.collection("GeneralPosts").orderBy("timeStamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String postID = doc.getDocument().getId();
                        IssueList issueList = doc.getDocument().toObject(IssueList.class).withId(postID);

                        issueLists.add(issueList);

                        issueAdapter.notifyDataSetChanged();

                        progressDialog.dismiss();
                    }
                }


            }
        });


        return view;
    }

}
