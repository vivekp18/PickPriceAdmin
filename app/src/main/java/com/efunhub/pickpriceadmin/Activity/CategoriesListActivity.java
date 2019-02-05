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
import com.efunhub.pickpriceadmin.Adapter.CategoriesListAdapter;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.Model.Category;
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

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.ADD_CATEGORY;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.CAMERA_CATEGORY_IMAGE_REQUEST;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.DELETE_CATEGORY;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.PICK_IMAGE_CATEGORY_REQUEST;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.RETRIVE_CATEGORY;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.STORAGE_PERMISSION_CODE;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.SUBMIT_CATEGORY_IMAGE__IMAGES;

public class CategoriesListActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView tvToolbar;
    private RecyclerView rvCategoryList;
    private CategoriesListAdapter categoriesListAdapter;

    private ArrayList<Category> categoryArrayList;
    private Category categoryModel;

    //Add  Category
    private AlertDialog alertDialog;
    private ImageView ivClose;
    private ImageView ivaddCategoryImage;
    private EditText edt_CategoryName;
    private Button btn_addCategory;
    private TextView tv_categoriesAvail;

    //for Image
    private String categoryImageId = null;
    private String categoryImage = null;
    private String categoryImagesUrl = "upload_category_image.php";
    private String categoryName = null;


    private ToastClass toastClass;
    private ProgressDialog progressDialog;

    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;

    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;

    private String RETRIVE_Categories="show_category.php";

    private String ADD_Categories="add_category.php";

    private String DELETE_Categories="delete_category.php";

    public static CategoriesListActivity categoriesListActivityCntx;


    private Uri filePath;
    private Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        init();
        setupToolbar();
        requestpermission();

        categoriesListActivityCntx = this;

        if (connectivityStatus) {

            retriveAllCategories();

        } else {
            Toast.makeText(CategoriesListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CategoriesListActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        dialogBuilder.setView(dialogView);

        ivClose = dialogView.findViewById(R.id.ivCloseAddCategory);

        btn_addCategory = dialogView.findViewById(R.id.btnAddCategory);

        ivaddCategoryImage = dialogView.findViewById(R.id.iv_categoryImage);

        edt_CategoryName = dialogView.findViewById(R.id.edtAddCategoryName);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ivaddCategoryImage.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                showFileChooser(PICK_IMAGE_CATEGORY_REQUEST);
                /*Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_CATEGORY_IMAGE_REQUEST);*///zero can be replaced with any action code
            }
        });

        btn_addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectivityStatus){
                    if(checkValidations()){
                        alertDialog.dismiss();

                        categoryName = edt_CategoryName.getText().toString();

                        progressDialog = ProgressDialog.show(CategoriesListActivity.this, "Pick Price",
                                "Please wait..", false, true);

                        addCategories();
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

        if (TextUtils.isEmpty(edt_CategoryName.getText().toString())||edt_CategoryName.getText().toString().equalsIgnoreCase("")) {
            toastClass.makeToast(CategoriesListActivity.this, "Please Enter Category Name");
            return false;
        }else if(categoryImage==null){
            toastClass.makeToast(CategoriesListActivity.this, "Please Add Category Image");
            return false;
        }
        return true;
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Categories");
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
        rvCategoryList = findViewById(R.id.rvCategoryList);
        tv_categoriesAvail = findViewById(R.id.tv_catAvailable);

        categoryArrayList = new ArrayList<>();

        toastClass = new ToastClass();

        sessionManager = new SessionManager(getApplicationContext());


    }

    private void retriveAllCategories() {

        progressDialog = ProgressDialog.show(CategoriesListActivity.this, "Pick Price",
                "Please wait..", false, true);

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, CategoriesListActivity.this);

        mVollyService.postDataVolley(RETRIVE_CATEGORY,
                this.getResources().getString(R.string.base_url) + RETRIVE_Categories);
    }

    public void addCategories(){

            initVolleyCallback();

            mVollyService = new VolleyService(mResultCallback, CategoriesListActivity.this);

            HashMap<String, String> params = new HashMap<>();
            params.put("name", categoryName);
            mVollyService.postDataVolleyParameters(ADD_CATEGORY,
                    this.getResources().getString(R.string.base_url) + ADD_Categories,params);


    }

    private void uploadImage(String categoryImgId, String image, int i) {

        progressDialog = ProgressDialog.show(CategoriesListActivity.this, "Pick Price",
                "Please wait..", false, true);

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, CategoriesListActivity.this);

        HashMap<String, String> param = new HashMap<>();
        param.put("category_id", categoryImgId);
        param.put("categoryimage", image);

        if (i == 1) {
            param.put("categoryimage", image);
            mVollyService.postDataVolleyParameters(SUBMIT_CATEGORY_IMAGE__IMAGES,
                    this.getResources().getString(R.string.base_url) + categoryImagesUrl, param);
        }
    }

    public void deleteCategories(String categoryId){


        progressDialog = ProgressDialog.show(CategoriesListActivity.this, "Pick Price",
                "Please wait..", false, true);

        if (connectivityStatus) {

            initVolleyCallback();

            mVollyService = new VolleyService(mResultCallback, CategoriesListActivity.this);

            HashMap<String, String> params = new HashMap<>();

            params.put("category_id", categoryId);
            mVollyService.postDataVolleyParameters(DELETE_CATEGORY,
                    this.getResources().getString(R.string.base_url) + DELETE_Categories,params);
        }else{
            Toast.makeText(CategoriesListActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETRIVE_CATEGORY:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(!categoryArrayList.isEmpty() ){
                                    categoryArrayList.clear();
                                }

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();


                                    if (jsonObject.has("category")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("category");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            categoryModel = new Category();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("category_id")){
                                                categoryModel.setCategoryId(jsonObj.getString("category_id"));
                                            }else{
                                                categoryModel.setCategoryId("");
                                            }

                                            if(jsonObj.has("name")){
                                                categoryModel.setCategoryName(jsonObj.getString("name"));
                                            }else{
                                                categoryModel.setCategoryName("");
                                            }

                                            if(jsonObj.has("cimage")){
                                                categoryModel.setCategoryImage(jsonObj.getString("cimage"));
                                            }else{
                                                categoryModel.setCategoryImage("");
                                            }

                                            categoryArrayList.add(categoryModel);

                                        }

                                        initCategoryList();
                                    }


                                } else {
                                    progressDialog.cancel();
                                    tv_categoriesAvail.setVisibility(View.VISIBLE);
                                    if(jsonObject.has("msg")){
                                        tv_categoriesAvail.setText(jsonObject.getString("msg"));
                                    }else{
                                        tv_categoriesAvail.setText("No categories available");
                                    }
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case ADD_CATEGORY :

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            int status = jsonObject.getInt("status");
                                            if (status == 0) {
                                                progressDialog.cancel();
                                                toastClass.makeToast(getApplicationContext(), jsonObject.getString(
                                                        "Something went wrong. Please try again"));

                                            } else if(status == 1){
                                                progressDialog.cancel();

                                                categoryImageId = jsonObject.getString("category_id");
                                                if (categoryImageId != null) {

                                                    uploadImage(categoryImageId, categoryImage, 1);
                                                }
                                            }

                                        } catch (JSONException e) {
                                            progressDialog.cancel();
                                            e.printStackTrace();
                                        }

                            break;

                        case DELETE_CATEGORY:

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

                                                retriveAllCategories();
                                            }
                                        } catch (JSONException e) {
                                            progressDialog.cancel();
                                            e.printStackTrace();
                                        }

                            break;

                        case SUBMIT_CATEGORY_IMAGE__IMAGES:
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),
                                           "Successfully Uploaded");
                                    retriveAllCategories();
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

            Log.d("CategoryListActivity", "initVolleyCallback: " +ex);
        }

    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_CATEGORY_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {

                    Bitmap categoryBitmap = (Bitmap) data.getExtras().get("data");

                    ivaddCategoryImage.setImageBitmap(categoryBitmap);
                    //String profilepic = getResizedBitmap(bitmap, 400);
                    categoryImage = getStringImage(categoryBitmap);
                }
                break;

            case  PICK_IMAGE_CATEGORY_REQUEST:

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
                        categoryImage = getStringImage(bitmap);

                        ivaddCategoryImage.setImageBitmap(bitmap);

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

    //converting image to base64 string
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void requestpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }



    public void initCategoryList(){


        categoriesListAdapter = new CategoriesListAdapter(this, categoryArrayList);
        rvCategoryList.setHasFixedSize(true);
        rvCategoryList.setNestedScrollingEnabled(false);
        rvCategoryList.setLayoutManager(new GridLayoutManager(this, 1));
        rvCategoryList.setItemAnimator(new DefaultItemAnimator());
        rvCategoryList.setAdapter(categoriesListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        categoriesListActivityCntx = this;

        //Check connectivity
        checkConnectivity = new CheckConnectivity(CategoriesListActivity.this, new NoInternetListener() {
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
        CategoriesListActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
