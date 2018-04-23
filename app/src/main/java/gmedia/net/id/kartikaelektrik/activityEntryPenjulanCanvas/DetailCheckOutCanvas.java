package gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gmedia.net.id.kartikaelektrik.DashboardContainer;
import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomerOrder.ListBarangSalesOrder;
import gmedia.net.id.kartikaelektrik.activitySalesOrderDetail.DetailSalesOrder;
import gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas.ListBarangDetailCOAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.model.SalesOrderDetail;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;

public class DetailCheckOutCanvas extends AppCompatActivity {

    private static String noBukti = "";
    private static String kdCus;
    private static String customerName = "", tanggal = "", tanggalTempo = "";
    private static LinearLayout llAddOrder;
    private LinearLayout llHapusCanvasOrder;
    private static TextView tvTotal;
    private static AutoCompleteTextView actvNamaBarang;
    private static ListView lvBarang;
    private String urlGetCODetail;
    private String urlDeleteCO;
    private String urlGetCustomer;
    private List<SalesOrderDetail> masterListSO, listBarangAutocomplete;
    private static List<CustomListItem> masterList;
    private static List<CustomListItem> autocompleteList;
    private static List<CustomListItem> tableList;
    private List<SalesOrderDetail> listBarangTable;
    private String TAG = "Detail.Sales.Order";
    private static ItemValidation iv = new ItemValidation();
    private static boolean firstLoad = true;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private static LinearLayout llSelesai;
    private static List<CustomListItem> selectedBarangList;
    private static String urlSaveCO = "";
    private static Double total = Double.valueOf(0);
    private boolean flagBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_check_out_canvas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        Bundle extras = getIntent().getExtras();

        // API List
        urlGetCODetail = getResources().getString(R.string.url_get_so_detail_by_id);
        urlDeleteCO = getResources().getString(R.string.url_delete_so_by_id);
        urlGetCustomer = getResources().getString(R.string.url_get_customer_by_id);
        urlSaveCO = getResources().getString(R.string.url_insert_canvas_order);

        tvTotal = (TextView) findViewById(R.id.tv_total);
        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        lvBarang = (ListView) findViewById(R.id.lv_barang);
        llAddOrder = (LinearLayout) findViewById(R.id.ll_add_order);
        llSelesai = (LinearLayout) findViewById(R.id.ll_selesai);
        llHapusCanvasOrder = (LinearLayout) findViewById(R.id.ll_hapus_canvas);

