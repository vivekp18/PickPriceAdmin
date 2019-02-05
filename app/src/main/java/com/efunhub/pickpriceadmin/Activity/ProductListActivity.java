package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.Adapter.CategoryViewPagerAdapter;
import com.efunhub.pickpriceadmin.Adapter.ProductListAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Fragment.TabFragment;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.FragmentCategoryModel;
import com.efunhub.pickpriceadmin.Model.ProductModel;
import com.efunhub.pickpriceadmin.Model.TabCategoryModel;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.TABS_CATEGORY;

public class ProductListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvProductList;
    private ProductListAdapter productListAdapter;
    private TextView tvProductsAvailable;

    private ArrayList<ProductModel> productsArrayList;
    private ProductModel productModel;

    private String dealerId;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CategoryViewPagerAdapter categoryViewPagerAdapter;

    // Fragment List
    private ArrayList<FragmentCategoryModel> fragmentCategoryModelArrayList = new ArrayList<>();
    private ArrayList<TabCategoryModel> tabCategoryModelArrayList = new ArrayList<>();
    private ArrayList<ArrayList<TabCategoryModel>> arrayList = new ArrayList<>();
    // Title List
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String RETRIVEDEALERPRODUCTS="show_products_admin.php";
    private String UPDATE_STATUS="update_dealer_status.php";
    public static ProductListActivity productListActivityCntx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        init();

        setupToolbar();

        if(connectivityStatus){

            progressDialog = ProgressDialog.show(ProductListActivity.this, "Pick Price",
                    "Please Wait", false, true);

            retriveSingleDealerAllProducts();
        }else{
            toastClass.makeToast(getApplicationContext(),"Please check internet connection.");
        }

        // Tab ViewPager setting
        //viewPager.setOffscreenPageLimit(mFragmentList.size());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(categoryViewPagerAdapter);

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Products");
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

        productListActivityCntx = this;

        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
       // rvProductList = findViewById(R.id.rvProductList);


        tvProductsAvailable = findViewById(R.id.tvNoProducts);

        productsArrayList = new ArrayList<>();

        if(getIntent().hasExtra("DealerId")){
            dealerId =getIntent().getStringExtra("DealerId") ;
        }

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        toastClass = new ToastClass();

        sessionManager = new SessionManager(getApplicationContext());
    }

    public void setupViewPager(ViewPager viewPager) {
        categoryViewPagerAdapter = new CategoryViewPagerAdapter(getSupportFragmentManager(), fragmentCategoryModelArrayList, mFragmentTitleList);
        viewPager.setAdapter(categoryViewPagerAdapter);

        viewPager.setCurrentItem(getIntent().getExtras().getInt("position"));
    }

    private void retriveSingleDealerAllProducts() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductListActivity.this);

        HashMap<String,String> hashMapParams = new HashMap<>();

        hashMapParams.put("dealer_id",dealerId);

        mVollyService.postDataVolleyParameters(TABS_CATEGORY,
                this.getResources().getString(R.string.base_url) + RETRIVEDEALERPRODUCTS,hashMapParams);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case TABS_CATEGORY:
                            try {
                                JSONObject jsonObjData = new JSONObject(response);

                                int status = jsonObjData.getInt("status");

                                String Cname, productname, productdesc, productprice,
                                        productimageurl, productid,dealerID,shopname,productStatus;
                                TabCategoryModel tabCategoryModel = null;
                                FragmentCategoryModel fragmentCategoryModel = null;

                                if (status == 0) {

                                    progressDialog.cancel();
                                    tvProductsAvailable.setVisibility(View.VISIBLE);
                                    //toastClass.makeToast(getApplicationContext(), "no category found.");

                                } else if(status == 1){
                                    progressDialog.cancel();

                                    tvProductsAvailable.setVisibility(View.GONE);
                                    viewPager.setVisibility(View.VISIBLE);
                                    tabLayout.setVisibility(View.VISIBLE);

                                    JSONArray allmenus = jsonObjData.getJSONArray("allproduct");

                                    for (int i = 0; i < allmenus.length(); i++) {

                                        tabCategoryModelArrayList.clear();

                                        JSONObject jsonObject = allmenus.getJSONObject(i);

                                        Cname = jsonObject.getString("cname");

                                        mFragmentTitleList.add(Cname);

                                        JSONArray menusJsonArray = jsonObject.getJSONArray("product");

                                        for (int j = 0; j < menusJsonArray.length(); j++) {

                                            JSONObject jsonObjectMenu = menusJsonArray.getJSONObject(j);
                                            productname = jsonObjectMenu.getString("product_name");
                                            productdesc = jsonObjectMenu.getString("description");
                                            productprice = jsonObjectMenu.getString("price");
                                            productimageurl = jsonObjectMenu.getString("image");
                                            productid = jsonObjectMenu.getString("product_id");
                                            dealerID = "DEALID101";
                                            shopname = jsonObjectMenu.getString("shopname");
                                            productStatus = jsonObjectMenu.getString("status");

                                            tabCategoryModel = new TabCategoryModel();
                                            tabCategoryModel.setProductId(productid);
                                            tabCategoryModel.setProductName(productname);
                                            tabCategoryModel.setProductDesc(productdesc);
                                            tabCategoryModel.setProductPrice(productprice);
                                            tabCategoryModel.setProductImageUrl(productimageurl);
                                            tabCategoryModel.setDealerShopName(shopname);
                                            tabCategoryModel.setDealer_Id(dealerID);
                                            tabCategoryModel.setStatus(productStatus);
                                            tabCategoryModel.setProductQty("1");
                                            tabCategoryModelArrayList.add(tabCategoryModel);
                                        }

                                        arrayList.add(new ArrayList<TabCategoryModel>(tabCategoryModelArrayList));

                                        fragmentCategoryModel = new FragmentCategoryModel();
                                        fragmentCategoryModel.setFragment(new TabFragment());
                                        fragmentCategoryModel.setArrayList(arrayList.get(i));
                                        fragmentCategoryModelArrayList.add(fragmentCategoryModel);
                                    }

                                    setupViewPager(viewPager);


                                }else if(status == 2){
                                    progressDialog.cancel();
                                    tvProductsAvailable.setVisibility(View.VISIBLE);
                                    viewPager.setVisibility(View.GONE);
                                    tabLayout.setVisibility(View.GONE);

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
            Log.d("LoginActivity", "initVolleyCallback: " +ex);
        }

    }




    @Override
    public void onResume() {
        super.onResume();

       productListActivityCntx = this;

        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProductListActivity.this, new NoInternetListener() {
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
        ProductListActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    public int getDealerId(){

        return  Integer.parseInt(dealerId);
    }


}
