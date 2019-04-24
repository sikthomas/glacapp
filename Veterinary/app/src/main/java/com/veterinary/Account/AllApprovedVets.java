package com.veterinary.Account;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

public class AllApprovedVets extends AppCompatActivity {
    private SearchView searchView;
    private List<ApproveList> approveLists;
    private VetApprovedAdapter vetApprovedAdapter;
    private FirebaseFirestore firebaseFirestore;
    private String user_id,memberType,sendissue;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_approved_vets);

        approveLists = new ArrayList<>();
        vetApprovedAdapter = new VetApprovedAdapter(approveLists);
        mSwipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipe_approved_vets);
        mRecyclerView = (RecyclerView)findViewById(R.id.vet_recycler_approved);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllApprovedVets.this));
        mRecyclerView.setAdapter(vetApprovedAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        sendissue = getIntent().getExtras().getString("sendissue");

        //checking member tyoe
        if (sendissue.equals("reply")){
            Query query = firebaseFirestore.collection("Users").document(user_id).collection("MyIssues").
                    orderBy("timeStamp", Query.Direction.DESCENDING);
            query.addSnapshotListener(AllApprovedVets.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (final DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges())
                    {
                        if (documentChange.getType() == DocumentChange.Type.ADDED)
                        {
                            final String allvets_ids  = documentChange.getDocument().getString("vet_id");
                            firebaseFirestore.collection("Users").document(user_id).get().
                                    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                if (task.getResult().exists())
                                                {
                                                    String mycounty = task.getResult().getString("mycounty");
                                                    Query query = firebaseFirestore.collection("Veterinary").document("AllVeterinary").collection("Approved")
                                                            .document("RealVets").collection(mycounty).whereEqualTo("user_ID",allvets_ids).orderBy("timeStamp");
                                                    query.addSnapshotListener(AllApprovedVets.this, new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                    String postID = doc.getDocument().getId();
                                                                    ApproveList approveList = doc.getDocument().toObject(ApproveList.class).withId(postID);

                                                                    approveLists.add(approveList);

                                                                    vetApprovedAdapter.notifyDataSetChanged();

                                                                }
                                                            }


                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });



                        }
                    }
                }
            });

        }else {
            firebaseFirestore.collection("Users").document(user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().exists()){
                                    memberType = task.getResult().getString("usetype");
                                    String mycounty = task.getResult().getString("mycounty");
                                    String admin = task.getResult().getString("admin");

                                    if (memberType.equals("Veterinarian") || memberType.equals("Farmer")){

                                        Query query = firebaseFirestore.collection("Veterinary").document("AllVeterinary").collection("Approved")
                                                .document("RealVets").collection(mycounty).orderBy("timeStamp");
                                        query.addSnapshotListener(AllApprovedVets.this, new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                                        String postID = doc.getDocument().getId();
                                                        ApproveList approveList = doc.getDocument().toObject(ApproveList.class).withId(postID);

                                                        approveLists.add(approveList);

                                                        vetApprovedAdapter.notifyDataSetChanged();

                                                    }
                                                }


                                            }
                                        });
                                    }else if (memberType.equals("Farmer") && admin.equals("admin")){
                                        //show the dialog
                                        final Dialog dialog = new Dialog(AllApprovedVets.this);
                                        dialog.setTitle("County Selection");
                                        dialog.setContentView(R.layout.dialog_county_selection);

                                        //dialg itemms
                                        final Spinner counties = (Spinner)dialog.findViewById(R.id.sp_county_to_select);
                                        Button go = (Button)dialog.findViewById(R.id.btn_county_go);
                                        //setting on click
                                        go.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String SelectedCounty = counties.getSelectedItem().toString();
                                                if (counties.getSelectedItemPosition() == 0){
                                                    Query query = firebaseFirestore.collection("Veterinary").document("AllVeterinary")
                                                            .collection("Approved").orderBy("timeStamp", Query.Direction.DESCENDING);
                                                    query.addSnapshotListener(AllApprovedVets.this, new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                    String postID = doc.getDocument().getId();
                                                                    ApproveList approveList = doc.getDocument().toObject(ApproveList.class).withId(postID);

                                                                    approveLists.add(approveList);

                                                                    vetApprovedAdapter.notifyDataSetChanged();

                                                                }
                                                            }


                                                        }
                                                    });
                                                    dialog.dismiss();
                                                }else {

                                                    Query query = firebaseFirestore.collection("Veterinary").document("AllVeterinary")
                                                            .collection("Approved").document("RealVets").collection(SelectedCounty).orderBy("timeStamp", Query.Direction.DESCENDING);
                                                    query.addSnapshotListener(AllApprovedVets.this, new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {



                                                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                                                    String postID = doc.getDocument().getId();
                                                                    ApproveList approveList = doc.getDocument().toObject(ApproveList.class).withId(postID);

                                                                    approveLists.add(approveList);

                                                                    vetApprovedAdapter.notifyDataSetChanged();

                                                                }
                                                            }


                                                        }
                                                    });
                                                    dialog.dismiss();

                                                }
                                            }
                                        });

                                        dialog.show();


                                    }else {
                                        Toast.makeText(AllApprovedVets.this, "Seems you are not an administrator...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vet_search_menu, menu);// Associate searchable configuration with the SearchView

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search_vet)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                vetApprovedAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                vetApprovedAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
