package gmedia.net.id.kartikaelektrik;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import gmedia.net.id.kartikaelektrik.notificationService.InitFirebaseSetting;
import gmedia.net.id.kartikaelektrik.services.BackgroundLocationService;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.RuntimePermissionsActivity;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends RuntimePermissionsActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private String JSON_URL = "";
    private String uid = "", nik = "", nama = "", token = "", exp = "", level = "", laba = "";
    SessionManager session;
    private Button btnLogin;
    private static final int REQUEST_PERMISSIONS = 20;
    private EditText edtUsername;
    private EditText edtPassword;
    private boolean doubleBackToExitPressedOnce = false;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gmedia.net.id.kartikaelektrik.R.layout.activity_login_screen);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


        // for android > M
        if (ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                LoginScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {

            LoginScreen.super.requestAppPermissions(new
                            String[]{Manifest.permission.WRITE_SETTINGS, Manifest.permission.WAKE_LOCK, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WAKE_LOCK, android.Manifest.permission.READ_EXTERNAL_STORAGE}, gmedia.net.id.kartikaelektrik.R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        //String refreshToken = FirebaseInstanceId.getInstance().getToken();
        InitFirebaseSetting.getFirebaseSetting(LoginScreen.this);
        JSON_URL = getResources().getString(R.string.url_login);
        edtUsername = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_username);
        edtPassword = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_password);
        btnLogin = (Button) findViewById(gmedia.net.id.kartikaelektrik.R.id.btn_login);

        session = new SessionManager(getApplicationContext());

        //TODO: disable before release
        /*if(session.isLoggedIn()){
            HashMap<String, String> user = session.getUserDetails();
            edtUsername.setText(user.get(session.TAG_NAMA));
            edtPassword.setText(user.get(session.TAG_PASSWORD));
            login();
        }*/

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("username", edtUsername.getText());
            jsonBody.put("password", edtPassword.getText());

            final String username = edtUsername.getText().toString();
            final String password = edtPassword.getText().toString();


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(1, JSON_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // sukseslogin(response);
                    final boolean loged;

                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getJSONObject("metadata").getString("status");
                        String message = obj.getJSONObject("metadata").getString("message");

                        if(iv.parseNullInteger(status) == 200){

                            uid = obj.getJSONObject("response").getString("id");
                            token = obj.getJSONObject("response").getString("token");
                            exp = obj.getJSONObject("response").getString("expired_at");
                            level = obj.getJSONObject("response").getString("level");
                            laba = obj.getJSONObject("response").getString("laba");
//                        nik = obj.getJSONObject("response").getString("nik");
                            nama = username;// obj.getJSONObject("response").getString("nama");
                            nik= uid;

                            if (token.isEmpty()) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                onLoginFailed();
                                progressDialog.dismiss();

                                return;
                            } else {

                                session.createLoginSession(uid, nik, nama, password, token, exp, level, laba);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                btnLogin.setEnabled(true);
                                startService(new Intent(LoginScreen.this, BackgroundLocationService.class));
                                onLoginSuccess();

                                edtUsername.setText("");
                                edtPassword.setText("");
                                progressDialog.dismiss();
                            }
                        }else{
                            progressDialog.dismiss();
                            btnLogin.setEnabled(true);
                            Snackbar.make(findViewById(R.id.activity_login_screen), message, BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                        onLoginFailed();
                        progressDialog.dismiss();

                        return;
                    }

                }
            }, new Response.ErrorListener() {
                @Override

                public void onErrorResponse(VolleyError error) {
                    onLoginFailed();
                    String message = "Login gagal, silahkan coba beberapa saat lagi";
                    if(error.toString().toLowerCase().contains("com.android.volley.noconnectionerror")){
                        message = "Tidak ada koneksi internet";
                    }else if(error.toString().toLowerCase().contains("com.android.volley.servererror")){
                        message = "Username atau password anda salah";
                    }
//                    Toast.makeText(getApplicationContext(), "Username atau password anda salah", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                    return;
                }
            }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Client-Service", "frontend-client");
                    params.put("Auth-Key", "gmedia_kartika");
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return String.format("application/json; charset=utf-8");
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };
            // MySingleton.getInstance(this).addToRequestQueue(stringRequest);

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            onLoginFailed();
            e.printStackTrace();
            progressDialog.dismiss();

            return;
        }
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        /*MasterDataHandler mdh = new MasterDataHandler(LoginScreen.this);
        mdh.checkWeeklyUpdate();*/
        //Intent intent = new Intent(this, Dashboard.class);
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (username.isEmpty()) {
            edtUsername.setError("Harap isi Username");
            valid = false;
        } else {
            edtUsername.setError(null);
        }

        if (password.isEmpty()) {
            edtPassword.setError("Harap isi Password");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            stopService(new Intent(LoginScreen.this, BackgroundLocationService.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        // take 2 second before the doubleBackToExitPressedOnce become false again
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
