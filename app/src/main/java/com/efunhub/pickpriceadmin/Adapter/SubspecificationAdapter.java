package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Model.ProductSubSpecification;
import com.efunhub.pickpriceadmin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 21-12-2018.
 */

public class SubspecificationAdapter extends RecyclerView.Adapter<SubspecificationAdapter.ItemViewHolder> {

    private Context mContext;

    private ArrayList<ProductSubSpecification> productSubSpecificationDataArrayList = new ArrayList<>();

    private HashMap<String,List<HashMap<String,String>>> productSubSpecificationArrayList = new HashMap<>();

    HashMap<String,String> subSpecification = new HashMap<>();

    Object[] subSpecificationkeys;

    public SubspecificationAdapter(Context mContext, HashMap<String,String> subSpecification,
                                   ArrayList<ProductSubSpecification> productSubSpecificationDataArrayList)
    {
        this.mContext = mContext;
        this.subSpecification = subSpecification;
        this.productSubSpecificationDataArrayList =productSubSpecificationDataArrayList;
        subSpecificationkeys = subSpecification.keySet().toArray();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subspecification_item_list, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder,final int position) {

        String productSpecificationKey = String.valueOf(subSpecificationkeys[position]);

        String productSpecificationValue = subSpecification.get(productSpecificationKey);

        holder.tv_ProductSubSpecificationKey.setText(productSpecificationKey);

        holder.tv_ProductSubSpecificationValue.setText(productSpecificationValue);


    }

    @Override
    public int getItemCount() {
        return subSpecification.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_ProductMainSpecification;
        TextView tv_ProductSubSpecificationValue;
        TextView tv_ProductSubSpecificationKey;
        ImageView ivDelete;
        ListView listView;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_ProductMainSpecification = itemView.findViewById(R.id.tv_MainSpecificationName);

            tv_ProductSubSpecificationValue = itemView.findViewById(R.id.tv_SubSpecificationValue);

            tv_ProductSubSpecificationKey = itemView.findViewById(R.id.tv_SubSpecificationKey);

        }
    }


}
