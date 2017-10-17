package com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments;

import android.app.ProgressDialog;
import android.location.Location;
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


public class FindFragment extends Fragment {

    @BindView(R.id.RvFindFragment)
    RecyclerView rvFindFragment;
    List<UserDetails> users;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userUid;

    public FindFragment() {
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
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        ButterKnife.bind(this, view);
        users = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        userUid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading!");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                float curr_latitude;
                float curr_longitude;
                UserDetails curr_UserDetails = dataSnapshot.child(userUid).getValue(UserDetails.class);
                curr_latitude = curr_UserDetails.getLatitude();
                curr_longitude = curr_UserDetails.getLongitude();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserDetails userDetails = postSnapshot.getValue(UserDetails.class);
                    float user_latitude = userDetails.getLatitude();
                    float user_longitude = userDetails.getLongitude();
                    float distance_arr[] = new float[10];
                    Location.distanceBetween(curr_latitude, curr_longitude, user_latitude, user_longitude, distance_arr);
                    float distance = distance_arr[0] / 1000;
                    distance = (float) (Math.floor(distance * 10) / 10);

                    if (!(userDetails.getUserId().equals(userUid))) {
                        if (distance <= 100) {
                            userDetails.setDistance(distance);
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
        getActivity().getMenuInflater().inflate(R.menu.menu_find, menu);
    }
}
