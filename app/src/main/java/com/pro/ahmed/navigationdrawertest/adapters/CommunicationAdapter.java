package com.pro.ahmed.navigationdrawertest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.pro.ahmed.navigationdrawertest.communicationFragment.childFragments.MyFriendsFragments;
import com.pro.ahmed.navigationdrawertest.communicationFragment.childFragments.MyGroupsFragment;

/**
 * Created by hp on 07/09/2017.
 */

public class CommunicationAdapter extends FragmentPagerAdapter {
    private final String title[] =
            {"HOTLIST", "GROUPS"};
    private final Fragment fragment[] = new Fragment[title.length];

    public CommunicationAdapter(FragmentManager fm) {
        super(fm);
        fragment[0] = new MyFriendsFragments();
        fragment[1] = new MyGroupsFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Log.v("Title-Item = ", String.valueOf(position));
        return fragment[position];
    }


    @Override
    public CharSequence getPageTitle(int position) {
        Log.v("Title-PageTitle = ", title[position]);
        return title[position];
    }


    @Override
    public int getCount() {
        return fragment.length;
    }
}


