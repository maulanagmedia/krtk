package gmedia.net.id.kartikaelektrik.activityTambahOrderSales;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListKategoriBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.KategoriBarang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.BarangAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListKategoriBarangAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListMenuCategoryBarang extends AppCompatActivity {

    /*
    * Bundle need to send to this activity : kdCus, customerName
    */

    private AutoCompleteTextView actvListCategoryBarang, actvListBarang;
    private String TAG = "Main.Caegory.Barang";
    private ListView lvListKategoriBarang, lvListBarang;
    private View listKategoriView, detailKategoriView;
    private String kdCus = "", namaPelanggan = "", kodeKategoriBarang = "", namaKategoriBarang = "", kodeBarang = "", namaBarang = "", tempo = "";
    private boolean potraitMode;
    private String noSalesOrder = "";
    private ItemValidation iv = new ItemValidation();

    // Fragment List Kategori Barang
    private String urlGetAllKategoriBarang;
    private List<KategoriBarang> listKategoriBarang, listKategoriBarangTable;
    private LinearLayout llLoadCategory;
    private ProgressBar pbLoadCategory;
    private Button btnRefreshCategory;

    //Fragment Detail Barang
    private String urlGetAllBarangByKategori;
    private List<Barang> listMasterBarang, moreList;
    private ProgressBar pbLoadBarang;
    private LinearLayout llLoadBarang;
    private Button btnRefreshBarang;
    private boolean firstLoadKategori = true;
    private boolean firstLoadBarang = true;
    private int start = 0, count = 10;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private ListBarangTableAdapter arrayAdapterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_barang);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Barang Order");
        initUI();
    }

    private void initUI(){

        // Get Previous Data Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            noSalesOrder = extras.getString("noSalesOrder");
            kdCus = extras.getString("kdCus");
            namaPelanggan = extras.getString("customerName");
            tempo = extras.getString("tempo");
        }

        urlGetAllKategoriBarang = getResources().getString(R.string.url_get_all_kategori_barang);
        urlGetAllBarangByKategori = ServerURL.getBarangPerKategori;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            listKategoriView = (View) findViewById(R.id.fr_list_kategori_barang_land);
            detailKategoriView = (View) findViewById(R.id.fr_detail_kategori_barang_land);
            potraitMode = false;
            initListMenuLeft();
            initDetailMenuRight();

        }else{ // Potrait mode
            listKategoriView = (View) findViewById(R.id.fr_list_kategori_barang_potrait);
            potraitMode = true;
            initListMenuLeft();
        }

    }

    //region Left Menu Methods
    private void initListMenuLeft(){

        lvListKategoriBarang = (ListView) listKategoriView.findViewById(R.id.lv_fragment_list_category_barang);
        actvListCategoryBarang = (AutoCompleteTextView) listKategoriView.findViewById(R.id.actv_list_category_barang);
        llLoadCategory = (LinearLayout) listKategoriView.findViewById(R.id.ll_load_category_barang);
        pbLoadCategory = (ProgressBar) listKategoriView.findViewById(R.id.pb_load_category_barang);
        btnRefreshCategory = (Button) listKategoriView.findViewById(R.id.btn_refresh);

        setListCategoryBarangAutocomplete();

        btnRefreshCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setListCategoryBarangAutocomplete();
            }
        });
    }

    public void setListCategoryBarangAutocomplete(){

        iv.ProgressbarEvent(llLoadCategory,pbLoadCategory,btnRefreshCategory,"SHOW");

        // Get All Kategori Barang
        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(listKategoriView.getContext(), jsonBody, "GET", urlGetAllKategoriBarang, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");

                            listKategoriBarang = new ArrayList<KategoriBarang>();
                            listKategoriBarangTable = new ArrayList<KategoriBarang>();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listKategoriBarang.add(new KategoriBarang(jo.getString("kdkat"),jo.getString("kategori")));
                                    listKategoriBarangTable.add(new KategoriBarang(jo.getString("kdkat"),jo.getString("kategori")));
                                }
                            }

                            iv.ProgressbarEvent(llLoadCategory,pbLoadCategory,btnRefreshCategory,"GONE");
                            getListCategoryBarangAutocomplete(listKategoriBarang);
                            // set list table customer
                            getListCategoryBarangTable(listKategoriBarangTable);

                        }catch (Exception e){
                            iv.ProgressbarEvent(llLoadCategory,pbLoadCategory,btnRefreshCategory,"GONE");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoadCategory,pbLoadCategory,btnRefreshCategory,"ERROR");
                    }
                });
    }

    private void getListCategoryBarangAutocomplete(List<KategoriBarang> listItems){

        ListKategoriBarangAutocompleteAdapter arrayAdapterString;

        //set adapter for autocomplete//set adapter for autocomplete
        arrayAdapterString = new ListKategoriBarangAutocompleteAdapter(listKategoriView.getContext(),listItems.size(),listItems);

        //set adapter to autocomplete
        actvListCategoryBarang.setAdapter(arrayAdapterString);

        categoryBarangAutocompleteEvent();
    }

    private void categoryBarangAutocompleteEvent(){

        actvListCategoryBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KategoriBarang kategoriBarang = (KategoriBarang) adapterView.getItemAtPosition(i);
                kodeKategoriBarang = kategoriBarang.getKodeKategori();
                namaKategoriBarang = kategoriBarang.getNamaKategori();

                List<KategoriBarang> itemlistKategoriBarang = new ArrayList<KategoriBarang>();
                itemlistKategoriBarang.add(kategoriBarang);
                getListCategoryBarangTable(itemlistKategoriBarang);
            }
        });

        if(firstLoadKategori){
            firstLoadKategori = false;
            actvListCategoryBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.toString().length() <= 0) getListCategoryBarangTable(listKategoriBarangTable);
                }
            });
        }
    }

    // TODO: Show table Kategori Barang
    // method to show table category barang
    private void getListCategoryBarangTable(List<KategoriBarang> listItems){

        lvListKategoriBarang.setAdapter(null);

        ListKategoriBarangTableAdapter arrayAdapterString;

        //set adapter for autocomplete
        arrayAdapterString = new ListKategoriBarangTableAdapter((Activity) listKategoriView.getContext(),listItems.size(), listItems);

        //set adapter to autocomplete
        lvListKategoriBarang.setAdapter(arrayAdapterString);

        lvListKategoriBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KategoriBarang kategoriBarang = (KategoriBarang) adapterView.getItemAtPosition(i);
                kodeKategoriBarang = kategoriBarang.getKodeKategori();
                namaKategoriBarang = kategoriBarang.getNamaKategori();
                setDetailView();
            }
        });
    }
    //endregion

    //region Detail Menu Right (Detail view)

    private void initDetailMenuRight(){

        actvListBarang = (AutoCompleteTextView) detailKategoriView.findViewById(R.id.actv_list_nama_barang);
        lvListBarang = (ListView) detailKategoriView.findViewById(R.id.lv_fragment_detail_category_barang);
        pbLoadBarang = (ProgressBar) detailKategoriView.findViewById(R.id.pgb_nama_barang);
        llLoadBarang = (LinearLayout) detailKategoriView.findViewById(R.id.ll_pgb_container);
        btnRefreshBarang = (Button) detailKategoriView.findViewById(R.id.btn_refresh);

    }

    //TODO : Tap One Of Kategori Barang
    private void setDetailView(){

        if (potraitMode){

            // When identify the potrait, move to potrait detail
            Intent intent = new Intent(getApplicationContext(), ListMenuCategoryBarangDetailPotrait.class);
            intent.putExtra("noSalesOrder", noSalesOrder);
            intent.putExtra("namaPelanggan", namaPelanggan);
            intent.putExtra("kdCus", kdCus);
            intent.putExtra("tempo", tempo);
            intent.putExtra("kodeKategoriBarang", kodeKategoriBarang);
            intent.putExtra("namaKategoriBarang", namaKategoriBarang);
            startActivity(intent);

        }else{
            setDetailBarangAutocomplete();

            btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDetailBarangAutocomplete();
                }
            });
        }

    }

    public void setDetailBarangAutocomplete(){

        iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"SHOW");
        start = 0;
        isLoading = true;

        // Get All Barang by Kategori
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(listKategoriView.getContext(), jBody, "POST", urlGetAllBarangByKategori, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){

                        iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");
                        isLoading = false;
                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            listMasterBarang = new ArrayList<>();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    listMasterBarang.add(new Barang(jo.getString("kdbrg"),jo.getString("namabrg")));
                                }
                            }

                            getListBarangAutocomplete(listMasterBarang);
                            getListBarangTable(listMasterBarang);

                        }catch (Exception e){
                            e.printStackTrace();
                            getListBarangAutocomplete(null);
                            getListBarangTable(null);
                            iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        isLoading = false;
                        iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"ERROR");
                    }
                });
    }

    private void getListBarangAutocomplete(List<Barang> listItems){

        actvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(detailKategoriView.getContext(),listItems.size(),listItems);

            //set adapter to autocomplete
            actvListBarang.setAdapter(arrayAdapterString);

            barangAutocompleteEvent();
        }
    }

    private void barangAutocompleteEvent(){

        actvListBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    keyword = actvListBarang.getText().toString();
                    setDetailBarangAutocomplete();
                    iv.hideSoftKey(ListMenuCategoryBarang.this);
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
                        setDetailBarangAutocomplete();
                    };
                }
            });
        }
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            //set adapter for autocomplete
            arrayAdapterString = new ListBarangTableAdapter((Activity) listKategoriView.getContext(),listItems.size(), listItems);

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
                    intent.putExtra("noSalesOrder", noSalesOrder);
                    intent.putExtra("namaPelanggan", namaPelanggan);
                    intent.putExtra("kdCus", kdCus);
                    intent.putExtra("tempo", tempo);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(ListMenuCategoryBarang.this, jBody, "POST", urlGetAllBarangByKategori, "", "", 0,
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

    //endregion

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
