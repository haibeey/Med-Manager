package com.haibeey.android.medmanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.haibeey.android.medmanager.fragments.HomeFragment;
import com.haibeey.android.medmanager.fragments.SetMedicationFragment;

/**
 * Created by haibeey on 3/27/2018.
 */

public class AdapterForMainActivityTabLayout  extends FragmentPagerAdapter{

    HomeFragment homeFragment;
    SetMedicationFragment setMedicationFragment;

    public AdapterForMainActivityTabLayout(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(homeFragment!=null)return homeFragment;
                homeFragment=HomeFragment.newInstance();
                return homeFragment;
            case 1:
                if(setMedicationFragment!=null)return setMedicationFragment;
                setMedicationFragment=SetMedicationFragment.newInstance();
                //sets Home medication as listener
                setMedicationFragment.setListener(homeFragment);

                return setMedicationFragment;
            default:
                return HomeFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return  "Home";
            case 1:
                return "Set Med";
            default:
                return "Home";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
