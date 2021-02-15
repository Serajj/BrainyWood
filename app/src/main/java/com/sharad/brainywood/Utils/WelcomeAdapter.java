package com.sharad.brainywood.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sharad.brainywood.CourseFragment;
import com.sharad.brainywood.HomeFragment;
import com.sharad.brainywood.LiveFragment;
import com.sharad.brainywood.QnAFragment;


public class WelcomeAdapter extends FragmentPagerAdapter {


    public WelcomeAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new CourseFragment();
            case 2:
                return new LiveFragment();
            case 3:
                return new QnAFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
}
