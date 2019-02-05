package com.efunhub.pickpriceadmin.Adapter;

import android.content.Context;
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

import com.efunhub.pickpriceadmin.Activity.CustomersListActivity;
import com.efunhub.pickpriceadmin.Model.Customer;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.PicassoTrustAll;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomersListAdapter extends RecyclerView.Adapter<CustomersListAdapter.ItemViewHolder>
        implements Filterable {

    private Context mContext;
    private ArrayList<Customer> customerArrayList = new ArrayList<>();
    private Customer customerModel;
    private boolean isSpinnerTouched = false;
    private List<Customer> customerListFiltered;

    public CustomersListAdapter(Context mContext, ArrayList<Customer> customerArrayList) {
        this.mContext = mContext;
        this.customerArrayList = customerArrayList;
        this.customerListFiltered = customerArrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        customerModel = customerArrayList.get(position);

        holder.tv_customerName.setText(customerModel.getCustomerName());


        if(!customerModel.getCustomerCreatedAt().isEmpty()||
                !customerModel.getCustomerCreatedAt().equalsIgnoreCase("")){


            String createdAt[] = customerModel.getCustomerCreatedAt().trim().split("\\s+");

            holder.tv_customerCreatedAt.setText(createdAt[0]);
        }else{
            holder.tv_customerCreatedAt.setText("Not available");
        }

        holder.tv_customerEmail.setText(customerModel.getCustomerEmail());
        holder.tv_customerContact.setText(customerModel.getCustomerContact());
        //holder.tv_customerArea.setText(customerModel.getCustomerArea()+","+customerModel.getCustomerAddress()+","+customerModel.getCustomerCity());
        //holder.tv_customerAddress.setText(customerModel.getCustomerState()+","+customerModel.getCustomerCountry()+","+customerModel.getCustomerPincode());
        holder.tv_customerFullName.setText(customerModel.getCustomerName());
        holder.tv_customerContatctNumber.setText(customerModel.getCustomerContact());

        if(customerModel.getCustomerStatus().equalsIgnoreCase("Unblock")){
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
                            CustomersListActivity.customersListActivity.updateStatus(selectedItem,customerModel.getCustomerId());

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


        if (!customerModel.getCustomerProfileImage().equalsIgnoreCase("")
                || !customerModel.getCustomerProfileImage().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(customerModel.getCustomerProfileImage())
                        .into(holder.ivProfilePicture);

                PicassoTrustAll.getInstance(mContext)
                        .load(customerModel.getCustomerProfileImage())
                        .into(holder.ivProfilePic);
            }catch(Exception ex){

                Log.e("CustomerListAdapter", "onBindViewHolder: ",ex );
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
        return customerListFiltered.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private FoldingCell fc;
        private ImageView ivProfilePic;
        private TextView tv_customerName;
        private TextView tv_customerCreatedAt;
        private TextView tv_customerEmail;
        private TextView tv_customerContact;
        private TextView tv_customerArea;
        private TextView tv_customerAddress;
        private CircleImageView ivProfilePicture;
        private TextView tv_customerFullName;
        private TextView tv_customerContatctNumber;
        private Spinner sp_status;


        ItemViewHolder(View itemView) {
            super(itemView);

            fc = itemView.findViewById(R.id.folding_cell);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tv_customerName = itemView.findViewById(R.id.tv_dealerName);
            tv_customerCreatedAt = itemView.findViewById(R.id.tv_dealerCreatedAt);
            tv_customerEmail = itemView.findViewById(R.id.tv_dealerEmail);
            tv_customerContact = itemView.findViewById(R.id.tv_dealerContact);
            ivProfilePicture = itemView.findViewById(R.id.iv_dealerProfilePic);
            tv_customerFullName = itemView.findViewById(R.id.tv_dealerFullName);
            tv_customerContatctNumber = itemView.findViewById(R.id.tv_dealerContatctNumber);

            sp_status = itemView.findViewById(R.id.spn_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fc.toggle(false);
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    customerListFiltered = customerArrayList;
                } else {
                    List<Customer> filteredList = new ArrayList<>();
                    for (Customer row : customerArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCustomerArea().toLowerCase().contains(charString.toLowerCase())
                                || row.getCustomerCity().contains(charSequence)
                                || row.getCustomerState().contains(charSequence)
                                || row.getCustomerAddress().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    customerListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = customerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                customerListFiltered = (ArrayList<Customer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
