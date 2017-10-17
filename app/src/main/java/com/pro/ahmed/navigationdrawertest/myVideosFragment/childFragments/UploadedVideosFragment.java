package com.pro.ahmed.navigationdrawertest.myVideosFragment.childFragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.VideosAdapter;
import com.pro.ahmed.navigationdrawertest.model.Video;

import java.util.ArrayList;
import java.util.List;


public class UploadedVideosFragment extends Fragment {

    List<Video> videos;
    RecyclerView recyclerView;
    VideosAdapter videosAdapter;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String uId;

    public UploadedVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uploaded_videos, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvVideosList);
        videos = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uId = firebaseAuth.getCurrentUser().getUid();
                videos.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Video video = postSnapshot.getValue(Video.class);
                    Log.d("UserId", video.toString());
                    if (uId.equals(video.getUserId())) {
                        videos.add(video);
                    }

                    videosAdapter = new VideosAdapter(getActivity(), videos);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(videosAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_none, menu);
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
