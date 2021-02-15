package com.sharad.brainywood.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sharad.brainywood.CourseFragment;
import com.sharad.brainywood.HomeFragment;
import com.sharad.brainywood.LiveFragment;
import com.sharad.brainywood.QnAFragment;
import com.sharad.brainywood.SliderFourFragment;
import com.sharad.brainywood.SliderOneFragment;
import com.sharad.brainywood.SliderThreeFragment;
import com.sharad.brainywood.SliderTwoFragment;


public class SliderAdapter extends FragmentPagerAdapter {


    public SliderAdapter(@NonNull FragmentManager fm) {
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
                return new SliderOneFragment();
            case 1:
                return new SliderTwoFragment();
            case 2:
                return new SliderThreeFragment();
            case 3:
                return new SliderFourFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
}
