package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.efunhub.pickpriceadmin.Activity.DealerListActivity;
import com.efunhub.pickpriceadmin.Activity.ProductListActivity;
import com.efunhub.pickpriceadmin.Model.Dealer;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.PicassoTrustAll;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 07-12-2018.
 */

public class DealerListAdapter extends RecyclerView.Adapter<DealerListAdapter.ItemViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<Dealer> arrayList = new ArrayList<>();
    Dealer dealerItemModel;
    private boolean isSpinnerTouched = false;

    private List<Dealer> dealerList;
    private List<Dealer> dealerListFiltered;

    public DealerListAdapter(Context mContext, ArrayList<Dealer> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.dealerListFiltered = arrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dealer_list_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        dealerItemModel = dealerListFiltered.get(position);

        holder.tv_dealerName.setText(dealerItemModel.getDealerName());


        if(!dealerItemModel.getDealerCreatedAt().isEmpty()|| !dealerItemModel.getDealerCreatedAt().equalsIgnoreCase("")){


            String createdAt[] = dealerItemModel.getDealerCreatedAt().trim().split("\\s+");

            holder.tv_dealerCreatedAt.setText(createdAt[0]);
        }else{
            holder.tv_dealerCreatedAt.setText("Not available");
        }

        holder.tv_dealerEmail.setText(dealerItemModel.getDealerEmail());
        holder.tv_dealerContact.setText(dealerItemModel.getDealerContact());
        holder.tv_dealerArea.setText(dealerItemModel.getDealerArea()+","+dealerItemModel.getDealerAddress()+","+dealerItemModel.getDealerCity());
        holder.tv_dealerAddress.setText(dealerItemModel.getDealerState()+","+dealerItemModel.getDealerCountry()+","+dealerItemModel.getDealerPincode());
        holder.tv_dealerFullName.setText(dealerItemModel.getDealerName());
        holder.tv_dealerContatctNumber.setText(dealerItemModel.getDealerContact());

        if(DealerListActivity.dealerListActivityCntx.getDealerID() == 1){

            holder.sp_status.setVisibility(View.VISIBLE);

            if(dealerItemModel.getDealerStatus().equalsIgnoreCase("Unblock")){
                holder.sp_status.setSelection(1);
            }else{
                holder.sp_status.setSelection(0);
            }

            holder.sp_status.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isSpinnerTouched = true;

                    holder.sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            if (isSpinnerTouched) {
                                String selectedItem = parent.getItemAtPosition(position).toString();
                                DealerListActivity.dealerListActivityCntx.updateStatus(selectedItem,dealerItemModel.getDealerId());

                            }

                        }
                        //to close the onItemSelected
                        public void onNothingSelected(AdapterView<?> parent)
                        {

                        }
                    });
                    return false;
                }
            });
        }else{
            holder.sp_status.setVisibility(View.GONE);
        }




        if (!dealerItemModel.getDealerProfileImage().equalsIgnoreCase("")
                || !dealerItemModel.getDealerProfileImage().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(dealerItemModel.getDealerProfileImage())
                        .into(holder.ivProfilePicture);

                PicassoTrustAll.getInstance(mContext)
                        .load(dealerItemModel.getDealerProfileImage())
                        .into(holder.ivProfilePic);


            }catch(Exception ex){

                Log.e("TodaysVistorAdapter", "onBindViewHolder: ",ex );
            }


        } else {
            holder.ivProfilePic.setImageResource(R.drawable.placeholder);
            holder.ivProfilePicture.setImageResource(R.drawable.placeholder);
        }


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return dealerListFiltered.size();
    }


    public void filterList(ArrayList<Dealer> filteredList) {
        this.arrayList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    dealerListFiltered = arrayList;

                } else {
                    List<Dealer> filteredList = new ArrayList<>();

                    for (Dealer row : arrayList) {

                        // here we are looking for city or area or address match
                        if (row.getDealerCity().toLowerCase().contains(charString.toLowerCase())
                                || row.getDealerArea().contains(charSequence)
                                || row.getDealerAddress().contains(charSequence)
                                || row.getDealerState().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    dealerListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dealerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dealerListFiltered = (ArrayList<Dealer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private FoldingCell fc;

        private ImageView ivProfilePic;
        //private CircularImageView ivProfilePic;
        private TextView tv_dealerName;
        private TextView tv_dealerCreatedAt;
        private TextView tv_dealerEmail;
        private TextView tv_dealerContact;
        private TextView tv_dealerArea;
        private TextView tv_dealerAddress;
        private CircleImageView ivProfilePicture;
        private TextView tv_dealerFullName;
        private TextView tv_dealerContatctNumber;
        private Spinner sp_status;


        ItemViewHolder(View itemView) {
            super(itemView);

            fc = itemView.findViewById(R.id.folding_cell);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tv_dealerName = itemView.findViewById(R.id.tv_dealerName);
            tv_dealerCreatedAt = itemView.findViewById(R.id.tv_dealerCreatedAt);
            tv_dealerEmail = itemView.findViewById(R.id.tv_dealerEmail);
            tv_dealerContact = itemView.findViewById(R.id.tv_dealerContact);
            tv_dealerArea = itemView.findViewById(R.id.tv_dealerArea);
            tv_dealerAddress = itemView.findViewById(R.id.tv_dealerAddress);
            ivProfilePicture = itemView.findViewById(R.id.iv_dealerProfilePic);
            tv_dealerFullName = itemView.findViewById(R.id.tv_dealerFullName);
            tv_dealerContatctNumber = itemView.findViewById(R.id.tv_dealerContatctNumber);

            sp_status = itemView.findViewById(R.id.spn_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(DealerListActivity.dealerListActivityCntx.getDealerID() == 1){
                        fc.toggle(false);
                    }else if (DealerListActivity.dealerListActivityCntx.getDealerID() == 2){
                        fc.fold(true);
                        Dealer dealer = dealerListFiltered.get(getAdapterPosition());
                        Intent intent = new Intent(mContext, ProductListActivity.class);
                        intent.putExtra("DealerId",dealer.getDealerId());
                        mContext.startActivity(intent);

                    }
                }
            });

        }
    }
}
