package gmedia.net.id.kartikaelektrik.activitySOKhusus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activitySOKhusus.Adapter.ListOrderKhususAdapter;
import gmedia.net.id.kartikaelektrik.dialogFragment.UpdateSalesOrderDF;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class DetailSalesOrderKh extends AppCompatActivity {

    private String noSalesOrder, kdCustomer, tanggal, tanggalTempo, customerName = "";
    private LinearLayout llAddOrder, llHapusSO;
    private TextView tvTotalSO;
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvListBarangOrder;
    private String urlGetSODetail;
    private String urlDeleteSO;
    private String urlGetCustomer;
    private List<SalesOrderDetail> masterListSO, listBarangAutocomplete;
    private List<SalesOrderDetail> listBarangTable;
    private String TAG = "Detail.Sales.Order";
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private String statusSO;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private boolean paketMode = false;
    private String tempo = "", idTempo;
    private Activity activity;
    private boolean flagFromList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sales_order_kh);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        activity = this;
        initUI();
    }

    private void initUI() {

        Bundle extras = getIntent().getExtras();

        // API List
        urlGetSODetail = ServerURL.getBarangDetail;
        urlDeleteSO = ServerURL.deleteSOById;
        urlGetCustomer = ServerURL.getCustomer;

        tvTotalSO = (TextView) findViewById(R.id.tv_total_so);
        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_list_nama_barang);
        lvListBarangOrder = (ListView) findViewById(R.id.lv_barang_order);
        llAddOrder = (LinearLayout) findViewById(R.id.ll_add_order);
        llHapusSO = (LinearLayout) findViewById(R.id.ll_hapus_so);

        // Progres Bar
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        if (extras != null){

            noSalesOrder = extras.getString("nosalesorder");
            statusSO = extras.getString("status");
            flagFromList = extras.getBoolean("flag", false);
            /*tvTitleText.setText(String.format(getResources().getString(R.string.sales_order),noSalesOrder));
            tvTitleText.setPaintFlags(tvTitleText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
            this.setTitle(String.format(getResources().getString(R.string.sales_order),noSalesOrder));
            if(extras.getBoolean("paket",false)) paketMode = true;
        }

        setDetailSO();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDetailSO();
            }
        });
    }

    private void setDetailSO() {

        /*
         * Access the detail SO
         */

        // Get Detail Barang
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");
        final JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(this, jsonBody, "GET", urlGetSODetail + noSalesOrder, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            JSONObject responseJSON = responseAPI.getJSONObject("response");
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterListSO = new ArrayList<SalesOrderDetail>();

                            if(Integer.parseInt(status) == 200){

                                // Get Header
                                try{
                                    kdCustomer = responseJSON.getJSONObject("so_header").getString("kdcus");
                                    tanggal = responseJSON.getJSONObject("so_header").getString("tgl");
                                    tanggalTempo = responseJSON.getJSONObject("so_header").getString("tgltempo");
                                    tempo = responseJSON.getJSONObject("so_header").getString("tempo");
                                    idTempo = responseJSON.getJSONObject("so_header").getString("id_tempo");
                                    tvTotalSO.setText(iv.ChangeToRupiahFormat(Float.parseFloat(responseJSON.getJSONObject("so_header").getString("total"))));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                // Get Detail
                                JSONArray jsonArray = responseJSON.getJSONArray("so_detail");

                                for(int i = 0; i < jsonArray.length(); i++){

                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    SalesOrderDetail sod = new SalesOrderDetail(
                                            jo.getString("id"),
                                            jo.getString("kdbrg"),
                                            jo.getString("namabrg"),
                                            jo.getString("jumlah"),
                                            jo.getString("satuan"),
                                            jo.getString("total"),
                                            jo.getString("terkirim"),
                                            jo.getString("sisa")
                                    );

                                    sod.setHarga(jo.getString("harga"));
                                    sod.setHargapcs(jo.getString("hargapcs"));
                                    sod.setDiskon(jo.getString("diskon"));
                                    sod.setHargaNetto(jo.getString("hargadiskon"));
                                    sod.setStatus(responseJSON.getJSONObject("so_header").getString("status"));
                                    sod.setKdPaket(jo.getString("kdpaket"));
                                    sod.setNamaPaket(jo.getString("nama_paket"));
                                    sod.setJensiPaket(jo.getString("jenis_paket"));
                                    masterListSO.add(sod);
                                }

                                listBarangAutocomplete = new ArrayList<SalesOrderDetail>(masterListSO);
                                listBarangTable = new ArrayList<SalesOrderDetail>(masterListSO);

                                getListBarangAutocomplete(listBarangAutocomplete);
                                getCustomerName();
                                setDeleteButtonEvent();
                            }else{
                                getListBarangAutocomplete(null);
                                getListBarangTable(null);
                            }

                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                        }catch (Exception e){
                            e.printStackTrace();
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                        }
                    }

                    @Override
                    public void onError(String result) {

                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    private void getListBarangAutocomplete(List<SalesOrderDetail> listItems){

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
                        setDetailSO();
                    }
                }
            });
        }

        actvNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                SalesOrderDetail sod = (SalesOrderDetail) adapterView.getItemAtPosition(i);
                List<SalesOrderDetail> items = new ArrayList<SalesOrderDetail>();
                items.add(sod);
                getListBarangTable(items);
            }
        });

        actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<SalesOrderDetail> items = new ArrayList<SalesOrderDetail>();
                    String keyword = actvNamaBarang.getText().toString().trim().toUpperCase();

                    for(SalesOrderDetail sod_item: masterListSO){

                        if(sod_item.getNamaBarang().toUpperCase().contains(keyword)){
                            items.add(sod_item);
                        }
                    }

                    getListBarangTable(items);
                    iv.hideSoftKey(activity);
                    return true;
                }
                return false;
            }
        });
    }

    private void getListBarangTable(List<SalesOrderDetail> listItems){

        lvListBarangOrder.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            ListOrderKhususAdapter arrayAdapterString;

            //set adapter for autocomplete
            HashMap<String, String> stringList = new HashMap<String, String>();

            stringList.put("noSalesOrder", noSalesOrder);
            stringList.put("namaPelanggan", customerName);
            stringList.put("kdCus", kdCustomer);
            stringList.put("tanggaltempo", tanggalTempo);
            stringList.put("tempo", tempo);
            stringList.put("idTempo", idTempo);

            arrayAdapterString = new ListOrderKhususAdapter(activity, listItems, stringList, statusSO);

            //set adapter to autocomplete
            lvListBarangOrder.setAdapter(arrayAdapterString);

            lvListBarangOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    SalesOrderDetail salesOrderDetail = (SalesOrderDetail) adapterView.getItemAtPosition(i);

                }
            });
        }
    }

    private void deleteData(String noBuktiSalesOrder){

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(activity, jsonBody, "DELETE", urlDeleteSO+noBuktiSalesOrder, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            int statusNumber = Integer.parseInt(statusI);
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(activity, dialog, Toast.LENGTH_LONG).show(); // show message response
                            if(statusNumber == 200){
                                backToSOList();
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

    private void getCustomerName(){

        // Get Detail Barang
        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(activity, jsonBody, "GET", urlGetCustomer + kdCustomer, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(Integer.parseInt(status) == 200){

                                JSONArray jsonArray = responseAPI.getJSONArray("response");
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    customerName = jo.getString("nama");
                                }
                                getListBarangTable(listBarangTable);
                                setButtonAction();
                            }
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                        }catch (Exception e){
                            e.printStackTrace();
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }

    private void setButtonAction(){
        // Tambah Order
        llAddOrder.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(activity, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                CharSequence options[] = new CharSequence[] {"Barang Tunggal"};

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailSalesOrderKh.this);
                builder.setCancelable(false);
                builder.setTitle("Jenis Barang");
                builder.setIcon(R.mipmap.kartika_logo);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, ListBarangSalesOrderKh.class);
                        intent.putExtra("noSalesOrder", noSalesOrder);
                        intent.putExtra("kdCus",kdCustomer);
                        intent.putExtra("namaPelanggan",customerName);
                        intent.putExtra("tempo",tempo);
                        intent.putExtra("idTempo",idTempo);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the user clicked on Cancel
                    }
                });
                builder.show();

                /*if(paketMode){


                }else{

                }*/
            }
        });

        // Edit SO
        /*llUbahTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateSODialog(customerName);
            }
        });*/
    }

    private void showUpdateSODialog(String namaPelanggan) {

        // Show the dialog (Modal)
        FragmentManager fm = getSupportFragmentManager();
        final UpdateSalesOrderDF editNameDialogFragment = UpdateSalesOrderDF.newInstance(noSalesOrder,kdCustomer,namaPelanggan,tanggal,tanggalTempo);
        editNameDialogFragment.show(fm, "fr_update_so");

        editNameDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setDetailSO();
            }
        });
    }

    private void setDeleteButtonEvent(){

        // Delete Sales Order
        llHapusSO.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(activity, peringatan, Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(activity).setTitle("Peringatan").setMessage("Apakah anda yakin ingin menghapus " + noSalesOrder +" ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteData(noSalesOrder);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
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

        if(flagFromList){
            super.onBackPressed();
        }else{
            backToSOList();
        }

    }

    private void backToSOList(){
        Intent intent = new Intent(activity, DashboardContainer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("kodemenu","daftarso");
        finish();
        startActivity(intent);
//        DetailSalesOrder.this.overridePendingTransition(R.anim.back_activity_in, R.anim.back_activity_out);
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
