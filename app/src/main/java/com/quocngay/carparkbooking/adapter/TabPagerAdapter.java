package com.quocngay.carparkbooking.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.quocngay.carparkbooking.fragment.HistoryFragment;
import com.quocngay.carparkbooking.fragment.MapFragment;
import com.quocngay.carparkbooking.fragment.OpenTicketsFragment;
import com.quocngay.carparkbooking.fragment.ProfileFragment;

/**
 * Created by ninhh on 6/4/2017.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    public static final int TAB_NUMBER = 4;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OpenTicketsFragment();
            case 1:
                return new MapFragment();
            case 2:
                return new HistoryFragment();
            case 3:
                return new ProfileFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return TAB_NUMBER;
    }
}
