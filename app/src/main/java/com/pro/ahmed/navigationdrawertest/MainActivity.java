package com.pro.ahmed.navigationdrawertest;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.communicationFragment.communicationFragment;
import com.pro.ahmed.navigationdrawertest.model.UserDetails;
import com.pro.ahmed.navigationdrawertest.myVideosFragment.MyVideosFragment;
import com.pro.ahmed.navigationdrawertest.sayhiFragment.SayHi;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    CircleImageView circleImageViewProfileIm;
    TextView profileName;

    private FragmentManager fragmentManager;

    private DatabaseReference databaseReference;
    private String photoUri, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Nearby people");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        fragmentManager = getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        profileName = (TextView) header.findViewById(R.id.profile_name);
        circleImageViewProfileIm = (CircleImageView) header.findViewById(R.id.circleViewProfileIm);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                UserDetails curr_UserDetails = dataSnapshot.child(userUid).getValue(UserDetails.class);
                photoUri = curr_UserDetails.getUserPhoto();
                userName = curr_UserDetails.getUserName();
                profileName.setText(userName);
                Glide.with(getApplicationContext()).load(photoUri).into(circleImageViewProfileIm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fragmentShow(SayHi.class);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Class fragment = null;

        if (id == R.id.nav_sayHi) {
            fragment = SayHi.class;
            toolbar.setTitle("Nearby people");
            fragmentShow(fragment);
        } else if (id == R.id.nav_communication) {
            fragment = communicationFragment.class;
            fragmentShow(fragment);

        } else if (id == R.id.nav_myVideos) {
            fragment = MyVideosFragment.class;
            fragmentShow(fragment);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fragmentShow(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flcontent, fragment).commit();*/
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userState").setValue("offline");
        Toast.makeText(this, "Offline DEst", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userState").setValue("offline");
        Toast.makeText(this, "Offline Pause", Toast.LENGTH_SHORT).show();

    }

}
