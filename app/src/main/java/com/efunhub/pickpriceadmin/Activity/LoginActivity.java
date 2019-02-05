package com.efunhub.pickpriceadmin.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static com.efunhub.pickpriceadmin.Utility.ContantVariables.ADMIN_LOGIN;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText edtLoginPassword, edtLoginUsername;
    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String LoginURL="login_admin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_login);

        init();

        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (connectivityStatus) {
                    if (checkValid()) {
                        progressDialog = ProgressDialog.show(LoginActivity.this, "Please Wait", null, false, true);
                        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);*/
                        adminLogin();
                        //initVolleyCallback();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }

    private boolean checkValid() {
        //validations
        if (edtLoginUsername.getText().toString().equalsIgnoreCase("")) {
            toastClass.makeToast(getApplicationContext(), "Please enter user name");
            return false;
        } else if (edtLoginPassword.getText().toString().equalsIgnoreCase("")) {
            toastClass.makeToast(getApplicationContext(), "Please enter password");
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(LoginActivity.this, new NoInternetListener() {
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
        LoginActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    private void adminLogin() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, LoginActivity.this);

        HashMap<String, String> params = new HashMap<>();
        params.put("username", edtLoginUsername.getText().toString());
        params.put("password", edtLoginPassword.getText().toString());


        mVollyService.postDataVolleyParameters(ADMIN_LOGIN,
                this.getResources().getString(R.string.base_url) + LoginURL, params);
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
                            if (jsonObject.has("admin_id")) {

                                String admind = jsonObject.getString("admin_id");

                                sessionManager.createLoginSession(admind);

                                //guardLogin();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        } else if (status == 2) {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), " Please Check Username & Password");
                        } else if (status == 3) {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), "Username Not Exist ");
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

    private void init() {
        btnLogin = findViewById(R.id.btnLogin);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        edtLoginUsername = findViewById(R.id.edtLoginUsername);
        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());



    }

}
