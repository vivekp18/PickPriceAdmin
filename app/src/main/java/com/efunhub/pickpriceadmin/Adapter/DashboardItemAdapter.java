package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Activity.BrandListActivity;
import com.efunhub.pickpriceadmin.Activity.CategoriesListActivity;
import com.efunhub.pickpriceadmin.Activity.CustomersListActivity;
import com.efunhub.pickpriceadmin.Activity.DealerListActivity;
import com.efunhub.pickpriceadmin.Model.DashboardItemModel;
import com.efunhub.pickpriceadmin.R;

import java.util.ArrayList;

/**
 * Created by STARKIO on 17-Sep-18.
 */

public class DashboardItemAdapter extends RecyclerView.Adapter<DashboardItemAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<DashboardItemModel> arrayList;

    public DashboardItemAdapter(Context mContext, ArrayList<DashboardItemModel> dashboardItemModelArrayList) {
        this.mContext = mContext;
        this.arrayList = dashboardItemModelArrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dasboard_list_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        DashboardItemModel dashboardItemModel = arrayList.get(position);

        holder.ivCategoryImage.setBackgroundResource(dashboardItemModel.getCatImage());
        holder.tvCategoryName.setText(dashboardItemModel.getCatName());
        holder.tvCategoryCount.setText(dashboardItemModel.getCatCount());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCategoryImage;
        private TextView tvCategoryName, tvCategoryCount;

        ItemViewHolder(View itemView) {
            super(itemView);

            ivCategoryImage = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryCount = itemView.findViewById(R.id.tvCategoryCount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (getAdapterPosition()) {
                        case 0:

                            Intent intent = new Intent(mContext,DealerListActivity.class);
                            intent.putExtra("dealerId","1");
                            mContext.startActivity(intent);
                            break;

                        case 1:
                            mContext.startActivity(new Intent(mContext, CustomersListActivity.class));
                            break;

                        case 2:
                            mContext.startActivity(new Intent(mContext, CategoriesListActivity.class));
                            break;

                        case 3:
                            mContext.startActivity(new Intent(mContext, BrandListActivity.class));
                            break;
                        case 4:
                            Intent i = new Intent(mContext,DealerListActivity.class);
                            i.putExtra("productDealerId","2");
                            mContext.startActivity(i);
                            break;

                        case 5:

                            break;

                        case 6:
                            break;

                        case 7:
                            break;
                    }
                }
            });
        }
    }
}
