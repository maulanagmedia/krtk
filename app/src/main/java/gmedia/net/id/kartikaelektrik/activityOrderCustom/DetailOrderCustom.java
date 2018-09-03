package gmedia.net.id.kartikaelektrik.activityOrderCustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomOrder.ListCustomOrderAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailOrderCustom extends AppCompatActivity {


    private AutoCompleteTextView actvNamaBarang;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private ListView lvBarang;
    private TextView tvTotal;
    private LinearLayout llAdd;
    private LinearLayout llHapus;
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private String kdCus = "", namaCus = "", noBukti = "", urlGetOC = "", urlgetOCHeader = "", urlDeleteOC = "", statusSO = "", flag = "";
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private boolean dataLoaded = false;
    private String tanggal = "", tanggalTempo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_custom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        urlGetOC = getResources().getString(R.string.url_get_custom_detail);
        urlgetOCHeader = getResources().getString(R.string.url_get_custom_header);
        urlDeleteOC = getResources().getString(R.string.url_delete_oc);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        llAdd = (LinearLayout) findViewById(R.id.ll_add_order);
        llHapus = (LinearLayout) findViewById(R.id.ll_hapus);

        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            kdCus = bundle.getString("kdcus");
            namaCus = bundle.getString("nama");
            noBukti = bundle.getString("nobukti");

            setTitle("Order Custom "+ noBukti);
            getOCHeader();
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOCHeader();
                }
            });
        }
    }

    private void getOCHeader() {

        // Get Detail Barang
        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlgetOCHeader + noBukti, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        masterList = new ArrayList<>();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){

                                JSONArray jArray = responseAPI.getJSONArray("response");

                                for(int i = 0; i < jArray.length(); i++){

                                    JSONObject jo = jArray.getJSONObject(i);

                                    tanggal = jo.getString("tgl");
                                    tanggalTempo = jo.getString("tgltempo");
                                    statusSO = jo.getString("status");
                                    flag = jo.getString("flag");
                                    tvTotal.setText(iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("total"))));
                                }
                                getDetailOC();
                                setButtonAction();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    private void getDetailOC() {

        // Get Detail Barang
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");
        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetOC + noBukti, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        masterList = new ArrayList<>();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){

                                JSONArray jArray = responseAPI.getJSONArray("response");

                                for(int i = 0; i < jArray.length(); i++){

                                    JSONObject jo = jArray.getJSONObject(i);

                                    masterList.add(new CustomListItem(jo.getString("keterangan"), jo.getString("harga"), jo.getString("jumlah"), jo.getString("satuan"), jo.getString("diskon"), jo.getString("hargadiskon"), jo.getString("total"), jo.getString("id"), jo.getString("nobukti"), jo.getString("kdbrg"), jo.getString("namabrg"), jo.getString("ukuran"), jo.getString("crbayar"), jo.getString("satuanbarang")));
                                }

                                autocompleteList = new ArrayList<>(masterList);
                                tableList = new ArrayList<>(masterList);

                                getAutocomplete(autocompleteList);
                                getTable(tableList);

                            }else{
                                getAutocomplete(null);
                                getTable(null);
                            }

                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                        }catch (Exception e){
                            e.printStackTrace();
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                            getAutocomplete(null);
                            getTable(null);
                        }
                    }

                    @Override
                    public void onError(String result) {

                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                        getAutocomplete(null);
                        getTable(null);
                    }
                });
    }

    private void getAutocomplete(List<CustomListItem> listItems){

        actvNamaBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            /*ListBarangDetailSOAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new ListBarangDetailSOAutocompleteAdapter(DetailSalesOrder.this,listItems.size(),listItems);

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);*/
            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){
        if(firstLoad){
            firstLoad = false;
            actvNamaBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNamaBarang.getText().length() <= 0){
                        //getTable(masterList);
                        getOCHeader();
                    }
                }
            });
        }

        actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    String keyword = actvNamaBarang.getText().toString().trim().toUpperCase();

                    for(CustomListItem item: masterList){

                        if(item.getListItem1().toUpperCase().contains(keyword)){
                            items.add(item);
                        }
                    }

                    getTable(items);
                    iv.hideSoftKey(DetailOrderCustom.this);
                    return true;
                }
                return false;
            }
        });
    }

    private void getTable(List<CustomListItem> listItems){

        lvBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            ListCustomOrderAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListCustomOrderAdapter(DetailOrderCustom.this, listItems, namaCus, kdCus, statusSO, tanggal, tanggalTempo, flag);

            //set adapter to autocomplete
            lvBarang.setAdapter(arrayAdapterString);

        }
    }

    private void setButtonAction(){
        // Tambah Order
        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(iv.parseNullInteger(statusSO) == 3 || iv.parseNullInteger(statusSO) == 7 || iv.parseNullInteger(statusSO) == 9){
                    String peringatan = "Tidak Dapat Menanbah Order";
                    switch (iv.parseNullInteger(statusSO)){
                        case 3:
                            peringatan = "Tidak dapat menambah Order yang telah disetujui";
                            break;
                        case 7:
                            peringatan = "Tidak dapat menambah Order yang telah di Posting";
                            break;
                        case 9:
                            peringatan = "Tidak dapat menambah Order yang ditolak";
                            break;
                    }
                    Toast.makeText(DetailOrderCustom.this, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(DetailOrderCustom.this, ListBarangCustom.class);
                intent.putExtra("nobukti", noBukti);
                intent.putExtra("kdcus",kdCus);
                intent.putExtra("nama",namaCus);
                intent.putExtra("status",statusSO);
                intent.putExtra("tgl",tanggal);
                intent.putExtra("tgltempo",tanggalTempo);
                intent.putExtra("flag",flag);
                startActivity(intent);
            }
        });

        // Delete Sales Order
        llHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(iv.parseNullInteger(statusSO) == 3 || iv.parseNullInteger(statusSO) == 7 || iv.parseNullInteger(statusSO) == 9){
                    String peringatan = "Tidak Dapat Menghapus Order";
                    switch (iv.parseNullInteger(statusSO)){
                        case 3:
                            peringatan = "Tidak dapat menghapus Order yang telah disetujui";
                            break;
                        case 7:
                            peringatan = "Tidak dapat menghapus Order yang telah di Posting";
                            break;
                        case 9:
                            peringatan = "Tidak dapat menghapus Order yang ditolak";
                            break;
                    }
                    Toast.makeText(DetailOrderCustom.this, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(DetailOrderCustom.this).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus " + noBukti +" ?").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteData();
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
    }

    private void deleteData(){

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(DetailOrderCustom.this, jsonBody, "DELETE", urlDeleteOC+noBukti, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            int statusNumber = iv.parseNullInteger(statusI);
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(DetailOrderCustom.this, dialog, Toast.LENGTH_LONG).show(); // show message response
                            if(statusNumber == 200){
                                backToOCList();
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

        backToOCList();
    }

    private void backToOCList(){
        Intent intent = new Intent(DetailOrderCustom.this, DashboardContainer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("kodemenu","ordercustom");
        finish();
        startActivity(intent);
//        DetailSalesOrder.this.overridePendingTransition(R.anim.back_activity_in, R.anim.back_activity_out);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
