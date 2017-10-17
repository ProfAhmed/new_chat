package com.pro.ahmed.navigationdrawertest.communicationFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pro.ahmed.navigationdrawertest.MainActivity;
import com.pro.ahmed.navigationdrawertest.R;
import com.pro.ahmed.navigationdrawertest.adapters.CommunicationAdapter;


public class communicationFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public communicationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("HotList");
        mViewPager = (ViewPager) view.findViewById(R.id.communicationViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.communicationTabs);
        CommunicationAdapter adapter = new CommunicationAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        mViewPager.setCurrentItem(0);
                        mainActivity.toolbar.setTitle("HotList");
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        mainActivity.toolbar.setTitle("Groups");
                        break;

                    default:
                        mViewPager.setCurrentItem(tab.getPosition());
                        mainActivity.toolbar.setTitle("HotList");
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
