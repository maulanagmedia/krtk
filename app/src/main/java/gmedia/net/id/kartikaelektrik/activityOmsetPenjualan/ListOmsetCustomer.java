package gmedia.net.id.kartikaelektrik.activityOmsetPenjualan;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.OmsetPenjualan.OmsetPerCustomerTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SessionManager;

public class ListOmsetCustomer extends AppCompatActivity {

    private String urlGetOmset;
    private Button btnTampilkan;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private TextInputLayout tilTanggalAwal, tilTanggalAkhir;
    private EditText edTanggalAwal, edtTanggalAkhir;
    private AutoCompleteTextView actvNamaPelanggan;
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private String tanggalAwal = "", tanggalAkhir = "";
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private ListView lvListOmset;
    private String formatDate = "", formatDateDisplay = "";
    private TextView tvTotal;
    private SessionManager session;
    private String nik = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_omset_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Omset Customer");

        session = new SessionManager(this);

        initUI();
    }

    private void initUI() {

        urlGetOmset = getResources().getString(R.string.url_get_omset);
        tilTanggalAwal = (TextInputLayout) findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_nama_pelanggan);
        btnTampilkan = (Button) findViewById(R.id.btn_tampilkan);
        lvListOmset = (ListView) findViewById(R.id.lv_list_omset);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);

        edTanggalAwal.setText(iv.getToday(formatDateDisplay));
        edtTanggalAkhir.setText(iv.getToday(formatDateDisplay));

        /*btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTampilkan();
            }
        });

        initValidation();*/

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            nik = bundle.getString("nik", session.getNik());
            tanggalAwal = bundle.getString("tanggalawal");
            tanggalAkhir = bundle.getString("tanggalakhir");

            setListAutocomplete();

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setListAutocomplete();
                }
            });
        }
    }

    private void initValidation() {

        iv.datePickerEvent(ListOmsetCustomer.this, edTanggalAwal, "RIGHT", formatDateDisplay);
        iv.datePickerEvent(ListOmsetCustomer.this, edtTanggalAkhir, "RIGHT", formatDateDisplay);

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTampilkan();
            }
        });
    }

    private void validateTampilkan(){

        // Mondatory
        if(!iv.mondatoryEdittext(tilTanggalAwal, edTanggalAwal, "Tanggal Awal Tidak Boleh Kosong")){
            return;
        }

        if(!iv.mondatoryEdittext(tilTanggalAkhir, edtTanggalAkhir, "Tanggal Akhir Tidak Boleh Kosong")){
            return;
        }

        if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal, formatDateDisplay)){
            tilTanggalAkhir.setErrorEnabled(true);
            tilTanggalAkhir.setError("Tanggal Akhir Tidak Dapat Sebelum Tanggal Awal");
            edtTanggalAkhir.requestFocus();
            return;
        }else{
            tilTanggalAkhir.setError(null);
            tilTanggalAkhir.setErrorEnabled(false);
        }

        tanggalAwal = iv.ChangeFormatDateString(edTanggalAwal.getText().toString(), formatDateDisplay, formatDate);
        tanggalAkhir = iv.ChangeFormatDateString(edtTanggalAkhir.getText().toString(), formatDateDisplay, formatDate);

        setListAutocomplete();
    }

    private void setListAutocomplete() {

        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nik", nik);
            jsonBody.put("flag","RO");
            jsonBody.put("kdcus","");
            jsonBody.put("tgl",tanggalAwal);
            jsonBody.put("tgl2",tanggalAkhir);
            jsonBody.put("nonota","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(ListOmsetCustomer.this, jsonBody, "POST", urlGetOmset, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();
                            double total = 0;

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    masterList.add(new CustomListItem(jo.getString("customer"),
                                            jo.getString("alamat") + ", " + jo.getString("kota"),
                                            iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("max_piutang"))),
                                            iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("piutang"))),
                                            jo.getString("kdcus")));

                                    total += Double.parseDouble(jo.getString("piutang"));

                                }
                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
                            autocompleteList = new ArrayList<CustomListItem>(masterList);
                            tableList= new ArrayList<CustomListItem>(masterList);
                            getListAutocomplete(autocompleteList);
                            getListTable(tableList);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListAutocomplete(null);
                            getListTable(null);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListAutocomplete(null);
                        getListTable(null);
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    // method to show autocomplete item
    private void getListAutocomplete(final List<CustomListItem> listItemCustomer){

        actvNamaPelanggan.setAdapter(null);

        if(listItemCustomer != null && listItemCustomer.size() > 0){
            /*CustomListItemAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new CustomListItemAutocompleteAdapter(ListOmsetCustomer.this,listItemCustomer.size(),listItemCustomer, "L");

            //set adapter to autocomplete
            actvNamaPelanggan.setAdapter(arrayAdapterString);*/

            // Listener event on change autocomplete
            if(firstLoad){
                firstLoad = false;
                actvNamaPelanggan.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(actvNamaPelanggan.length() <= 0){
                            getListTable(tableList);
                        }
                    }
                });
            }

            actvNamaPelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomListItem item = (CustomListItem) adapterView.getItemAtPosition(i);
                    List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                    itemToAdd.add(item);
                    getListTable(itemToAdd);
                }
            });

            actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        String keyword = actvNamaPelanggan.getText().toString();
                        for(CustomListItem item:tableList){
                            if(item.getListItem1().toUpperCase().contains(keyword.trim().toUpperCase())){
                                itemToAdd.add(item);
                            }
                        }
                        getListTable(itemToAdd);
                        iv.hideSoftKey(ListOmsetCustomer.this);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table
    private void getListTable(List<CustomListItem> listItemCustomer){

        lvListOmset.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){

            OmsetPerCustomerTableAdapter arrayAdapterString;
            arrayAdapterString = new OmsetPerCustomerTableAdapter(ListOmsetCustomer.this,listItemCustomer.size(), listItemCustomer, 1);
            lvListOmset.setAdapter(arrayAdapterString);

            lvListOmset.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);
                    String namaCus = item.getListItem1();
                    String kdCus = item.getListItem5();
                    String total = item.getListItem4();

                    Intent intent = new Intent(ListOmsetCustomer.this, DetailOmsetCustomer.class);
                    intent.putExtra("nik", nik);
                    intent.putExtra("nama", namaCus);
                    intent.putExtra("total", total);
                    intent.putExtra("kdcus", kdCus);
                    intent.putExtra("start", tanggalAwal);
                    intent.putExtra("end", tanggalAkhir);

                    startActivity(intent);
                }
            });
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
