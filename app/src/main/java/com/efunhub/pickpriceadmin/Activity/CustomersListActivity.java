package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.Adapter.CustomersListAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.Customer;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.RETRIVE_CUSTOMERS;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.UPDATE_CUSTOMERS_STATUS;

public class CustomersListActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvCustomerList;
    private TextView tvCustomersAvailable;
    private CustomersListAdapter customersListAdapter;

    private ArrayList<Customer> customerArrayList;
    private Customer customerModel;
    private SearchView searchView;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String RETRIVECUSTOMERURL="show_customers.php";
    private String UPDATE_STATUS_URL="update_customer_status.php";
    public static CustomersListActivity customersListActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_list);

        init();
        setupToolbar();

        if (connectivityStatus) {
            retriveAllCustomers();

        } else {
            Toast.makeText(CustomersListActivity.this, "No Internet Connection",
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
       tvToolbar.setText("Customers");
        //mToolbar.setTitle(R.string.toolbar_contact_title);
       // mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        //getSupportActionBar().setTitle(R.string.toolbar_contact_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        customersListActivity = this;
        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
        rvCustomerList = findViewById(R.id.rvCustomerList);
        tvCustomersAvailable = findViewById(R.id.tv_cusAvailable);
        customerArrayList = new ArrayList<>();

        toastClass = new ToastClass();

        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = ProgressDialog.show(CustomersListActivity.this, "Pick Price",
                "Please wait while loading Customers..", false, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchMenu)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setMinimumHeight(Integer.MIN_VALUE);


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                customersListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                customersListAdapter.getFilter().filter(query);
                return false;
            }
        });

        /*this.mMenu = menu;

        MenuItem item = menu.findItem(R.id.searchMenu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setQueryHint("Search dealer");
        searchView.setOnQueryTextListener(this);*/

        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.searchMenu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void retriveAllCustomers() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, CustomersListActivity.this);

        mVollyService.postDataVolley(RETRIVE_CUSTOMERS,
                this.getResources().getString(R.string.base_url) + RETRIVECUSTOMERURL);
    }

    public void updateStatus(String status,String customer_Id){

        if (connectivityStatus) {
            progressDialog = ProgressDialog.show(CustomersListActivity.this, "Pick Price",
                    "Please wait while updating status..", false, true);
            initVolleyCallback();

            mVollyService = new VolleyService(mResultCallback, CustomersListActivity.this);

            HashMap<String, String> params = new HashMap<>();

            params.put("customer_id", customer_Id);
            params.put("status", status);

            mVollyService.postDataVolleyParameters(UPDATE_CUSTOMERS_STATUS,
                    this.getResources().getString(R.string.base_url) + UPDATE_STATUS_URL,params);
        }else{
            Toast.makeText(CustomersListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {

                    switch (requestId){

                        case RETRIVE_CUSTOMERS:

                            if(!customerArrayList.isEmpty()){
                                customerArrayList.clear();
                            }

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();


                            if (jsonObject.has("allcustomers")) {


                                JSONArray jsonArrayData = jsonObject.getJSONArray("allcustomers");

                                for (int i=0;i<jsonArrayData.length();i++){

                                    customerModel = new Customer();

                                    JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                    if(jsonObj.has("customer_id")){
                                        customerModel.setCustomerId(jsonObj.getString("customer_id"));
                                    }else{
                                        customerModel.setCustomerId("Not available");
                                    }


                                    if(jsonObj.has("name")){
                                        customerModel.setCustomerName(jsonObj.getString("name"));
                                    }else{
                                        customerModel.setCustomerName("Not available");
                                    }

                                    if(jsonObj.has("contact")){
                                        customerModel.setCustomerContact(jsonObj.getString("contact"));
                                    }else{
                                        customerModel.setCustomerContact("Not available");
                                    }


                                    if(jsonObj.has("email")){
                                        customerModel.setCustomerEmail(jsonObj.getString("email"));
                                    }else{
                                        customerModel.setCustomerEmail("Not available");
                                    }


                                    if(jsonObj.has("profileimage")){
                                        customerModel.setCustomerProfileImage(jsonObj.getString("profileimage"));
                                    }else{
                                        customerModel.setCustomerProfileImage("");
                                    }


                                    if(jsonObj.has("country")){
                                        customerModel.setCustomerCountry(jsonObj.getString("country"));
                                    }else{
                                        customerModel.setCustomerCountry("");
                                    }


                                    if(jsonObj.has("state")){
                                        customerModel.setCustomerState(jsonObj.getString("state"));
                                    }else{
                                        customerModel.setCustomerState("");
                                    }


                                    if(jsonObj.has("city")){
                                        customerModel.setCustomerCity(jsonObj.getString("city"));
                                    }else{
                                        customerModel.setCustomerCity("");
                                    }


                                    if(jsonObj.has("area")){
                                        customerModel.setCustomerArea(jsonObj.getString("area"));
                                    }else{
                                        customerModel.setCustomerArea("");
                                    }

                                    if(jsonObj.has("address")){
                                        customerModel.setCustomerAddress(jsonObj.getString("address"));
                                    }else{
                                        customerModel.setCustomerAddress("");
                                    }

                                    if(jsonObj.has("pincode")){
                                        customerModel.setCustomerPincode(jsonObj.getString("pincode"));
                                    }else{
                                        customerModel.setCustomerPincode("");
                                    }

                                    if(jsonObj.has("status")){
                                        customerModel.setCustomerStatus(jsonObj.getString("status"));
                                    }else{
                                        customerModel.setCustomerStatus("");
                                    }

                                    if(jsonObj.has("created_at")){
                                        customerModel.setCustomerCreatedAt(jsonObj.getString("created_at"));
                                    }else{
                                        customerModel.setCustomerCreatedAt("");
                                    }

                                    customerArrayList.add(customerModel);
                                }
                            }

                            initCustomersList();

                                } else {
                                    progressDialog.cancel();
                                    tvCustomersAvailable.setVisibility(View.VISIBLE);
                                    if(jsonObject.has("msg")){
                                        tvCustomersAvailable.setText(jsonObject.getString("msg"));
                                    }else{
                                        tvCustomersAvailable.setText("No customers available");
                                    }
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }

                            break;


                        case UPDATE_CUSTOMERS_STATUS:

                            try{
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), jsonObject.getString(
                                            "Something went wrong. Please try again"));
                                            } else if(status == 1){
                                                progressDialog.cancel();

                                                toastClass.makeToast(getApplicationContext(),
                                                        jsonObject.getString("Successfully Updated"));

                                                retriveAllCustomers();
                                            }else if(status == 2){
                                                progressDialog.cancel();

                                                toastClass.makeToast(getApplicationContext(),
                                                        jsonObject.getString("Please Check Customer ID."));
                                            }

                            }catch (Exception ex){
                                progressDialog.cancel();
                                Log.d("CustomerActivity", "initVolleyCallback: " +ex);
                            }
                            break;
                    }

                }

                @Override
                public void notifyError(int requestId, VolleyError error) {
                    Log.v("Volley requestid ", String.valueOf(requestId));
                    Log.v("Volley Error", String.valueOf(error));
                }
            };
        }catch (Exception ex){

            Log.d("LoginActivity", "initVolleyCallback: " +ex);
        }

    }


    public void initCustomersList(){
      /*  arrayList.add("Ashish");
        arrayList.add("Parth");*/

        customersListAdapter = new CustomersListAdapter(this, customerArrayList);
        rvCustomerList.setHasFixedSize(true);
        rvCustomerList.setNestedScrollingEnabled(false);
        rvCustomerList.setLayoutManager(new GridLayoutManager(this, 1));
        rvCustomerList.setItemAnimator(new DefaultItemAnimator());
        rvCustomerList.setAdapter(customersListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        customersListActivity = this;
        //Check connectivity
        checkConnectivity = new CheckConnectivity(CustomersListActivity.this, new NoInternetListener() {
            @Override
            public void availConnection(boolean connection) {
                if (connection) {
                    connectivityStatus = true;
                } else {
                    connectivityStatus = false;
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        CustomersListActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }


}
