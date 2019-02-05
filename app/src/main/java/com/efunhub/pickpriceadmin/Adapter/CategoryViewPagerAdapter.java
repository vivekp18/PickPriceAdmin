package com.efunhub.pickpriceadmin.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.efunhub.pickpriceadmin.Fragment.TabFragment;
import com.efunhub.pickpriceadmin.Model.FragmentCategoryModel;

import java.util.ArrayList;

/**
 * Created by Admin on 04-01-2019.
 */

public class CategoryViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<FragmentCategoryModel> mFragmentList = new ArrayList<>();
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();

    public CategoryViewPagerAdapter(FragmentManager fm, ArrayList<FragmentCategoryModel> fragmentCategoryModelArrayList, ArrayList<String> titleLists) {
        super(fm);
        this.mFragmentList = fragmentCategoryModelArrayList;
        this.mFragmentTitleList = titleLists;
    }

    @Override
    public Fragment getItem(int position) {

        FragmentCategoryModel fragmentCategoryModel = mFragmentList.get(position);

        return TabFragment.newInstance(fragmentCategoryModel.getArrayList());
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
