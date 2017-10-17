package com.pro.ahmed.navigationdrawertest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments.ChatsFragment;
import com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments.FindFragment;
import com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments.GroupsFragment;
import com.pro.ahmed.navigationdrawertest.sayhiFragment.childFragments.VideosFragment;

public class SayHiAdapter extends FragmentPagerAdapter {

    private final String title[] =
            {"Find", "Chats", "Videos", "Groups"};
    String[] tabsTitle = {"Find", "Chat", "Videos", "Groups"};
    private final Fragment fragment[] = new Fragment[title.length];

    public SayHiAdapter(FragmentManager fm) {
        super(fm);
        fragment[0] = new FindFragment();
        fragment[1] = new ChatsFragment();
        fragment[2] = new VideosFragment();
        fragment[3] = new GroupsFragment();
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

