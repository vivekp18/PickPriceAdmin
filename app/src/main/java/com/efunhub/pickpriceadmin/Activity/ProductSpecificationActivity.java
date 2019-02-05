package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.Adapter.ProductSpecificationAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.ProductMainSpecification;
import com.efunhub.pickpriceadmin.Model.ProductSubSpecification;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.RETREIVE_PRODUCT_SPECIFICATION;

/**
 * Created by Admin on 04-01-2019.
 */

public class ProductSpecificationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tv_SpecificationStatus;

    private String productId,dealerId;
    private LinearLayout parentSpecifictionLayout;
    private HashMap<String,String> subSpecificationHashMapData;
    private ArrayList<ProductMainSpecification> productMainSpecificationArrayList;
    private ArrayList<ProductSubSpecification>  productSubSpecificationArrayList;

    private ProductMainSpecification productMainSpecificationModel;
    private ProductSubSpecification productSubSpecificationModel;

    private RecyclerView rvProductSpecification;

    private ProductSpecificationAdapter productSpecificationAdapter;

    private HashMap<String,List<HashMap<String,String>>> subSpecificationDataHashMap;

    private ArrayList<String> mainSpecificationDataList;



    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String Retreive_Product_Specification_URL="show_specifications.php";
    private String Retreive_Product_Main_Specification_URL="show_main_specification.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_specification);

        init();

        setupToolbar();

        if(connectivityStatus){
            retreiveProductSpecifications();
        }else{
            toastClass.makeToast(getApplicationContext(),"Please check internet connection.");
        }

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbar.setText("Product Specification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {


        toolbar = findViewById(R.id.toolbar);

        tvToolbar = toolbar.findViewById(R.id.tvToolbar);

        rvProductSpecification = findViewById(R.id.rvProductSpecification);

        tv_SpecificationStatus = findViewById(R.id.tv_SpecificationStatus);

        subSpecificationDataHashMap = new HashMap<>();

        subSpecificationHashMapData = new HashMap<>();

        mainSpecificationDataList = new ArrayList<>();


        if(getIntent().hasExtra("ProductId")){
            productId = getIntent().getStringExtra("ProductId");
        }

        if(getIntent().hasExtra("DealerId")){
            dealerId = getIntent().getStringExtra("DealerId");
        }

        parentSpecifictionLayout = findViewById(R.id.parent_specification_layout);

        productMainSpecificationArrayList = new ArrayList<>();

        productSubSpecificationArrayList = new ArrayList<>();

        toastClass = new ToastClass();

        sessionManager = new SessionManager(this);

    }


    private void retreiveProductSpecifications(){

        progressDialog = ProgressDialog.show(ProductSpecificationActivity.this, "Pick Price",
                "Please Wait", false, true);

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductSpecificationActivity.this);



        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealerId);
        params.put("product_id",productId);

        mVollyService.postDataVolleyParameters(RETREIVE_PRODUCT_SPECIFICATION,
                this.getResources().getString(R.string.base_url) + Retreive_Product_Specification_URL,params);

    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETREIVE_PRODUCT_SPECIFICATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    if(jsonObject.has("allproductspecification")){

                                        if(!productMainSpecificationArrayList.isEmpty()){
                                            productMainSpecificationArrayList.clear();
                                        }
                                        JSONArray jsonArrayProductSpecificationdata = jsonObject.getJSONArray("allproductspecification");

                                        for (int i=0;i<jsonArrayProductSpecificationdata.length();i++){

                                            productMainSpecificationModel = new ProductMainSpecification();

                                            JSONObject jsonObjectMainSpecification= jsonArrayProductSpecificationdata.getJSONObject(i);

                                            if(jsonObjectMainSpecification.has("main_specification_id")){

                                                productMainSpecificationModel.setMain_specification_id(jsonObjectMainSpecification.getString("main_specification_id"));

                                            }

                                            if(jsonObjectMainSpecification.has("name")){

                                                productMainSpecificationModel.setMain_specification_name(jsonObjectMainSpecification.getString("name"));
                                            }

                                            if(jsonObjectMainSpecification.has("pspecification")){

                                                JSONArray jsonArraySubSpecicfication = jsonObjectMainSpecification.getJSONArray("pspecification");

                                                HashMap<String,String> hashMap;

                                                HashMap<String,HashMap<String,String>> hashMapData;

                                                List<HashMap<String,String>> hashMapsList = new ArrayList<>() ;

                                                for(int j=0;j<jsonArraySubSpecicfication.length();j++){

                                                    productSubSpecificationModel = new ProductSubSpecification();

                                                    hashMap= new HashMap<>();

                                                    hashMapData= new HashMap<>();

                                                    JSONObject jsonObjectSubSpecificationData = jsonArraySubSpecicfication.getJSONObject(j);

                                                    if(jsonObjectSubSpecificationData.has("product_specification_id")){

                                                        productSubSpecificationModel.setSubSpecificationId(jsonObjectSubSpecificationData.getString(
                                                                "product_specification_id"));
                                                    }

                                                    if(jsonObjectSubSpecificationData.has("product_specification_key")
                                                            &&jsonObjectSubSpecificationData.has("product_specification_value")){

                                                        String key = jsonObjectSubSpecificationData.getString("product_specification_key");

                                                        String value = jsonObjectSubSpecificationData.getString("product_specification_value");

                                                        if(!hashMap.containsKey(jsonObjectSubSpecificationData.getString("product_specification_key"))){

                                                            hashMap.put(key,value);
                                                        }

                                                        hashMapData.put(productMainSpecificationModel.getMain_specification_id(),hashMap);

                                                        hashMapsList.add(hashMap);

                                                        subSpecificationDataHashMap.put(productMainSpecificationModel.getMain_specification_id(),hashMapsList);

                                                        productSubSpecificationModel.setMainSpecificationId(productMainSpecificationModel.getMain_specification_id());

                                                        productSubSpecificationModel.setProductSubSpecificationDataFinal(hashMapData);

                                                        subSpecificationHashMapData.put(key,productSubSpecificationModel.getSubSpecificationId());

                                                    }
                                                    productSubSpecificationArrayList.add(productSubSpecificationModel);

                                                }
                                            }
                                            productMainSpecificationArrayList.add(productMainSpecificationModel);

                                        }
                                    }
                                    productSubSpecificationArrayList.size();

                                    initProductSpecificationLayout();

                                } else {
                                    progressDialog.cancel();
                                    initProductSpecificationLayout();

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
                    Log.v("Volley requestid ", String.valueOf(requestId));
                    Log.v("Volley Error", String.valueOf(error));
                }
            };
        }catch (Exception ex){

            Log.d("SpecificationActivity", "initVolleyCallback: " +ex);
        }

    }

    private void initProductSpecificationLayout(){

        if(!productSubSpecificationArrayList.isEmpty()&&!productMainSpecificationArrayList.isEmpty()){
            tv_SpecificationStatus.setVisibility(View.GONE);
            rvProductSpecification.setVisibility(View.VISIBLE);

            productSpecificationAdapter = new ProductSpecificationAdapter(this, productMainSpecificationArrayList,
                    subSpecificationDataHashMap,productSubSpecificationArrayList);
            rvProductSpecification.setHasFixedSize(true);
            rvProductSpecification.setNestedScrollingEnabled(false);
            rvProductSpecification.setLayoutManager(new GridLayoutManager(this, 1));
            rvProductSpecification.setItemAnimator(new DefaultItemAnimator());
            rvProductSpecification.setAdapter(productSpecificationAdapter);
        }else{
            tv_SpecificationStatus.setVisibility(View.VISIBLE);
            rvProductSpecification.setVisibility(View.GONE);
            tv_SpecificationStatus.setText("Product Specifications not available");
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProductSpecificationActivity.this, new NoInternetListener() {
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
        ProductSpecificationActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
