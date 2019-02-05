package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Model.Highlights;
import com.efunhub.pickpriceadmin.R;

import java.util.ArrayList;

/**
 * Created by Admin on 27-12-2018.
 */

public class HighLightsAdapter extends RecyclerView.Adapter<HighLightsAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Highlights> highlightsArrayList = new ArrayList<>();
    private String productId;

    private Highlights highlightsModel;

    public HighLightsAdapter(Context mContext, ArrayList<Highlights> highlightsArrayList) {
        this.mContext = mContext;
        this.highlightsArrayList = highlightsArrayList;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_highlights_item_list, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        highlightsModel = highlightsArrayList.get(position);

        holder.tv_highLights.setText(highlightsModel.getHighlights());

    }

    @Override
    public int getItemCount() {
        return highlightsArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_highLights;
        ImageView ivDelete;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_highLights = itemView.findViewById(R.id.tv_highLights);


            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    *//*if(pos != RecyclerView.NO_POSITION)
                    {
                        Offers clickedDataItem = offersArrayList.get(pos);
                        Intent intent = new Intent(mContext, ProductListActivity.class);
                        intent.putExtra("category_id",clickedDataItem.offers_id);
                        mContext.startActivity(intent);

                    }*//*

                }
            });*/
        }
    }
}
