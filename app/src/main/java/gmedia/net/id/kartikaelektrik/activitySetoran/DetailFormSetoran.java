package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.OptionItem;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class DetailFormSetoran extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";
    private EditText edtSales, edtCustomer, edtTanggal, edtTotal;
    private RadioGroup rgCaraBayar;
    private RadioButton rbTunai, rbBank, rbGiro;
    private Spinner spBank;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    private String kdcus = "", namaCus = "";
    private String crBayar = "";
    private ProgressBar pbLoading;
    private List<OptionItem> listBank;
    private String currentString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Setoran");
        context = this;
        session = new SessionManager(context);

        intiUI();
    }

    private void intiUI() {

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        edtSales = (EditText) findViewById(R.id.edt_sales);
        edtCustomer = (EditText) findViewById(R.id.edt_customer);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        rgCaraBayar = (RadioGroup) findViewById(R.id.rg_crbayar);
        rbTunai = (RadioButton) findViewById(R.id.rb_tunai);
        rbBank = (RadioButton) findViewById(R.id.rb_bank);
        rbGiro = (RadioButton) findViewById(R.id.rb_giro);
        spBank = (Spinner) findViewById(R.id.sp_bank);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setText("Simpan Setoran");

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdcus = bundle.getString("kdcus", "");
            namaCus = bundle.getString("namacus", "");
            edtSales.setText(session.getUser());
            edtCustomer.setText(namaCus);

            initEvent();
        }
    }

    private void initEvent() {

        edtTanggal.setText(iv.getCurrentDate(formatDateDisplay));
        edtTanggal.setKeyListener(null);
        //iv.datePickerEvent(context,edtTanggal,"RIGHT",formatDateDisplay, iv.getCurrentDate(formatDateDisplay));

        rgCaraBayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                getDataBank();
            }
        });

        edtTotal.addTextChangedListener(new TextWatcher() {

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
                    edtTotal.removeTextChangedListener(this);

                    String formatted = iv.ChangeToCurrencyFormat(cleanString);

                    currentString = formatted;
                    edtTotal.setText(formatted);
                    edtTotal.setSelection(formatted.length());
                    edtTotal.addTextChangedListener(this);
                }
            }
        });

        llSaveContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateBeforeSave();
            }
        });

        getDataBank();
    }

    private void getDataBank() {

        pbLoading.setVisibility(View.VISIBLE);

        if(rbTunai.isChecked()){
            crBayar = "T";
        }else if(rbBank.isChecked()){
            crBayar = "B";
        }else{
            crBayar = "G";
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("cara_bayar", crBayar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getMasterBayar, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listBank = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBank.add(new OptionItem(jo.getString("kode"), jo.getString("nama")));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    setBankAdapter(listBank);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setBankAdapter(null);
                }

            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setBankAdapter(null);
            }
        });
    }

    private void validateBeforeSave(){

        if(spBank.getAdapter() == null){

            Toast.makeText(context, "Tunggu hingga data termuat",Toast.LENGTH_LONG).show();
            return;
        }

        if(edtTotal.getText().toString().isEmpty()){

            edtTotal.setError("Total harap diisi");
            edtTotal.requestFocus();
            return;
        }else{
            edtTotal.setError(null);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Apakah anda yakin ingin menyimpan setoran?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveData();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void saveData() {

        llSaveContainer.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(context,
                gmedia.net.id.kartikaelektrik.R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.show();

        String namaBank = ((OptionItem) spBank.getSelectedItem()).getText();
        String kodeBank = ((OptionItem) spBank.getSelectedItem()).getValue();

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("crbayar", crBayar);
            jsonBody.put("kode_bank", kodeBank);
            jsonBody.put("bank", namaBank);
            jsonBody.put("kdcus", kdcus);
            jsonBody.put("total", edtTotal.getText().toString().replaceAll("[,.]", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jsonBody, "POST", ServerURL.saveSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                progressDialog.dismiss();
                JSONObject responseAPI = new JSONObject();
                try {

                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    String message = responseAPI.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if(Integer.parseInt(status) == 200){

                        onBackPressed();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {

                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setBankAdapter(List<OptionItem> listItem) {

        spBank.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spBank.setAdapter(adapter);

        }
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
