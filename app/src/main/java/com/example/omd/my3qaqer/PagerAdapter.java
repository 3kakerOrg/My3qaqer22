package com.example.omd.my3qaqer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.omd.my3qaqer.fragments.searchFragment_pager;

/**
 * Created by Delta on 02/07/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numofTab;

    public PagerAdapter(FragmentManager fm ,int numofTab) {
        super(fm);
        this.numofTab = numofTab;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new searchFragment_pager();

            case 1:
                return new loginFragment_pager();
            case 2:
                return new registerFragment_pager();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numofTab;
    }
}
