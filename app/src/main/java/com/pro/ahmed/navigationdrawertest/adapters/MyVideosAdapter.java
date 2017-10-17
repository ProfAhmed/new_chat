package com.pro.ahmed.navigationdrawertest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.pro.ahmed.navigationdrawertest.myVideosFragment.childFragments.UploadedVideosFragment;
import com.pro.ahmed.navigationdrawertest.myVideosFragment.childFragments.LikedVideosFragment;
import com.pro.ahmed.navigationdrawertest.myVideosFragment.childFragments.UnlockedVideosFragment;

/**
 * Created by hp on 07/09/2017.
 */

public class MyVideosAdapter extends FragmentPagerAdapter {
    private final String title[] =
            {"LIKED", "UNLOCKED", "UPLOADS"};
    private final Fragment fragment[] = new Fragment[title.length];

    public MyVideosAdapter(FragmentManager fm) {
        super(fm);
        fragment[0] = new LikedVideosFragment();
        fragment[1] = new UnlockedVideosFragment();
        fragment[2] = new UploadedVideosFragment();

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
