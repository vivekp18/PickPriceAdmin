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

import com.efunhub.pickpriceadmin.Activity.BrandListActivity;
import com.efunhub.pickpriceadmin.Model.Brand;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.PicassoTrustAll;

import java.util.ArrayList;

public class BrandsListAdapter extends RecyclerView.Adapter<BrandsListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Brand> arrayList = new ArrayList<>();
    private Brand brandModel;

    public BrandsListAdapter(Context mContext, ArrayList<Brand> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brands_list_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder,final int position) {
        brandModel = arrayList.get(position);


        holder.tv_brandName.setText(brandModel.getBrand_name());

        if (!brandModel.getBimage().equalsIgnoreCase("")
                || !brandModel.getBimage().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(brandModel.getBimage())
                        .into(holder.iv_brandImage);




            }catch(Exception ex){

                Log.e("BrandListAdapterAdapter", "onBindViewHolder: ",ex );
            }


        } else {
            holder.iv_brandImage.setImageResource(R.drawable.ic_electronics);
        }

        holder.iv_deleteBrand.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id

                Brand brand = arrayList.get(position);

                deleteDialog(brand);


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

        private TextView tv_brandName;
        private ImageView iv_brandImage;
        private ImageView iv_deleteBrand;

        ItemViewHolder(View itemView) {
            super(itemView);
            tv_brandName = itemView.findViewById(R.id.tv_brandName);
            iv_brandImage = itemView.findViewById(R.id.ivBrandIcon);
            iv_deleteBrand = itemView.findViewById(R.id.iv_deleteBrand);

        }
    }

    public void  deleteDialog(final Brand brand){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Are you sure you want to delete?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        BrandListActivity.brandListActivityCntx.deleteBrand(brand.getBrand_id());
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
