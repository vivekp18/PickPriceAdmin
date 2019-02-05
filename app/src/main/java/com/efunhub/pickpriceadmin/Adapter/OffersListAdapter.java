package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Model.Offers;
import com.efunhub.pickpriceadmin.R;

import java.util.ArrayList;

/**
 * Created by Admin on 27-12-2018.
 */

public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Offers> offersArrayList = new ArrayList<>();
    private String productId;

    private Offers offersModel;

    public OffersListAdapter(Context mContext, ArrayList<Offers> offersArrayList) {
        this.mContext = mContext;
        this.offersArrayList = offersArrayList;

    }

    @Override
    public OffersListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_offers_list_item, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        offersModel = offersArrayList.get(position);

        holder.tvOffers.setText(offersModel.getOffer());



    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvOffers;
        ImageView ivDelete;

        ItemViewHolder(View itemView) {
            super(itemView);

            tvOffers = itemView.findViewById(R.id.tv_offers);

        }
    }
}
