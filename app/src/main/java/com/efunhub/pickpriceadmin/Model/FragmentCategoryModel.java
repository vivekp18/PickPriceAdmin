package com.efunhub.pickpriceadmin.Model;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by Admin on 04-01-2019.
 */

public class FragmentCategoryModel {

    private Fragment fragment;
    private ArrayList<TabCategoryModel> arrayList;


    public FragmentCategoryModel() {}

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public ArrayList<TabCategoryModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<TabCategoryModel> arrayList) {
        this.arrayList = arrayList;
    }
}
