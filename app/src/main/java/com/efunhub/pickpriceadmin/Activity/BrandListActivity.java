package com.efunhub.pickpriceadmin.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.Adapter.BrandsListAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.Brand;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.ImageFilePath;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.ADD_BRAND;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.CAMERA_CATEGORY_IMAGE_REQUEST;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.DELETE_BRAND;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.PICK_IMAGE_BRAND_REQUEST;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.RETRIVE_BRAND;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.STORAGE_PERMISSION_CODE;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.SUBMIT_CATEGORY_BRAND__IMAGES;

public class BrandListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvBrandsList;
    private BrandsListAdapter brandsListAdapter;

    private ArrayList<Brand> brandArrayList;
    private Brand brandModel;
    private HashMap<String,String> brandHashMap;

    //Add Brand Dialog
    private AlertDialog alertDialog;
    private ImageView ivClose;

    private ImageView ivaddBrandImage;
    private EditText edt_brandName;
    private Button btn_addBrand;
    private TextView tvBrandsAvailble;

    //for Image
    private String brandImageId = null;
    private String brandImage = null;
    private String brandImagesUrl = "upload_brand_image.php";
    private String brandName = null;


    private ToastClass toastClass;
    private ProgressDialog progressDialog;

    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;

    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;

    private String RETRIVE_Brands="show_brands.php";

    private String ADD_Brand="add_brand.php";

    private String DELETE_Brand="delete_brand.php";

    public static BrandListActivity brandListActivityCntx;

    private Uri filePath;
    private Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_list);

        init();
        setupToolbar();
        requestpermission();

        brandListActivityCntx = this;

        if (connectivityStatus) {

            retriveAllBrands();

        } else {
            Toast.makeText(BrandListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                addDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BrandListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_brand, null);
        dialogBuilder.setView(dialogView);


        ivClose = dialogView.findViewById(R.id.ivCloseAddBrand);

        ivaddBrandImage = dialogView.findViewById(R.id.iv_brandImage);

        edt_brandName = dialogView.findViewById(R.id.edtAddBrandName);

        btn_addBrand = dialogView.findViewById(R.id.btnAddBrand);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ivaddBrandImage.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                showFileChooser(PICK_IMAGE_BRAND_REQUEST);
               /* Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_CATEGORY_IMAGE_REQUEST);//zero can be replaced with any action code*/
            }
        });


        btn_addBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectivityStatus){
                    if(checkValidations()){
                        alertDialog.dismiss();

                        brandName = edt_brandName.getText().toString();

                        progressDialog = ProgressDialog.show(BrandListActivity.this, "Pick Price",
                                "Please wait..", false, true);

                        addBrand();
                    }
                }
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void showFileChooser(int pickImageRequest) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageRequest);
    }

    public boolean checkValidations() {

        if (TextUtils.isEmpty(edt_brandName.getText().toString())||
                edt_brandName.getText().toString().equalsIgnoreCase("")) {
            toastClass.makeToast(BrandListActivity.this, "Please Enter Brand Name");
            return false;
        }else if(brandImage==null){
            toastClass.makeToast(BrandListActivity.this, "Please Add Brand Image");
            return false;
        }
        return true;
    }


    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Brands");
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
        rvBrandsList = findViewById(R.id.rvBrandsList);
        brandArrayList = new ArrayList<>();
        brandHashMap = new HashMap<>();

        tvBrandsAvailble = findViewById(R.id.tv_brandsAvailable);

        sessionManager = new SessionManager(getApplicationContext());

        toastClass = new ToastClass();
    }


    private void retriveAllBrands() {

        progressDialog = ProgressDialog.show(BrandListActivity.this, "Pick Price",
                "Please wait..", false, true);

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, BrandListActivity.this);

        mVollyService.postDataVolley(RETRIVE_BRAND,
                this.getResources().getString(R.string.base_url) + RETRIVE_Brands);
    }

    public void addBrand(){

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, BrandListActivity.this);

        HashMap<String, String> params = new HashMap<>();
        params.put("name", brandName);
        mVollyService.postDataVolleyParameters(ADD_BRAND,
                this.getResources().getString(R.string.base_url) + ADD_Brand,params);


    }

    private void uploadImage(String categoryImgId, String image, int i) {

        progressDialog = ProgressDialog.show(BrandListActivity.this, "Pick Price",
                "Please wait..", false, true);

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, BrandListActivity.this);

        HashMap<String, String> param = new HashMap<>();
        param.put("brand_id", categoryImgId);
        param.put("brandimage", image);

        if (i == 1) {
            param.put("brandimage", image);
            mVollyService.postDataVolleyParameters(SUBMIT_CATEGORY_BRAND__IMAGES,
                    this.getResources().getString(R.string.base_url) + brandImagesUrl, param);
        }
    }

    public void deleteBrand(String brandId){


        progressDialog = ProgressDialog.show(BrandListActivity.this, "Pick Price",
                "Please wait..", false, true);

        if (connectivityStatus) {

            initVolleyCallback();

            mVollyService = new VolleyService(mResultCallback, BrandListActivity.this);

            HashMap<String, String> params = new HashMap<>();

            params.put("brand_id", brandId);
            mVollyService.postDataVolleyParameters(DELETE_BRAND,
                    this.getResources().getString(R.string.base_url) + DELETE_Brand,params);
        }else{
            Toast.makeText(BrandListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETRIVE_BRAND:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(!brandArrayList.isEmpty() ){
                                    brandArrayList.clear();
                                }

                                int status = jsonObject.getInt("status");
                                if (status == 1) {

                                    progressDialog.cancel();


                                    if (jsonObject.has("brands")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("brands");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            brandModel = new Brand();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("brand_id")){
                                                brandModel.setBrand_id(jsonObj.getString("brand_id"));
                                            }else{
                                                brandModel.setBrand_id("");
                                            }

                                            if(jsonObj.has("name")){
                                                brandModel.setBrand_name(jsonObj.getString("name"));
                                            }else{
                                                brandModel.setBrand_name("");
                                            }

                                            if(jsonObj.has("bimage")){
                                                brandModel.setBimage(jsonObj.getString("bimage"));
                                            }else{
                                                brandModel.setBimage("");
                                            }

                                            brandArrayList.add(brandModel);

                                        }

                                        initBrandsList();
                                    }


                                } else {
                                    progressDialog.cancel();
                                    tvBrandsAvailble.setVisibility(View.VISIBLE);
                                    if(jsonObject.has("msg")){
                                        tvBrandsAvailble.setText(jsonObject.getString("msg"));
                                    }else{
                                        tvBrandsAvailble.setText("No brands available");
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case ADD_BRAND :

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), jsonObject.getString(
                                            "Something went wrong. Please try again"));

                                } else if(status == 1){
                                    progressDialog.cancel();

                                    brandImageId = jsonObject.getString("brand_id");
                                    if (brandImageId != null) {

                                        uploadImage(brandImageId, brandImage, 1);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.cancel();
                                toastClass.makeToast(getApplicationContext(), "Error.");
                            }

                            break;

                        case DELETE_BRAND:

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),
                                            "Something went wrong. Please try again");

                                } else if(status == 1){
                                    progressDialog.cancel();

                                    toastClass.makeToast(getApplicationContext(),
                                            "Successfully Deleted");

                                    retriveAllBrands();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            break;

                        case SUBMIT_CATEGORY_BRAND__IMAGES:
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),
                                            "Successfully Uploaded");
                                    retriveAllBrands();
                                }else{
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),
                                            "Error while Uploading category image.");
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

            Log.d("BrandListActivity", "initVolleyCallback: " +ex);
        }

    }

    public void initBrandsList(){


        //arrayList.add("Ashish");
        //arrayList.add("Parth");

        brandsListAdapter = new BrandsListAdapter(this, brandArrayList);
        rvBrandsList.setHasFixedSize(true);
        rvBrandsList.setNestedScrollingEnabled(false);
        rvBrandsList.setLayoutManager(new GridLayoutManager(this, 1));
        rvBrandsList.setItemAnimator(new DefaultItemAnimator());
        rvBrandsList.setAdapter(brandsListAdapter);
    }


    //converting image to base64 string
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void requestpermission() {
        if (ContextCompat.checkSelfPermission(BrandListActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(BrandListActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CATEGORY_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {

                    Bitmap brandBitmap = (Bitmap) data.getExtras().get("data");

                    ivaddBrandImage.setImageBitmap(brandBitmap);
                    //String profilepic = getResizedBitmap(bitmap, 400);
                    brandImage = getStringImage(brandBitmap);
                }
                break;

            case  PICK_IMAGE_BRAND_REQUEST:

                if(data!=null){
                    filePath = data.getData();
                    try {
                        Bitmap docBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);

                        //realPath = ImageFilePath.getPath(this, data.getData());

                        //Convert System Path to string and Check SDK version is Greater then KITKAT
                        String picturePath = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            picturePath = ImageFilePath.getPath(this, filePath);
                        }

                        bitmap = exifInterface(picturePath, docBitmap);
                        //String profilepic = getResizedBitmap(bitmap, 400);
                        brandImage = getStringImage(bitmap);

                        ivaddBrandImage.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private Bitmap exifInterface(String filePath, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        return rotateBitmap(bitmap, orientation);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        brandListActivityCntx = this;

        //Check connectivity
        checkConnectivity = new CheckConnectivity(BrandListActivity.this, new NoInternetListener() {
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
        BrandListActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
