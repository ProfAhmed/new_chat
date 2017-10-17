package com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.UsersAdapter;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatsFragment extends Fragment {

    @BindView(R.id.RvFindFragment)
    RecyclerView rvFindFragment;
    List<UserDetails> users;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userUid;
    String curr_Id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, view);
        users = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        userUid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading!");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserDetails userDetails = postSnapshot.getValue(UserDetails.class);
                    if (userDetails.getCurr_id().equals(userUid)) {
                        users.add(userDetails);
                    }
                }
                //Log.d("Data", users.get(0).toString());
                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                rvFindFragment.setHasFixedSize(true);
                rvFindFragment.setLayoutManager(llm);
                UsersAdapter adapter = new UsersAdapter(getContext(), users);
                rvFindFragment.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error FireBase Database Reading", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_chat, menu);
    }
}
