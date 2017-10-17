package com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pro.ahmed.navigationdrawertest.R;


public class GroupsFragment extends Fragment {

    public GroupsFragment() {
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
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_find, menu);
    }

}
