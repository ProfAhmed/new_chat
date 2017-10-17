package com.pro.ahmed.navigationdrawertest.sayhiFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pro.ahmed.navigationdrawertest.MainActivity;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.SayHiAdapter;


public class SayHi extends Fragment {

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public SayHi() {
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
        View view = inflater.inflate(R.layout.fragment_say_hi, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.sayHiViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.sayHitabs);
        SayHiAdapter adapter = new SayHiAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MainActivity mainActivity = (MainActivity) getActivity();
                switch (tab.getPosition()) {
                    case 0:
                        mViewPager.setCurrentItem(0);
                        mainActivity.toolbar.setTitle("Nearby people");
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        mainActivity.toolbar.setTitle("Messages");
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2);
                        mainActivity.toolbar.setTitle("Videos");
                        break;

                    case 3:
                        mViewPager.setCurrentItem(3);
                        mainActivity.toolbar.setTitle("Groups");
                        break;

                    default:

                        mViewPager.setCurrentItem(tab.getPosition());
                        mainActivity.toolbar.setTitle("F");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

}
