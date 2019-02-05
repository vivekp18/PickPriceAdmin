package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Activity.CategoriesListActivity;
import com.efunhub.pickpriceadmin.Model.Category;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.PicassoTrustAll;

import java.util.ArrayList;

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Category> arrayList = new ArrayList<>();

    private Category categoryModel;

    public CategoriesListAdapter(Context mContext, ArrayList<Category> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        categoryModel = arrayList.get(position);


        holder.tv_categoryName.setText(categoryModel.getCategoryName());

        if (!categoryModel.getCategoryImage().equalsIgnoreCase("")
                || !categoryModel.getCategoryImage().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(categoryModel.getCategoryImage())
                        .into(holder.iv_categoryIcon);


                 /*Picasso.with(mContext)
                        .load(todaysVisitorModel.getTodayVImage())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .resize(120,120)
                        .centerCrop()
                        .into(holder.iv_VisitorPic);*/


            }catch(Exception ex){

                Log.e("CategoryListAdapter", "onBindViewHolder: ",ex );
            }


        } else {
            holder.iv_categoryIcon.setImageResource(R.drawable.ic_electronics);
        }

        holder.iv_deleteCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id

                Category category = arrayList.get(position);

                deleteDialog(category);

            }
        });

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

        ImageView iv_categoryIcon;
        TextView tv_categoryName;
        ImageView iv_deleteCategory;

        ItemViewHolder(View itemView) {
            super(itemView);

            iv_categoryIcon = itemView.findViewById(R.id.ivBrandIcon);

            tv_categoryName = itemView.findViewById(R.id.tv_categories_name);

            iv_deleteCategory = itemView.findViewById(R.id.ic_delete_category);
        }
    }

    public void  deleteDialog(final Category category){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        CategoriesListActivity.categoriesListActivityCntx.deleteCategories(category.getCategoryId());
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
