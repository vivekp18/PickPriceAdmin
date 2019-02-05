package com.efunhub.pickpriceadmin.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Adapter.BrandsListAdapter;
import com.efunhub.pickpriceadmin.Adapter.ProductDealerListAdapter;
import com.efunhub.pickpriceadmin.R;

import java.util.ArrayList;

public class ProductDealerListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvProductDealerList;
    private ProductDealerListAdapter productDealerListAdapter;

    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_dealer_list);

        init();
        setupToolbar();

        arrayList.add("Ashish");
        arrayList.add("Parth");

        productDealerListAdapter = new ProductDealerListAdapter(this, arrayList);
        rvProductDealerList.setHasFixedSize(true);
        rvProductDealerList.setNestedScrollingEnabled(false);
        rvProductDealerList.setLayoutManager(new GridLayoutManager(this, 1));
        rvProductDealerList.setItemAnimator(new DefaultItemAnimator());
        rvProductDealerList.setAdapter(productDealerListAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Dealers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
        rvProductDealerList = findViewById(R.id.rvProductDealerList);
        arrayList = new ArrayList<>();
    }
}