        // Progres Bar
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        if (extras != null){

            noBukti = extras.getString("nobukti");
            /*tvTitleText.setText(String.format(getResources().getString(R.string.sales_order),noBukti));
            tvTitleText.setPaintFlags(tvTitleText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
            setTitle("Order Canvas " + noBukti);
            kdCus = extras.getString("kdcus");
            customerName = extras.getString("nama");
            tanggal = extras.getString("tanggal");
            tanggalTempo = extras.getString("tanggaltempo");

            selectedBarangList = new ArrayList<>();

            // Tambahkan barang baru yang telah dibuat
            String barangString = extras.getString("barang");
            String barangListString = extras.getString("selectedbarang");
            Type tipeBarang = new TypeToken<CustomListItem>(){}.getType();
            Type tipeBarangList = new TypeToken<List<CustomListItem>>(){}.getType();
            Gson gson = new Gson();
            CustomListItem barangBaru = gson.fromJson(barangString, tipeBarang);

            if(barangListString != null && barangListString.length() > 0){
                selectedBarangList = gson.fromJson(barangListString, tipeBarangList);
            }

            if(extras.getString("updatestatus") != null && extras.getString("updatestatus").length() >0){

                int index = iv.parseNullInteger(extras.getString("updatestatus"));
                selectedBarangList.set(index, barangBaru );
            }else{
                selectedBarangList.add(barangBaru);
            }
        }

        getAllData(DetailCheckOutCanvas.this);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllData(DetailCheckOutCanvas.this);
            }
        });
    }

    public static void deleteSelectedBarang(Context context, int position) {
        selectedBarangList.remove(position);
        getAllData(context);
    }

    private static void getAllData(Context context) {

        masterList = new ArrayList<>(selectedBarangList);
        total = Double.valueOf(0);
        for(CustomListItem item: selectedBarangList){

            total += iv.parseNullDouble(item.getListItem12());
        }
        tvTotal.setText(iv.ChangeToRupiahFormat(total));
        autocompleteList = new ArrayList<CustomListItem>(masterList);
        tableList = new ArrayList<CustomListItem>(masterList);
        getListBarangAutocomplete(context, autocompleteList);
        getListBarangTable(context, tableList);
        setButtonAction(context);

    }

    private static void getListBarangAutocomplete(Context context, List<CustomListItem> listItems){

        actvNamaBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            /*ListBarangDetailSOAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new ListBarangDetailSOAutocompleteAdapter(DetailSalesOrder.this,listItems.size(),listItems);

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);*/
            setAutocompleteEvent(context);
        }
    }

    private static void setAutocompleteEvent(final Context context){

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
                        getAllData(context);
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

                        if(item.getListItem2().toUpperCase().contains(keyword)){
                            items.add(item);
                        }
                    }

                    getListBarangTable(context, items);
                    return true;
                }
                return false;
            }
        });
    }

    private static void getListBarangTable(Context context, List<CustomListItem> listItems){

        lvBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            ListBarangDetailCOAdapter arrayAdapterString;

            //set adapter for autocomplete
            HashMap<String, String> stringList = new HashMap<String, String>();

            stringList.put("nobukti", noBukti);
            stringList.put("kdcus", noBukti);
            stringList.put("namacus", customerName);
            Gson gson = new Gson();
            stringList.put("selectedbarang", gson.toJson(selectedBarangList).toString());

            arrayAdapterString = new ListBarangDetailCOAdapter((Activity) context, listItems, stringList);

            //set adapter to autocomplete
            lvBarang.setAdapter(arrayAdapterString);
        }
    }

    private void deleteData(String noBuktiSalesOrder){

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(DetailCheckOutCanvas.this, jsonBody, "DELETE", urlDeleteCO +noBuktiSalesOrder, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            int statusNumber = Integer.parseInt(statusI);
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(DetailCheckOutCanvas.this, dialog, Toast.LENGTH_LONG).show(); // show message response
                            if(statusNumber == 200){

                                //backToSOList();
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

    private static void setButtonAction(final Context context){
        // Tambah Order
        llAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DetailEntryBarangCanvas.class);
                intent.putExtra("nobukti", noBukti);
                Gson gson = new Gson();
                intent.putExtra("selectedbarang", gson.toJson(selectedBarangList).toString());
                intent.putExtra("kdcus", kdCus);
                intent.putExtra("nama", customerName);
                context.startActivity(intent);
                //((Activity)context).finish();
            }
        });

        llSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog builder = new AlertDialog.Builder(context)
                        .setTitle("Peringatan")
                        .setMessage("Order yang telah masuk tidak dapat diubah, apakah anda yakin akan memproses order?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                simpanData(context);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
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

    private static void simpanData(final Context context) {

        // Header
        JSONArray header = new JSONArray();
        JSONObject header0 = new JSONObject();
        try {
            header0.put("nobukti", noBukti);
            header0.put("kdcus", kdCus);
            header0.put("tgl", tanggal);
            header0.put("tgltempo", tanggalTempo);
            header0.put("total", total);
            header0.put("totalhpp", "");
            header0.put("flag", "B");
            header0.put("status", "3");
            header0.put("kdgudang", "13");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        header.put(header0);

        // Detail
        JSONArray detail = new JSONArray();
        JSONObject detail0 = new JSONObject();
        for(CustomListItem item: selectedBarangList){
            detail0 = new JSONObject();
            try {
                detail0.put("nobukti", noBukti);
                detail0.put("kdbrg", item.getListItem1());
                detail0.put("harga", item.getListItem6());
                detail0.put("diskon", item.getListItem10());
                detail0.put("jumlah", item.getListItem3());
                detail0.put("satuan", item.getListItem4());
                detail0.put("hpp", "");
                detail0.put("jumlahpcs", item.getListItem7());
                detail0.put("hargapcs", item.getListItem9());
                detail0.put("total", item.getListItem12());
                detail0.put("totalhpp", "");
                detail0.put("hargadiskon", item.getListItem11());
                detail0.put("nokonsinyasi", item.getListItem8());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            detail.put(detail0);
        }

        header.put(header0);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("header", header);
            jsonBody.put("detail", detail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(context, jsonBody, "POST", urlSaveCO, "", "", 0,

                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            String dialog = responseAPI.getJSONObject("metadata").getString("message");
                            Toast.makeText(context, dialog, Toast.LENGTH_LONG).show(); // show message response
                            if(iv.parseNullInteger(statusI) == 200){

                                new LocationUpdateHandler(context,"Tambah Order "+ noBukti);
                                Intent intent = new Intent(context, DashboardContainer.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("kodemenu","entrycanvas");
                                context.startActivity(intent);
                                ((Activity)context).finish();
                                //backToSOList();
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
                flagBack = false;
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(flagBack){
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }else{

            AlertDialog builder = new AlertDialog.Builder(DetailCheckOutCanvas.this)
                    .setTitle("Peringatan")
                    .setMessage("Anda menekan tombol kembali dan Order anda belum tersimpan, anda yakin ingin menghapus data Order?")
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            flagBack = true;
                            onBackPressed();
                        }
                    }).show();
        }
    }
}
