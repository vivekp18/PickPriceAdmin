package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.Adapter.DashboardItemAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.DashboardItemModel;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.ADMIN_LOGIN;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvDashboardItem;
    private DashboardItemAdapter dashboardItemAdapter;
    private ArrayList<DashboardItemModel> dashboardItemModelArrayList;
    private String[] catName;
    private String[] catCount = new String[5];
    private int[] catImage;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String DashBoardURL="admin_dashboard.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setupToolbar();


        if (connectivityStatus) {
            adminDashBoard();

        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting:
                startActivity(new Intent(MainActivity.this, SettingAccountActivity.class));
                break;

            case R.id.item_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void adminDashBoard() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, MainActivity.this);
        HashMap<String, String> params = new HashMap<>();
        mVollyService.postDataVolley(ADMIN_LOGIN,
                this.getResources().getString(R.string.base_url) + DashBoardURL);
    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            progressDialog.cancel();
                            if (jsonObject.has("dealer_count")) {

                                String dealerCount = jsonObject.getString("dealer_count");

                                catCount[0]=dealerCount;

                            }
                            if (jsonObject.has("customer_count")) {

                                String customerCount = jsonObject.getString("customer_count");
                                catCount[1]=customerCount;

                            }
                            if (jsonObject.has("category_count")) {

                                String categoryCount = jsonObject.getString("category_count");
                                catCount[2]=categoryCount;

                            }if (jsonObject.has("brand_count")) {

                                String brandCount = jsonObject.getString("brand_count");
                                catCount[3]=brandCount;

                            }if (jsonObject.has("product_count")) {

                                String productCount = jsonObject.getString("product_count");
                                catCount[4]=productCount;

                            }

                            initDashBorad();
                        } else {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), "Unable to load data");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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

    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(MainActivity.this, new NoInternetListener() {
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
        MainActivity.this.registerReceiver(checkConnectivity, intentFilter);

        adminDashBoard();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    //init dashboard of admin
    public void initDashBorad(){


        if(!dashboardItemModelArrayList.isEmpty()){
            dashboardItemModelArrayList.clear();
        }

        catImage = new int[]{
                R.drawable.ic_dealer,
                R.drawable.ic_customer,
                R.drawable.ic_category,
                R.drawable.ic_brand,
                R.drawable.ic_product
        };// ,

        catName = new String[]{
                "Dealers",
                "Customers",
                "Categories",
                "Brands",
                "Products"
        };// ,

        for (int i = 0; i < catName.length; i++) {
            DashboardItemModel dashboardItemModel = new DashboardItemModel();
            dashboardItemModel.setCatImage(catImage[i]);
            dashboardItemModel.setCatCount(catCount[i]);
            dashboardItemModel.setCatName(catName[i]);
            dashboardItemModelArrayList.add(dashboardItemModel);
        }

        dashboardItemAdapter = new DashboardItemAdapter(this, dashboardItemModelArrayList);
        rvDashboardItem.setHasFixedSize(true);
        rvDashboardItem.setNestedScrollingEnabled(false);
        rvDashboardItem.setLayoutManager(new GridLayoutManager(this, 2));
        rvDashboardItem.setItemAnimator(new DefaultItemAnimator());
        rvDashboardItem.setAdapter(dashboardItemAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("P i c k P r i c e");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void init() {
        mToolbar = findViewById(R.id.toolbar_main);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbarMain);
        rvDashboardItem = findViewById(R.id.rvDashboardItem);
        dashboardItemModelArrayList = new ArrayList<>();
        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = ProgressDialog.show(MainActivity.this, "Pick Price",
                "Please wait while loading..", false, true);
    }


    @Override
    public void onBackPressed() {
        appExitDialog();
    }

    public void  appExitDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you want to Exit?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       finishAffinity();
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


    //catCount = new String[5];
}
