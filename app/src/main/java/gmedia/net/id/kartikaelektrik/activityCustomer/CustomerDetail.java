package gmedia.net.id.kartikaelektrik.activityCustomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.Rayon;
import gmedia.net.id.kartikaelektrik.util.DialogBox;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerDetail extends AppCompatActivity {

    private ItemValidation iv = new ItemValidation();
    private EditText edtKodePelanggan, edtNamaPelanggan, edtAlamat, edtKota, edtTelp, edtFax, edtEmail, edtTempo, edtBank, edtRekening, edtMaxPiutang, edtCP;
    private Spinner spRayon;
    private String urlGetRayon, urlSaveCustomer;
    private List<Rayon> rayonList;
    private Boolean insertMode = true;
    private String kodeCustomer;
    private TextInputLayout tilKodePelanggan, tilNamaPelanggan, tilAlamat, tilTelepon, tilMaxPiutang;
    private Object urlGetCustomer;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    private LinearLayout llKodePelanggan;
    private EditText edtNPWP;
    private Context context;
    private boolean isFromSaveLimit = false;
    private String kdcus = "";
    private LinearLayout llLimit;
    private EditText edtLimit;
    private String currentString = "";
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        initUI();
    }

    private void initUI() {

        urlGetCustomer = ServerURL.getCustomer;

        setTitle("Detail Pelanggan");
        llKodePelanggan = (LinearLayout) findViewById(R.id.ll_kode_pelanggan_container);
        tilKodePelanggan = (TextInputLayout) findViewById(R.id.til_kode_pelanggan);
        edtKodePelanggan = (EditText) findViewById(R.id.edt_kode_pelanggan);
        tilNamaPelanggan = (TextInputLayout) findViewById(R.id.til_nama_pelanggan);
        edtNamaPelanggan = (EditText) findViewById(R.id.edt_nama_pelanggan);
        tilAlamat = (TextInputLayout) findViewById(R.id.til_alamat);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat);
        edtKota = (EditText) findViewById(R.id.edt_kota);
        edtNPWP = (EditText) findViewById(R.id.edt_npwp);
        tilTelepon = (TextInputLayout) findViewById(R.id.til_telepon);
        edtTelp = (EditText) findViewById(R.id.edt_telepon);
        edtFax = (EditText) findViewById(R.id.edt_fax);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtTempo = (EditText) findViewById(R.id.edt_tempo);
        edtBank = (EditText) findViewById(R.id.edt_bank);
        edtRekening = (EditText) findViewById(R.id.edt_rekening);
        tilMaxPiutang = (TextInputLayout) findViewById(R.id.til_max_piutang);
        edtMaxPiutang = (EditText) findViewById(R.id.edt_max_piutang);
        edtCP = (EditText) findViewById(R.id.edt_cp);
        spRayon = (Spinner) findViewById(R.id.sp_kode_rayon);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        llLimit = (LinearLayout) findViewById(R.id.ll_limit);
        edtLimit = (EditText) findViewById(R.id.edt_limit);

        isFromSaveLimit = false;

        Bundle bundle = getIntent().getExtras();

        edtMaxPiutang.addTextChangedListener(iv.textChangeListenerCurrency(edtMaxPiutang));

        if(bundle != null){

            kodeCustomer = bundle.getString("kodecustomer","");
            if(!kodeCustomer.isEmpty()){
                llLimit.setVisibility(View.GONE);
            }else{
                llLimit.setVisibility(View.VISIBLE);
            }
            edtKodePelanggan.setText(kodeCustomer);
            llKodePelanggan.setVisibility(View.VISIBLE);
            edtKodePelanggan.setKeyListener(null);
            insertMode = false;
            tvSave.setText("Update Pelanggan");
            llSaveContainer.setVisibility(View.GONE);
        }else{

            llLimit.setVisibility(View.VISIBLE);
            llKodePelanggan.setVisibility(View.GONE);
            insertMode = true;
            tvSave.setText("Tambah Pelanggan");
            llSaveContainer.setVisibility(View.VISIBLE);
        }

        urlGetRayon = getResources().getString(R.string.url_get_all_rayon);

        edtLimit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().equals(currentString)){

                    String cleanString = editable.toString().replaceAll("[,.]", "");
                    edtLimit.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtLimit.setText(formatted);
                    edtLimit.setSelection(formatted.length());
                    edtLimit.addTextChangedListener(this);
                }
            }
        });

        getKodeRayon();
    }

    private void getKodeRayon() {

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(CustomerDetail.this, jsonBody, "GET", urlGetRayon, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            JSONArray arrayJSON = responseAPI.getJSONArray("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){

                                rayonList = new ArrayList<>();
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    rayonList.add(new Rayon(jo.getString("kode"),jo.getString("rayon")));
                                }

                                setSPRayonEntry();

                                if(!insertMode){
                                    getDetailCustomer();
                                }

                                llSaveContainer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            setOnSaveEvent();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }catch (Exception e){
                        }
                    }

                    @Override
                    public void onError(String result) {
                    }
                });
    }

    private void setOnSaveEvent() throws JSONException {

        if(!iv.mondatoryEdittext(tilKodePelanggan,edtKodePelanggan,"Kode Pelanggan Tidak Boleh Kosong") && !insertMode){
            return;
        }

        if(!iv.mondatoryEdittext(tilNamaPelanggan,edtNamaPelanggan,"Nama Pelanggan Tidak Boleh Kosong")){
            return;
        }

        if(!iv.mondatoryEdittext(tilAlamat,edtAlamat,"Alamat Tidak Boleh Kosong")){
            return;
        }

        if(!iv.mondatoryEdittext(tilTelepon,edtTelp,"No Telepon Tidak Boleh Kosong")){
            return;
        }

        /*if(!iv.mondatoryEdittext(tilMaxPiutang,edtMaxPiutang,"Maksimal Piutang Tidak Boleh Kosong")){
            return;
        }*/

        if(edtTempo.getText().length() <= 0 ){
            edtTempo.setText("0");
        }

        if(edtMaxPiutang.getText().length() <= 0 ){
            edtMaxPiutang.setText("0");
        }

        final String idRayon;

        Rayon rayon = (Rayon) spRayon.getSelectedItem();

        if(rayon != null){
            idRayon = rayon.getKode();
        }else{
            spRayon.requestFocus();
            return;
        }

        new android.app.AlertDialog.Builder(CustomerDetail.this)
                .setTitle("Peringatan")
                .setMessage("Data yang masuk tidak dapat dirubah, Anda yakin ingin menambah?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final JSONObject jsonBody = new JSONObject();

                                String method;
                                if(insertMode){
                                    method = "POST";
                                    urlSaveCustomer = getResources().getString(R.string.url_insert_customer);
                                    try {
                                        jsonBody.put("kode", "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    method = "PUT";
                                    urlSaveCustomer = getResources().getString(R.string.url_update_customer) + kodeCustomer;
                                    try {
                                        jsonBody.put("kode",kodeCustomer);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(isFromSaveLimit && !kdcus.isEmpty()){

                                    saveApprove(kdcus, edtLimit.getText().toString().replaceAll("[,.]", ""));
                                    return;
                                }

                                try {
                                    jsonBody.put("nama", edtNamaPelanggan.getText().toString());
                                    jsonBody.put("alamat", edtAlamat.getText().toString());
                                    jsonBody.put("kota", edtKota.getText().toString());
                                    jsonBody.put("npwp", edtNPWP.getText().toString());
                                    jsonBody.put("telp", edtTelp.getText().toString());
                                    jsonBody.put("fax", edtFax.getText().toString());
                                    jsonBody.put("email", edtEmail.getText().toString());
                                    jsonBody.put("tempo", edtTempo.getText().toString());
                                    jsonBody.put("bank", edtBank.getText().toString());
                                    jsonBody.put("rekening", edtRekening.getText().toString());
                                    jsonBody.put("max_piutang", edtMaxPiutang.getText().toString().replaceAll("[^\\d.]+", ""));
                                    jsonBody.put("cp", edtCP.getText().toString());
                                    jsonBody.put("kode_rayon", idRayon);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ApiVolley insertData = new ApiVolley(getApplicationContext(), jsonBody, method, urlSaveCustomer, "", "", 0, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        JSONObject responseAPI = new JSONObject();
                                        try {

                                            responseAPI = new JSONObject(result);
                                            String message = responseAPI.getJSONObject("metadata").getString("message");
                                            String status = responseAPI.getJSONObject("metadata").getString("status");

                                            if(Integer.parseInt(status) == 201 || Integer.parseInt(status) == 200){ // Success insert SO

                                                kdcus = responseAPI.getJSONObject("response").getJSONObject("dat_customer").getString("kdcus");
                                                saveApprove(kdcus, edtLimit.getText().toString().replaceAll("[,.]", ""));
                                            }else{

                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show(); // show message response
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show(); // show message response
                                        }
                                    }

                                    @Override
                                    public void onError(String result) {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); // show message response
                                    }
                                });
                            }
                        }
                ).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).show();
    }

    public void saveApprove(String kdcus, String jumlah) {

        dialogBox = new DialogBox(CustomerDetail.this);
        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("kdcus", kdcus);
            jBody.put("jumlah", jumlah);
            jBody.put("flag", "1"); // tanda harus pakai limit
        } catch (JSONException e) {
            e.printStackTrace();
        }

        isFromSaveLimit = true;

        ApiVolley apiVolley = new ApiVolley(context, jBody, "POST", ServerURL.sendCustomerLimit, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDetailCustomer() {
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(CustomerDetail.this, jsonBody, "GET", urlGetCustomer + kodeCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){

                                JSONArray jsonArray = responseAPI.getJSONArray("response");
                                for(int i = 0; i < 1; i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    edtKodePelanggan.setText(jo.getString("kdcus"));
                                    edtNamaPelanggan.setText(jo.getString("nama"));
                                    edtAlamat.setText(jo.getString("alamat"));
                                    edtKota.setText(jo.getString("kota"));
                                    edtNPWP.setText(jo.getString("npwp"));
                                    edtTelp.setText(jo.getString("notelp"));
                                    edtFax.setText(jo.getString("nofax"));
                                    edtEmail.setText(jo.getString("email"));
                                    edtTempo.setText(jo.getString("tempo"));
                                    edtBank.setText(jo.getString("bank"));
                                    edtRekening.setText(jo.getString("norekening"));
                                    edtMaxPiutang.setText(jo.getString("max_piutang"));
                                    edtCP.setText(jo.getString("kontak_person"));
                                    spRayon.setSelection(getSpinnerIndex(spRayon, jo.getString("kode_rayon")));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                    }
                });
    }

    private void setSPRayonEntry() {

        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.normal_spinner,rayonList);
        spRayon.setAdapter(adapter);
    }

    private int getSpinnerIndex(Spinner spinner, String kode){

        int index = 0;
        for(int i = 0; i < spinner.getCount();i++){
            Rayon rayon = (Rayon) spinner.getItemAtPosition(i);
            if(rayon.getKode().toLowerCase().equals(kode.toLowerCase())){
                index = i;
                break;
            }
        }
        return index;
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
