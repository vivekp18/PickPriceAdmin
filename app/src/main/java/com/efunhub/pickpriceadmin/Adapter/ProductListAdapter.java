package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Activity.ProductDetailsActivity;
import com.efunhub.pickpriceadmin.Model.ProductModel;
import com.efunhub.pickpriceadmin.Model.TabCategoryModel;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.PicassoTrustAll;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<ProductModel> productModelArrayList;
    private ArrayList<TabCategoryModel> tabCategoryModels;



    public ProductListAdapter(Context mContext, ArrayList<TabCategoryModel> tabCategoryModels) {
        this.mContext = mContext;
        this.tabCategoryModels = tabCategoryModels;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, null);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        final TabCategoryModel categoryModel = tabCategoryModels.get(position);

        //ProductModel  productModel = productModelArrayList.get(position);

        holder.tvProductName.setText( categoryModel.getProductName());

        holder.tvProductPrice.setText(mContext.getResources().getString(R.string.currency) +" "+categoryModel.getProductPrice());

        if (!categoryModel.getProductImageUrl().equalsIgnoreCase("")
                || !categoryModel.getProductImageUrl().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(categoryModel.getProductImageUrl())
                        .placeholder(R.drawable.ic_electronics)
                        .into(holder.ivProductImage);


            }catch(Exception ex){

                Log.e("CategoryListAdapter", "onBindViewHolder: ",ex );
            }

        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_electronics);
        }

    }

    @Override
    public int getItemCount() {
        return tabCategoryModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;

        ItemViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TabCategoryModel categoryModel = tabCategoryModels.get(getAdapterPosition());

                    Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                    intent.putExtra("DealerId",categoryModel.getDealer_Id());
                    intent.putExtra("ProductId",categoryModel.getProductId());
                    intent.putExtra("ProductImage",categoryModel.getProductImageUrl());
                    intent.putExtra("ProductStatus",categoryModel.getStatus());
                    mContext.startActivity(intent);

                    //Toast.makeText(mContext,"Postion:"+categoryModel.getProductName(),Toast.LENGTH_LONG).show();


                }
            });

        }



    }
}
