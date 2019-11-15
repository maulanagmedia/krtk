package gmedia.net.id.kartikaelektrik;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {

    private EditText edtPasswordLama;
    private EditText edtPasswordBaru;
    private EditText edtKonfPasswordBaru;
    private TextView tvSaveButton;
    private LinearLayout llSaveButton;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private TextInputLayout tilPasswordLama;
    private TextInputLayout tilPasswordBaru;
    private TextInputLayout tilKonfPasswordBaru;
    private String urlChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gmedia.net.id.kartikaelektrik.R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        initUI();
    }

    private void initUI() {

        tilPasswordLama = (TextInputLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.til_password_lama);
        tilPasswordBaru = (TextInputLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.til_password_baru);
        tilKonfPasswordBaru = (TextInputLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.til_konf_password_baru);
        edtPasswordLama = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_password_lama);
        edtPasswordBaru = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_password_baru);
        edtKonfPasswordBaru = (EditText) findViewById(gmedia.net.id.kartikaelektrik.R.id.edt_konf_password_baru);
        tvSaveButton = (TextView) findViewById(gmedia.net.id.kartikaelektrik.R.id.tv_save);
        llSaveButton = (LinearLayout) findViewById(gmedia.net.id.kartikaelektrik.R.id.ll_save_container);
        setTitle("Ubah Password");

        urlChangePassword = ServerURL.savePassword;
        session = new SessionManager(ChangePassword.this);

        tvSaveButton.setText("Ubah Password");

        llSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword();
            }
        });
    }

    private void UpdatePassword() {

        final HashMap<String, String> user = session.getUserDetails();
        final String userName = user.get(session.TAG_NAMA);
        String passLama = user.get(session.TAG_PASSWORD);

        //Mondatory
        if(!iv.mondatoryEdittext(tilPasswordLama,edtPasswordLama,"Password Lama Tidak Boleh Kosong")){
            return;
        }
        if(!iv.mondatoryEdittext(tilPasswordBaru,edtPasswordBaru,"Password Baru Tidak Boleh Kosong")){
            return;
        }
        if(!iv.mondatoryEdittext(tilKonfPasswordBaru,edtKonfPasswordBaru,"Konfirmasi Password Baru Tidak Boleh Kosong")){
            return;
        }

        //Validation
        if(!iv.ValidateOldPassword(tilPasswordLama,edtPasswordLama,passLama)){
            return;
        }

        if(!iv.syncPassword(edtPasswordBaru,tilKonfPasswordBaru,edtKonfPasswordBaru)){
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", userName);
            jsonBody.put("oldpassword", edtPasswordLama.getText().toString());
            jsonBody.put("newpassword", edtPasswordBaru.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(ChangePassword.this, jsonBody, "PUT", urlChangePassword, "Password has been updated", "Failed to update the Password", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");
                            if(iv.parseNullInteger(status) == 200){
                                session.createLoginSession(user.get(session.TAG_UID),
                                        user.get(session.TAG_NIK),
                                        user.get(session.TAG_NAMA),
                                        edtPasswordBaru.getText().toString(),
                                        user.get(session.TAG_TOKEN),
                                        user.get(session.TAG_EXP),
                                        user.get(session.TAG_LEVEL),
                                        user.get(SessionManager.TAG_LABA),
                                        user.get(SessionManager.TAG_NAMA_FULL));
                                finish();
                            }
                            Toast.makeText(ChangePassword.this, message,Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(ChangePassword.this, e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(ChangePassword.this, result,Toast.LENGTH_LONG).show();

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
