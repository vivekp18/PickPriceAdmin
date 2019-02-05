package com.efunhub.pickpriceadmin.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.efunhub.pickpriceadmin.Adapter.ProductListAdapter;
import com.efunhub.pickpriceadmin.Model.TabCategoryModel;
import com.efunhub.pickpriceadmin.R;

import java.util.ArrayList;

/**
 * Created by Admin on 04-01-2019.
 */

public class TabFragment extends Fragment {

    private View view;
    private RecyclerView rvCategory;

    private ProductListAdapter productListAdapter;

    public static TabFragment newInstance(ArrayList<TabCategoryModel> tabCategoryModelArrayList) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", tabCategoryModelArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab, container, false);

        Bundle bundle = getArguments();

        ArrayList<TabCategoryModel> arraylist  = bundle.getParcelableArrayList("list");

        rvCategory = view.findViewById(R.id.rvCategory);

        productListAdapter = new ProductListAdapter(getContext(), arraylist);
        rvCategory.setHasFixedSize(true);
        rvCategory.setNestedScrollingEnabled(false);
        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvCategory.setItemAnimator(new DefaultItemAnimator());
        rvCategory.setAdapter(productListAdapter);

        return view;
    }

}
