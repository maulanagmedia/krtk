package gmedia.net.id.kartikaelektrik;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import gmedia.net.id.kartikaelektrik.notificationService.InitFirebaseSetting;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdater;
import gmedia.net.id.kartikaelektrik.util.RuntimePermissionsActivity;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
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
    private String uid = "", nik = "", nama = "", token = "", exp = "", level = "", laba = "", fullName = "", idJabatan = "",jabatan = "", levelJabatan = "";
    SessionManager session;
    private Button btnLogin;
    private static final int REQUEST_PERMISSIONS = 20;
    private EditText edtUsername;
    private EditText edtPassword;
    private boolean doubleBackToExitPressedOnce = false;
    private ItemValidation iv = new ItemValidation();
    private String refreshToken = "";
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        // for android > M
        if (ContextCompat.checkSelfPermission(LoginScreen.this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginScreen.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginScreen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(LoginScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {

            LoginScreen.super.requestAppPermissions(new
                            String[]{
                            Manifest.permission.WAKE_LOCK
                            ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.CAMERA
                            ,Manifest.permission.ACCESS_FINE_LOCATION
                            ,Manifest.permission.ACCESS_COARSE_LOCATION
                    }, gmedia.net.id.kartikaelektrik.R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        //String refreshToken = FirebaseInstanceId.getInstance().getToken();
        InitFirebaseSetting.getFirebaseSetting(LoginScreen.this);
        refreshToken = FirebaseInstanceId.getInstance().getToken();
        JSON_URL = ServerURL.doLogin;
        edtUsername = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_username);
        edtPassword = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_password);
        btnLogin = (Button) findViewById(gmedia.net.id.kartikaelektrik.R.id.btn_login);
        dialogBox = new DialogBox(LoginScreen.this);

        session = new SessionManager(getApplicationContext());

        //TODO: disable before release
        /*if(session.isLoggedIn()){

            HashMap<String, String> user = session.getUserDetails();
            edtUsername.setText(user.get(session.TAG_NAMA));
            edtPassword.setText(user.get(session.TAG_PASSWORD));
            login();
        }*/

        //TODO: enable before release
        session.logout();

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

        dialogBox.showDialog(false);

        btnLogin.setEnabled(false);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", edtUsername.getText());
            jsonBody.put("password", edtPassword.getText());
            jsonBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String username = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();

        new ApiVolley(LoginScreen.this, jsonBody, "POST", JSON_URL, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    dialogBox.dismissDialog();
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getJSONObject("metadata").getString("status");
                    String message = obj.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        uid = obj.getJSONObject("response").getString("id");
                        token = obj.getJSONObject("response").getString("token");
                        exp = obj.getJSONObject("response").getString("expired_at");
                        level = obj.getJSONObject("response").getString("level");
                        laba = obj.getJSONObject("response").getString("laba");
                        fullName = obj.getJSONObject("response").getString("nama");

                        //TODO: About privilege user
                        // Diambil dari privilege - Level
                        // Level 1: Menu admin all write and read
                        // Level 2: Menu admin view only
                        // Level 3: Menu admin only available for masuk sebagai sales
                        // Level 4 : Sales, tidak ada akses admin

                        idJabatan = obj.getJSONObject("response").getJSONObject("privilege").getString("id_jab");
                        jabatan = obj.getJSONObject("response").getJSONObject("privilege").getString("jabatan");
                        levelJabatan = obj.getJSONObject("response").getJSONObject("privilege").getString("level");
                        Log.d(TAG, "jabatan: " + levelJabatan);
                        // nik = obj.getJSONObject("response").getString("nik");
                        nama = username;// obj.getJSONObject("response").getString("nama");
                        nik= uid;

                        if (token.isEmpty()) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            onLoginFailed();

                        } else {

                            session.createLoginSession(uid, nik, nama, password, token, exp, level, laba, fullName);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            session.saveNikAsli(nik);
                            session.saveNamaAsli(fullName);
                            session.saveIdJabatan(idJabatan);
                            session.saveJabatan(jabatan);
                            session.saveLevelJabatan(levelJabatan);
                            session.saveUsername(edtUsername.getText().toString());

                            btnLogin.setEnabled(true);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    edtUsername.setText("");
                                    edtPassword.setText("");
                                    onLoginSuccess();
                                }
                            });
                        }
                    }else{

                        btnLogin.setEnabled(true);
                        Snackbar.make(findViewById(R.id.activity_login_screen), message, Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    onLoginFailed();
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                onLoginFailed();
                String message = "Login gagal, silahkan coba beberapa saat lagi";
                if(result.toLowerCase().contains("com.android.volley.noconnectionerror")){
                    message = "Tidak ada koneksi internet";
                }else if(result.toLowerCase().contains("com.android.volley.servererror")){
                    message = "Username atau password anda salah";
                }
//                    Toast.makeText(getApplicationContext(), "Username atau password anda salah", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        /*MasterDataHandler mdh = new MasterDataHandler(LoginScreen.this);
        mdh.checkWeeklyUpdate();*/
        //Intent intent = new Intent(this, Dashboard.class);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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
            try {

                stopService(new Intent(LoginScreen.this, LocationUpdater.class));
            }catch (Exception e){
                e.printStackTrace();
            }

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
