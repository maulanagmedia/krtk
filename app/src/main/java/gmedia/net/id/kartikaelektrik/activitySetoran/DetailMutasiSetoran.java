package gmedia.net.id.kartikaelektrik.activitySetoran;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class DetailMutasiSetoran extends AppCompatActivity {

    private SessionManager session;
    private Context context;
    private static ItemValidation iv = new ItemValidation();
    private String formatDate = "", formatDateDisplay = "";
    private EditText edtSales, edtTanggal, edtTotal;
    private Spinner spBankTujuan, spBankSumber;
    private LinearLayout llSaveContainer;
    private TextView tvSave;
    private String kdcus = "", namaCus = "";
    private String crBayar = "";
    private ProgressBar pbLoading;
    private List<OptionItem> listBankSumber, listBankTujuan;
    private String currentString = "";
    private String idSetoran = "";
    private EditText edtDariBank, edtDariNorek, edtKeBank, edtKeNorek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mutasi_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Mutasi Setoran");
        context = this;
        session = new SessionManager(context);

        intiUI();
    }

    private void intiUI() {

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        edtSales = (EditText) findViewById(R.id.edt_sales);
        edtTanggal = (EditText) findViewById(R.id.edt_tanggal);
        spBankSumber = (Spinner) findViewById(R.id.sp_dari_bank);
        edtDariBank = (EditText) findViewById(R.id.edt_dari_bank);
        edtDariNorek = (EditText) findViewById(R.id.edt_dari_norek);
        spBankTujuan = (Spinner) findViewById(R.id.sp_ke_bank);
        edtKeBank = (EditText) findViewById(R.id.edt_ke_bank);
        edtKeNorek = (EditText) findViewById(R.id.edt_ke_norek);
        edtTotal = (EditText) findViewById(R.id.edt_total);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        llSaveContainer = (LinearLayout) findViewById(R.id.ll_save_container);
        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setText("Simpan Mutasi");

        edtSales.setText(session.getFullName());
        initEvent();
    }

    private void initEvent() {

        edtTanggal.setText(iv.getCurrentDate(formatDateDisplay));
        edtTanggal.setKeyListener(null);
        //iv.datePickerEvent(context,edtTanggal,"RIGHT",formatDateDisplay, iv.getCurrentDate(formatDateDisplay));

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

        getDataBankTunai();
    }

    private void getDataBankTunai() {

        pbLoading.setVisibility(View.VISIBLE);
        crBayar = "T";
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
                    listBankSumber = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBankSumber.add(new OptionItem(
                                    jo.getString("kode"),
                                    jo.getString("nama"),
                                    jo.getString("norekening")));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    getDataBank();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    getDataBank();
                }
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                getDataBank();
            }
        });
    }

    private void getDataBank() {

        pbLoading.setVisibility(View.VISIBLE);
        crBayar = "B";
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
                    listBankTujuan = new ArrayList<>();

                    if(status.equals("200")){

                        JSONArray jsonArray = response.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBankTujuan.add(new OptionItem(
                                    jo.getString("kode"),
                                    jo.getString("nama"),
                                    jo.getString("norekening")));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    setBankAdapter(listBankSumber, listBankTujuan);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                    setBankAdapter(null, null);
                }
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                setBankAdapter(null, null);
            }
        });
    }

    private void setBankAdapter(List<OptionItem> listItem1, List<OptionItem> listItem2) {

        spBankSumber.setAdapter(null);
        spBankTujuan.setAdapter(null);

        if(listItem1 != null && listItem1.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem1);
            spBankSumber.setAdapter(adapter);

            spBankSumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OptionItem item = (OptionItem) parent.getItemAtPosition(position);
                    edtDariBank.setText(item.getText());
                    edtDariNorek.setText(item.getAtt1());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        if(listItem2 != null && listItem2.size() > 0){

            ArrayAdapter adapter2 = new ArrayAdapter(context, R.layout.normal_spinner, listItem2);
            spBankTujuan.setAdapter(adapter2);

            spBankTujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    OptionItem item = (OptionItem) parent.getItemAtPosition(position);
                    edtKeBank.setText(item.getText());
                    edtKeNorek.setText(item.getAtt1());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void validateBeforeSave(){

        if(spBankSumber.getAdapter() == null){

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
                .setIcon(R.mipmap.kartika_logo)
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

        String namaBankSumber = ((OptionItem) spBankSumber.getSelectedItem()).getText();
        String kodeBankSumber = ((OptionItem) spBankSumber.getSelectedItem()).getValue();

        String namaBankTujuan = ((OptionItem) spBankTujuan.getSelectedItem()).getText();
        String kodeBankTujuan = ((OptionItem) spBankTujuan.getSelectedItem()).getValue();

        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("dari_kode_bank", kodeBankSumber);
            jsonBody.put("dari_nama_bank", edtDariBank.getText().toString());
            jsonBody.put("dari_rekening_bank", edtDariNorek.getText().toString());
            jsonBody.put("ke_kode_bank", kodeBankTujuan);
            jsonBody.put("ke_nama_bank", edtKeBank.getText().toString());
            jsonBody.put("ke_rekening_bank", edtKeNorek.getText().toString());
            jsonBody.put("total", edtTotal.getText().toString().replaceAll("[,.]", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley apiVolley = new ApiVolley(getApplicationContext(), jsonBody, "POST", ServerURL.saveMutasiSetoran, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                llSaveContainer.setEnabled(true);
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

                llSaveContainer.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
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
