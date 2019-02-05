package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.pickpriceadmin.BroadCastReciver.CheckConnectivity;
import com.efunhub.pickpriceadmin.Interface.IResult;
import com.efunhub.pickpriceadmin.Interface.NoInternetListener;
import com.efunhub.pickpriceadmin.R;
import com.efunhub.pickpriceadmin.Utility.SessionManager;
import com.efunhub.pickpriceadmin.Utility.ToastClass;
import com.efunhub.pickpriceadmin.Utility.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.PROFILE;
import static com.efunhub.pickpriceadmin.Utility.ContantVariables.UPDATE_PROFILE;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar mToolbar;
    private TextView tvToolbar;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String ProfileURL="profile_admin.php";
    private String UpdateProfileURL="update_profile_admin.php";

    private EditText edt_name;
    private EditText edt_user_name;
    private EditText edt_email;
    private EditText edt_phone_number;
    private Button btnUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        setupToolbar();

        adminLoadProfile();

        btnUpdateProfile.setOnClickListener(this);
    }


    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Profile");
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

        edt_name = findViewById(R.id.edtProfileName);
        edt_user_name = findViewById(R.id.edtProfileUsername);
        edt_phone_number = findViewById(R.id.edtProfileContact);
        edt_email = findViewById(R.id.edtProfileEMail);
        btnUpdateProfile = findViewById(R.id.btnProfileSave);

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = ProgressDialog.show(ProfileActivity.this, "Pick Price",
                "Please wait while loading..", false, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_Logout:
                appExitDialog();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void  appExitDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Do you want to Logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        sessionManager.logout();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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

    private void adminLoadProfile() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProfileActivity.this);

        mVollyService.postDataVolley(PROFILE,
                this.getResources().getString(R.string.base_url) + ProfileURL);
    }

    private void adminUpdateProfile() {



        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProfileActivity.this);

        HashMap<String,String> params = new HashMap<>();

        params.put("email",edt_email.getText().toString());
        params.put("contact",edt_phone_number.getText().toString());

        mVollyService.postDataVolleyParameters(UPDATE_PROFILE,
                this.getResources().getString(R.string.base_url) + UpdateProfileURL,params);
    }


    private boolean checkValidation(){
        if(TextUtils.isEmpty(edt_email.getText().toString())||
                edt_email.getText().toString().equalsIgnoreCase("")){
            edt_email.setError("Please enter email address");
            return false;
        }else if(edt_phone_number.getText().toString().equalsIgnoreCase("")||
                TextUtils.isEmpty(edt_phone_number.getText().toString())){

             edt_phone_number.setError("Please enter contact number");

            return false;
        }else if(edt_phone_number.getText().toString().length()<10) {
            edt_phone_number.setError("Please enter valid phone number");
            return false;
        }
        else if(!TextUtils.isEmpty(edt_email.getText().toString())||
                !edt_email.getText().toString().equalsIgnoreCase("")) {

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (!pat.matcher(edt_email.getText().toString()).matches()) {
                edt_email.setError("Please enter valid email address");
                return pat.matcher(edt_email.getText().toString()).matches();
            }else{
                if(!TextUtils.isEmpty(edt_phone_number.getText().toString())&&
                        !edt_phone_number.getText().toString().equalsIgnoreCase("")) {

                    String phoneRegex = "^[6-9][0-9]{9}$";

                    Pattern pattern = Pattern.compile(phoneRegex);

                    if (!pattern.matcher(edt_phone_number.getText().toString()).matches()) {
                        edt_phone_number.setError("Please enter valid phone number");
                        return pattern.matcher(edt_phone_number.getText().toString()).matches();
                    }
                }
            }
        }

        return true;
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {

                    switch (requestId) {

                        case PROFILE:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    JSONObject json_results = jsonObject.getJSONObject("myprofile");

                                    if (json_results.has("name")) {

                                        edt_name.setText(json_results.getString("name"));
                                    }else{
                                        edt_name.setText("Not available");
                                    }
                                    if (json_results.has("username")) {

                                        edt_user_name.setText(json_results.getString("username"));
                                    }else{
                                        edt_user_name.setText("Not available");
                                    }
                                    if (json_results.has("email")) {

                                        edt_email.setText(json_results.getString("email"));

                                    }else{
                                        edt_email.setText("Not available");
                                    }if (json_results.has("contact")) {
                                        edt_phone_number.setText(json_results.getString("contact"));
                                    }else{
                                        edt_phone_number.setText("Not available");
                                    }

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Unable to load data");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case UPDATE_PROFILE:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Please try again.");

                                } else if(status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), " Updated Sucessfully");
                                }

                                else if(status == 2) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Enter Valid Contact");
                                }

                                else if(status == 3) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Check Email Format");
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

            Log.d("LoginActivity", "initVolleyCallback: " +ex);
        }

    }



    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProfileActivity.this, new NoInternetListener() {
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
        ProfileActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btnProfileSave:

                if(checkValidation()){
                    progressDialog = ProgressDialog.show(ProfileActivity.this, "Pick Price",
                            "Please wait while updating..", false, true);
                   adminUpdateProfile();
                }

                break;

        }
    }
}
