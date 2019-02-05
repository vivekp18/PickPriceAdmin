package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.Adapter.HighLightsAdapter;
import com.efunhub.pickpriceadmin.Adapter.OffersListAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.Highlights;
import com.efunhub.pickpriceadmin.Model.Offers;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.PicassoTrustAll;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.RETREIVE_PRODUCT_ALL_DETAILS;

/**
 * Created by Admin on 04-01-2019.
 */

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private ImageView ivArrowNext;
    private ImageView ivProductImage;
    private ArrayList<String> optionSetArrayList;
    private String productImage;
    private String productOffer;
    private String productHighlights;


    private ArrayList<Offers> offersArrayList;
    private ArrayList<Highlights> highlightsArrayList;
    private ArrayList<String> offersArrayListData;
    private ArrayList<String> highLightsArrayListData;

    private Offers offersModel;
    private Highlights highlightsModel;

    private LinearLayout offersLinearlayout;
    private LinearLayout highLightssLinearlayout;

    private RecyclerView rv_OffersView;
    private RecyclerView rv_HighLightsView;
    private OffersListAdapter offersListAdapter;
    private HighLightsAdapter highLightsAdapter;

    private TextView tv_NoOffers;
    private TextView tv_NoHighLights;

    //private TextView tvAvailability;
    private Button btnAvailability;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;

    ImageView iv_ProductCategory;
    TextView tv_ProductName, tv_ProductDesc, tv_ProductPrice,tv_DealerShopName;

    private String productId,dealerId;


    private String Retreive_Products_details_URL="productdetails.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        init();

        setupToolbar();

        if(connectivityStatus){
            retreiveProductOfferAndHighlights();
        }else{
            toastClass.makeToast(getApplicationContext(),"Please check internet connection.");
        }
        ivArrowNext.setOnClickListener(this);


    }


    private void retreiveProductOfferAndHighlights(){

        progressDialog = ProgressDialog.show(ProductDetailsActivity.this, "Pick Price",
                "Please Wait", false, true);

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductDetailsActivity.this);

        HashMap<String,String> params = new HashMap<>();

        params.put("dealer_id",dealerId);
        params.put("product_id",productId);

        mVollyService.postDataVolleyParameters(RETREIVE_PRODUCT_ALL_DETAILS,
                this.getResources().getString(R.string.base_url) + Retreive_Products_details_URL,params);

    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETREIVE_PRODUCT_ALL_DETAILS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    if(jsonObject.has("alldeatils")){

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("alldeatils");

                                        for(int i=0;i<jsonArrayData.length();i++){

                                            JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);

                                            tv_ProductName.setText(jsonObjectData.getString("product_name"));
                                            tv_ProductDesc.setText(jsonObjectData.getString("description"));
                                            tv_ProductPrice.setText(getResources().getString(R.string.currency)+" "+jsonObjectData.getString("price"));
                                            tv_DealerShopName.setText("Shop name: "+jsonObjectData.getString("shopname"));
                                        }

                                    }


                                    if(jsonObject.has("alloffers")){

                                        if(!offersArrayList.isEmpty()){
                                            offersArrayList.clear();
                                        }

                                        JSONArray jsonArrayOffersdata = jsonObject.getJSONArray("alloffers");

                                        for (int i=0;i<jsonArrayOffersdata.length();i++){

                                            offersModel = new Offers();

                                            JSONObject jsonObjectOffer = jsonArrayOffersdata.getJSONObject(i);

                                            if(jsonObjectOffer.has("offer_id")){

                                                offersModel.setOffers_id(jsonObjectOffer.getString("offer_id"));

                                            }

                                            if(jsonObjectOffer.has("offer_name")){

                                                offersModel.setOffer(jsonObjectOffer.getString("offer_name"));
                                                offersArrayListData.add(jsonObjectOffer.getString("offer_name"));
                                            }

                                            offersArrayList.add(offersModel);
                                        }


                                    }

                                    if(jsonObject.has("allhighlights")){

                                        if(!highlightsArrayList.isEmpty()){
                                            highlightsArrayList.clear();
                                        }

                                        JSONArray jsonArrayHighlightsdata = jsonObject.getJSONArray("allhighlights");

                                        for (int i=0;i<jsonArrayHighlightsdata.length();i++){

                                            highlightsModel = new Highlights();

                                            JSONObject jsonObjectHighLights = jsonArrayHighlightsdata.getJSONObject(i);

                                            if(jsonObjectHighLights.has("highlight_id")){

                                                highlightsModel.setHighlight_id(jsonObjectHighLights.getString("highlight_id"));

                                            }

                                            if(jsonObjectHighLights.has("highlight_name")){

                                                highlightsModel.setHighlights(jsonObjectHighLights.getString("highlight_name"));
                                                highLightsArrayListData.add(jsonObjectHighLights.getString("highlight_name"));
                                            }
                                            highlightsArrayList.add(highlightsModel);
                                        }


                                    }

                                    initOffersAndHighlightsLayout();

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Offers and Highlights are not available. ");

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

            Log.d("CategoryListActivity", "initVolleyCallback: " +ex);
        }

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Product Details");
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
        ivArrowNext = findViewById(R.id.iv_arrow_next);

        ivProductImage = findViewById(R.id.ivProductDeatilsImage);

        offersLinearlayout = findViewById(R.id.offers_parent_layout);

        highLightssLinearlayout = findViewById(R.id.highlights_parent_layout);

        rv_OffersView = findViewById(R.id.rvOffers);
        rv_HighLightsView = findViewById(R.id.rvHighLights);

        tv_NoHighLights = findViewById(R.id.tv_HighLightsNotAvailable);
        tv_NoOffers = findViewById(R.id.tv_offersNotAvailable);
        btnAvailability = findViewById(R.id.btnAvailable);

        tv_ProductName = findViewById(R.id.tvProductName);
        tv_ProductDesc = findViewById(R.id.tvProductDesc);
        tv_ProductPrice = findViewById(R.id.tvProductPrice);
        tv_DealerShopName = findViewById(R.id.tvDealerShopName);

        if(getIntent().hasExtra("ProductId")){
            productId = getIntent().getStringExtra("ProductId");
        }else{
            productId="";
        }

        if(getIntent().hasExtra("DealerId")){
            dealerId = getIntent().getStringExtra("DealerId");
        }else{
            dealerId="";
        }
        if(getIntent().hasExtra("ProductImage")){
            productImage = getIntent().getStringExtra("ProductImage");
        }else{
            productImage="";
        }

        if(getIntent().hasExtra("ProductStatus")){

            if(Integer.parseInt(getIntent().getStringExtra("ProductStatus"))==0){
                btnAvailability.setText("Available");
            }else{
                btnAvailability.setText("Not Available");
            }

        }

        if (!productImage.equalsIgnoreCase("")
                || !productImage.isEmpty()) {

            try{

                PicassoTrustAll.getInstance(this)
                        .load(productImage)
                        .placeholder(R.drawable.ic_electronics)
                        .into(ivProductImage);


            }catch(Exception ex){

                Log.e("ProductDetailsActivity", "onBindViewHolder: ",ex );
            }


        } else {
            ivProductImage.setImageResource(R.drawable.ic_electronics);
        }

        offersArrayList = new ArrayList<>();
        highlightsArrayList = new ArrayList<>();

        offersArrayListData = new ArrayList<>();
        highLightsArrayListData = new ArrayList<>();

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    public void initOffersAndHighlightsLayout(){


        if(!offersArrayList.isEmpty()){
            rv_OffersView.setVisibility(View.VISIBLE);
            tv_NoOffers.setVisibility(View.GONE);
            offersListAdapter = new OffersListAdapter(this, offersArrayList);
            rv_OffersView.setHasFixedSize(true);
            rv_OffersView.setNestedScrollingEnabled(false);
            rv_OffersView.setLayoutManager(new GridLayoutManager(this, 1));
            rv_OffersView.setItemAnimator(new DefaultItemAnimator());
            rv_OffersView.setAdapter(offersListAdapter);
        }else{
            rv_OffersView.setVisibility(View.GONE);
            tv_NoOffers.setVisibility(View.VISIBLE);
        }

        if(!highlightsArrayList.isEmpty()){
            rv_HighLightsView.setVisibility(View.VISIBLE);
            tv_NoHighLights.setVisibility(View.GONE);
            highLightsAdapter = new HighLightsAdapter(this, highlightsArrayList);
            rv_HighLightsView.setHasFixedSize(true);
            rv_HighLightsView.setNestedScrollingEnabled(false);
            rv_HighLightsView.setLayoutManager(new GridLayoutManager(this, 1));
            rv_HighLightsView.setItemAnimator(new DefaultItemAnimator());
            rv_HighLightsView.setAdapter(highLightsAdapter);
        }else {
            rv_HighLightsView.setVisibility(View.GONE);
            tv_NoHighLights.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.iv_arrow_next:
                Intent intent = new Intent(getApplicationContext(),ProductSpecificationActivity.class);
                intent.putExtra("ProductId",productId);
                intent.putExtra("DealerId",dealerId);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProductDetailsActivity.this, new NoInternetListener() {
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
        ProductDetailsActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
