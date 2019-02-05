package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.efunhub.pickpriceadmin.Activity.ProductListActivity;
import com.efunhub.pickpriceadmin.R;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;

public class ProductDealerListAdapter extends RecyclerView.Adapter<ProductDealerListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<String> arrayList;

    public ProductDealerListAdapter(Context mContext, ArrayList<String> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_dealer_list_itme, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfilePic;

        ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, ProductListActivity.class));
                }
            });
        }
    }
}
