package gmedia.net.id.kartikaelektrik.ActivityRetur;

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
import gmedia.net.id.kartikaelektrik.activityPiutang.DetailPiutangPerNota;
import gmedia.net.id.kartikaelektrik.activityPiutang.SubDetailPiutangJatuhTempo;
import gmedia.net.id.kartikaelektrik.adapter.CustomListItemAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.adapter.Retur.ReturCustomerTableAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class ReturCustomer extends AppCompatActivity {

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
    private String urlGetRetur = "";
    private ListView lvListRetur;
    private String formatDate = "", formatDateDisplay = "";
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retur_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Retur Customer");
        formatDate = getResources().getString(R.string.format_date);
        formatDateDisplay = getResources().getString(R.string.format_date_display);
        urlGetRetur = getResources().getString(R.string.url_get_retur);
        tilTanggalAwal = (TextInputLayout) findViewById(R.id.til_tanggal_awal);
        tilTanggalAkhir = (TextInputLayout) findViewById(R.id.til_tanggal_akhir);
        edTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        actvNamaPelanggan = (AutoCompleteTextView) findViewById(R.id.actv_nama_pelanggan);
        btnTampilkan = (Button) findViewById(R.id.btn_tampilkan);
        lvListRetur = (ListView) findViewById(R.id.lv_list_retur);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        edTanggalAwal.setText(iv.getToday(formatDateDisplay));
        //tanggalTempo = iv.sumDate(iv.getToday(formatDate),37,formatDate);
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

        iv.datePickerEvent(ReturCustomer.this, edTanggalAwal, "RIGHT", formatDateDisplay);
        iv.datePickerEvent(ReturCustomer.this, edtTanggalAkhir, "RIGHT", formatDateDisplay);

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

        if(!iv.isMoreThanCurrentDate(edtTanggalAkhir,edTanggalAwal,formatDateDisplay)){
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
            jsonBody.put("flag","BP");
            jsonBody.put("start",tanggalAwal);
            jsonBody.put("end",tanggalAkhir);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(ReturCustomer.this, jsonBody, "POST", urlGetRetur, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();
                            autocompleteList = new ArrayList<CustomListItem>();

                            String namaCustomer = "";
                            double total = 0;

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);

                                    if(!namaCustomer.equals(jo.getString("customer"))){
                                        autocompleteList.add(new CustomListItem(jo.getString("customer"),""));
                                        masterList.add(new CustomListItem("HEADER", jo.getString("customer"),""));
                                        namaCustomer = jo.getString("customer");
                                    }

                                    masterList.add(new CustomListItem(jo.getString("nobukti"),
                                            jo.getString("customer"),
                                            iv.ChangeFormatDateString(jo.getString("tgl"), formatDate, formatDateDisplay),
                                            jo.getString("ppn"),
                                            jo.getString("diskon"),
                                            iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("total"))),
                                            jo.getString("keterangan")));

                                    total += Double.parseDouble(jo.getString("total"));

                                }
                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
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
            arrayAdapterString = new CustomListItemAutocompleteAdapter(ReturCustomer.this,listItemCustomer.size(),listItemCustomer, "L");

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
                    String nama = item.getListItem1();

                    List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                    for (CustomListItem itemList: tableList){

                        String namaCustomer = itemList.getListItem2();
                        if(nama.trim().equals(namaCustomer.trim())){
                            itemToAdd.add(itemList);
                        }
                    }
                    getListTable(itemToAdd);
                }
            });

            actvNamaPelanggan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<CustomListItem> itemToAdd = new ArrayList<CustomListItem>();
                        String keyword = actvNamaPelanggan.getText().toString().toUpperCase();
                        for (CustomListItem itemList: tableList){

                            String namaCustomer = itemList.getListItem2().toUpperCase();
                            if(namaCustomer.contains(keyword)){
                                itemToAdd.add(itemList);
                            }
                        }
                        getListTable(itemToAdd);
                        iv.hideSoftKey(ReturCustomer.this);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    // method to show table
    private void getListTable(List<CustomListItem> listItemCustomer){

        lvListRetur.setAdapter(null);

        if (listItemCustomer != null && listItemCustomer.size() > 0){

            ReturCustomerTableAdapter arrayAdapterString;
            arrayAdapterString = new ReturCustomerTableAdapter(ReturCustomer.this,listItemCustomer.size(),listItemCustomer);
            lvListRetur.setAdapter(arrayAdapterString);

            lvListRetur.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    CustomListItem item = (CustomListItem) parent.getItemAtPosition(position);

                    if(!item.getListItem1().equals("HEADER")){
                        Intent intent = new Intent(ReturCustomer.this, DetailBarangRetur.class);
                        intent.putExtra("nobukti", item.getListItem1());
                        intent.putExtra("namacus", item.getListItem2());
                        intent.putExtra("total", item.getListItem6());
                        startActivity(intent);
                    }
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
