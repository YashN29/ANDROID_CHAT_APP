package com.example.bingbong.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPager_Adapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;
    ArrayList<String> tittles;

    public ViewPager_Adapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.tittles  = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragments(Fragment fragment , String tittle)
    {
       fragments.add(fragment);
       tittles.add(tittle);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tittles.get(position);
    }
}
