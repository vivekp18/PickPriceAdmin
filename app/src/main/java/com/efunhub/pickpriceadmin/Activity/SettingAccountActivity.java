package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.CHANGE_PASSWORD;

public class SettingAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private TextView tvToolbar;
    private EditText edt_oldPassWord;
    private EditText edt_newPassWord;
    private Button btn_updatePassword;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String ChangePasswordURL="change_password_admin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);

        init();

        setupToolbar();

        btn_updatePassword.setOnClickListener(this);


    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Change Password");
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
        edt_oldPassWord =findViewById(R.id.edtSettingOldPassword);
        edt_newPassWord =findViewById(R.id.edtSettingNewPassword);
        btn_updatePassword =findViewById(R.id.btn_settingNewPassword);

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    private boolean checkValid() {
        //validations
        if (edt_oldPassWord.getText().toString().equalsIgnoreCase("")) {
            edt_oldPassWord.setError("Please enter old password");
            return false;
        } else if (edt_newPassWord.getText().toString().equalsIgnoreCase("")) {
            edt_newPassWord.setError("Please enter new password");
            return false;
        }else if(!TextUtils.isEmpty(edt_newPassWord.getText().toString())||
                !edt_newPassWord.getText().toString().equalsIgnoreCase("")){
            Pattern pattern;
            Matcher matcher;

            final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";


            pattern = Pattern.compile(PASSWORD_PATTERN);//^(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])$
            matcher = pattern.matcher(edt_newPassWord.getText().toString());

            if(!matcher.matches()){
                edt_newPassWord.setError(" Password must be at least 8 characters and " +
                        "must contain at least one lower case letter," +
                        "one upper case letter and one digit");
            }

            return matcher.matches();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(SettingAccountActivity.this, new NoInternetListener() {
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
        SettingAccountActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    private void changePassword() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, SettingAccountActivity.this);


        HashMap<String, String> params = new HashMap<>();
        params.put("oldp", edt_oldPassWord.getText().toString());
        params.put("newp", edt_newPassWord.getText().toString());


        mVollyService.postDataVolleyParameters(CHANGE_PASSWORD,
                this.getResources().getString(R.string.base_url) + ChangePasswordURL, params);
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

                            sessionManager.logout();

                            toastClass.makeToast(getApplicationContext(),jsonObject.getString("msg"));

                            //start login activity after updating password just to validate new password
                            Intent intent = new Intent(SettingAccountActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        } else if (status == 2) {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), jsonObject.getString("msg"));
                        } else if (status == 3) {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), jsonObject.getString("msg"));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_settingNewPassword:
                if (connectivityStatus) {
                    if (checkValid()) {
                        progressDialog = ProgressDialog.show(SettingAccountActivity.this, "Pick Price", "Please wait while updating password",
                                false, false);
                        changePassword();

                    }
                } else {
                    Toast.makeText(SettingAccountActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
