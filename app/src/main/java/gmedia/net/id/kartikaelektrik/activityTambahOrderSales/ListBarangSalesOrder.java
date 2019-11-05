package gmedia.net.id.kartikaelektrik.activityTambahOrderSales;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

public class ListBarangSalesOrder extends AppCompatActivity {

    private AutoCompleteTextView actvListBarang;
    private String TAG = "BarangOrder";
    private ListView lvListBarang;
    private ItemValidation iv = new ItemValidation();
    private String kdCus = "", namaPelanggan = "", kodeKategoriBarang = "0", namaKategoriBarang = "", kodeBarang = "", namaBarang = "", noSalesOrder, tempo = "", idTempo = "";

    //Fragment Detail Barang
    private String urlGetAllBarangByKategori = "";
    private List<Barang> listMasterBarang, moreList;
    private ProgressBar pgbLoadBarang;
    private LinearLayout llProgressBarContainer;
    private Button btnRefreshBarang;
    private boolean firstLoadBarang = true;
    private int start = 0, count = 10;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private ListBarangTableAdapter arrayAdapterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang_sales_order);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pilih Barang Order");

        initUI();
    }

    private void initUI() {

        urlGetAllBarangByKategori = ServerURL.getBarangPerKategori;
        actvListBarang = (AutoCompleteTextView) findViewById(R.id.actv_list_nama_barang);
        lvListBarang = (ListView) findViewById(R.id.lv_fragment_detail_category_barang);
        pgbLoadBarang = (ProgressBar) findViewById(R.id.pgb_nama_barang);
        llProgressBarContainer = (LinearLayout) findViewById(R.id.ll_pgb_container);
        btnRefreshBarang = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            kodeKategoriBarang = "0"; // ALL Kategory
            noSalesOrder = extras.getString("noSalesOrder");
            kdCus = extras.getString("kdCus");
            namaPelanggan = extras.getString("namaPelanggan");
            tempo = extras.getString("tempo", "30");
            idTempo = extras.getString("idTempo", "");
            kodeBarang = extras.getString("kodeBarang");
            namaBarang = extras.getString("namaBarang");
        }

        getDetailBarangAutocomplete();
        btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetailBarangAutocomplete();
            }
        });

    }

    public void getDetailBarangAutocomplete(){

        start = 0;
        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"SHOW");
        isLoading = true;

        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
            jBody.put("tempo", "");
            jBody.put("kdcus", kdCus);
            jBody.put("id_tempo", idTempo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(ListBarangSalesOrder.this, jBody, "POST", urlGetAllBarangByKategori, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listMasterBarang = new ArrayList<Barang>();
                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listMasterBarang.add(new Barang(jo.getString("kdbrg"),
                                            jo.getString("namabrg")));
                                }
                            }

                            getListBarangAutocomplete(listMasterBarang);
                            getListBarangTable(listMasterBarang);

                        }catch (Exception e){
                            e.printStackTrace();
                            getListBarangAutocomplete(null);
                            getListBarangTable(null);
                        }
                    }

                    @Override
                    public void onError(String result) {

                        isLoading = false;
                        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"ERROR");
                    }
                });
    }

    private void getListBarangAutocomplete(List<Barang> listItems){

        actvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            /*BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(ListBarangSalesOrder.this, listItems.size(), listItems);

            //set adapter to autocomplete
            actvListBarang.setAdapter(arrayAdapterString);*/

            barangAutocompleteEvent();
        }
    }

    private void barangAutocompleteEvent(){

        actvListBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvListBarang.getText().toString();
                    getDetailBarangAutocomplete();
                    iv.hideSoftKey(ListBarangSalesOrder.this);
                    return true;
                }
                return false;
            }
        });

        if(firstLoadBarang){
            firstLoadBarang = false;
            actvListBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(editable.toString().length() <= 0) {

                        keyword = "";
                        getDetailBarangAutocomplete();
                    }
                }
            });
        }
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            arrayAdapterString = new ListBarangTableAdapter(ListBarangSalesOrder.this, listItems.size(), listItems);

            //set adapter to autocomplete
            lvListBarang.setAdapter(arrayAdapterString);

            lvListBarang.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                    int threshold = 1;
                    int countMerchant = lvListBarang.getCount();

                    if (i == SCROLL_STATE_IDLE) {
                        if (lvListBarang.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                            isLoading = true;
                            lvListBarang.addFooterView(footerList);
                            start += count;
                            getMoreData();
                            //Log.i(TAG, "onScroll: last ");
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });

            lvListBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Barang barang = (Barang) adapterView.getItemAtPosition(i);
                    kodeBarang = barang.getKodeBarang();
                    namaBarang = barang.getNamaBarang();

                    Intent intent = new Intent(getApplicationContext(), OrderDetail.class);
                    intent.putExtra("noSalesOrder",noSalesOrder);
                    intent.putExtra("namaPelanggan", namaPelanggan);
                    intent.putExtra("kdCus", kdCus);
                    intent.putExtra("tempo", tempo);
                    intent.putExtra("idTempo", idTempo);
                    intent.putExtra("kodeKategoriBarang", kodeKategoriBarang);
                    intent.putExtra("namaKategoriBarang", namaKategoriBarang);
                    intent.putExtra("kodeBarang", kodeBarang);
                    intent.putExtra("namaBarang", namaBarang);
                    startActivity(intent);
                }
            });
        }
    }

    public void getMoreData(){

        isLoading = true;
        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
            jBody.put("tempo", "");
            jBody.put("kdcus", kdCus);
            jBody.put("id_tempo", idTempo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(ListBarangSalesOrder.this, jBody, "POST", urlGetAllBarangByKategori, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        lvListBarang.removeFooterView(footerList);
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            moreList = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    moreList.add(new Barang(jo.getString("kdbrg"),
                                            jo.getString("namabrg")));
                                }
                            }

                            if(arrayAdapterString != null) arrayAdapterString.addMoreData(moreList);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {

                        lvListBarang.removeFooterView(footerList);
                        isLoading = false;
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
