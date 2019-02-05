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
import com.efunhub.pickpriceadmin.Adapter.DealerListAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.Dealer;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.RETRIVE_DEALERS;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.UPDATE_DEALER_STATUS;

public class DealerListActivity extends AppCompatActivity {//implements SearchView.OnQueryTextListener
    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvCustomerList;
    private DealerListAdapter dealerListActivity;
    private TextView tvDealerAvailable;

    private ArrayList<Dealer> dealerArrayList;
    private Dealer dealerModel;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String RETRIVEDEALERS="show_dealers.php";
    private String UPDATE_STATUS="update_dealer_status.php";
    public static DealerListActivity dealerListActivityCntx;
    private int dealerId;
    private Menu mMenu;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_list);

        init();

        setupToolbar();

        if (connectivityStatus) {

            retriveAllDealers();

        } else {
            Toast.makeText(DealerListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Dealer");
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
        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
        rvCustomerList = findViewById(R.id.rvDealerList);
        tvDealerAvailable = findViewById(R.id.tv_dealersAvailable);
        dealerArrayList = new ArrayList<>();

        dealerListActivityCntx = this;

        if(getIntent().hasExtra("productDealerId")){
            dealerId = Integer.parseInt(getIntent().getStringExtra("productDealerId"));

        }else if(getIntent().hasExtra("dealerId")){
            dealerId =Integer.parseInt(getIntent().getStringExtra("dealerId"));
        }

        toastClass = new ToastClass();

        sessionManager = new SessionManager(getApplicationContext());

        progressDialog = ProgressDialog.show(DealerListActivity.this, "Pick Price",
                "Please wait while loading dealers..", false, true);
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
                dealerListActivity.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                dealerListActivity.getFilter().filter(query);
                return false;
            }
        });


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


    private void retriveAllDealers() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, DealerListActivity.this);

        mVollyService.postDataVolley(RETRIVE_DEALERS,
                this.getResources().getString(R.string.base_url) + RETRIVEDEALERS);
    }

    public void updateStatus(String status,String Dealer_Id){

        if (connectivityStatus) {

            progressDialog = ProgressDialog.show(DealerListActivity.this, "Pick Price",
                    "Please wait while updating status..", false, true);

            initVolleyCallback();

            mVollyService = new VolleyService(mResultCallback, DealerListActivity.this);

            HashMap<String, String> params = new HashMap<>();

            params.put("dealer_id", Dealer_Id);
            params.put("status", status);

            mVollyService.postDataVolleyParameters(UPDATE_DEALER_STATUS,
                    this.getResources().getString(R.string.base_url) + UPDATE_STATUS,params);
        }else{
            Toast.makeText(DealerListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETRIVE_DEALERS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    if (jsonObject.has("alldealers")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("alldealers");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            dealerModel = new Dealer();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("dealer_id")){
                                                dealerModel.setDealerId(jsonObj.getString("dealer_id"));
                                            }else{
                                                dealerModel.setDealerId("Not available");
                                            }


                                            if(jsonObj.has("name")){
                                                dealerModel.setDealerName(jsonObj.getString("name"));
                                            }else{
                                                dealerModel.setDealerName("Not available");
                                            }

                                            if(jsonObj.has("contact")){
                                                dealerModel.setDealerContact(jsonObj.getString("contact"));
                                            }else{
                                                dealerModel.setDealerContact("Not available");
                                            }


                                            if(jsonObj.has("email")){
                                                dealerModel.setDealerEmail(jsonObj.getString("email"));
                                            }else{
                                                dealerModel.setDealerEmail("Not available");
                                            }


                                            if(jsonObj.has("profileimage")){
                                                dealerModel.setDealerProfileImage(jsonObj.getString("profileimage"));
                                            }else{
                                                dealerModel.setDealerProfileImage("");
                                            }


                                            if(jsonObj.has("country")){
                                                dealerModel.setDealerCountry(jsonObj.getString("country"));
                                            }else{
                                                dealerModel.setDealerCountry("Not available");
                                            }


                                            if(jsonObj.has("state")){
                                                dealerModel.setDealerState(jsonObj.getString("state"));
                                            }else{
                                                dealerModel.setDealerState("Not available");
                                            }


                                            if(jsonObj.has("city")){
                                                dealerModel.setDealerCity(jsonObj.getString("city"));
                                            }else{
                                                dealerModel.setDealerCity("Not available");
                                            }


                                            if(jsonObj.has("area")){
                                                dealerModel.setDealerArea(jsonObj.getString("area"));
                                            }else{
                                                dealerModel.setDealerArea("Not available");
                                            }

                                            if(jsonObj.has("address")){
                                                dealerModel.setDealerAddress(jsonObj.getString("address"));
                                            }else{
                                                dealerModel.setDealerAddress("Not available");
                                            }

                                            if(jsonObj.has("pincode")){
                                                dealerModel.setDealerPincode(jsonObj.getString("pincode"));
                                            }else{
                                                dealerModel.setDealerPincode("Not available");
                                            }

                                            if(jsonObj.has("status")){
                                                dealerModel.setDealerStatus(jsonObj.getString("status"));
                                            }else{
                                                dealerModel.setDealerStatus("Not available");
                                            }

                                            if(jsonObj.has("created_at")){
                                                dealerModel.setDealerCreatedAt(jsonObj.getString("created_at"));
                                            }else{
                                                dealerModel.setDealerCreatedAt("");
                                            }

                                            dealerArrayList.add(dealerModel);

                                        }

                                        initDealerList();
                                    }


                                } else {
                                    progressDialog.cancel();
                                    tvDealerAvailable.setVisibility(View.VISIBLE);
                                    if(jsonObject.has("msg")){
                                        tvDealerAvailable.setText(jsonObject.getString("msg"));
                                    }else{
                                        tvDealerAvailable.setText("No dealers available");
                                    }
                                    //toastClass.makeToast(getApplicationContext(), jsonObject.getString("msg"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case UPDATE_DEALER_STATUS:

                            try {
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

                                    retriveAllDealers();
                                }else if(status == 2){
                                    progressDialog.cancel();

                                    toastClass.makeToast(getApplicationContext(),
                                            jsonObject.getString("Please Check Dealer ID."));
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                    }

                }

                @Override
                public void notifyError(int requestId, VolleyError error) {
                    progressDialog.cancel();
                    Log.v("Volley requestid ", String.valueOf(requestId));
                    Log.v("Volley Error", String.valueOf(error));
                }
            };
        }catch (Exception ex){
            progressDialog.cancel();
            Log.d("DealerListActivity", "initVolleyCallback: " +ex);
        }

    }


    public void initDealerList(){

        dealerListActivity = new DealerListAdapter(this, dealerArrayList);
        rvCustomerList.setHasFixedSize(true);
        rvCustomerList.setNestedScrollingEnabled(false);
        rvCustomerList.setLayoutManager(new GridLayoutManager(this, 1));
        rvCustomerList.setItemAnimator(new DefaultItemAnimator());
        rvCustomerList.setAdapter(dealerListActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        dealerListActivityCntx = this;

        //Check connectivity
        checkConnectivity = new CheckConnectivity(DealerListActivity.this, new NoInternetListener() {
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
        DealerListActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    public int getDealerID(){

        return dealerId;
    }
}
