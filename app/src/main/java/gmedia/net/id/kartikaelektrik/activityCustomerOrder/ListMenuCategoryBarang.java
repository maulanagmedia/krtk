package gmedia.net.id.kartikaelektrik.activityCustomerOrder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListKategoriBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.KategoriBarang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.adapter.BarangAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListKategoriBarangAutocompleteAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;

import org.json.JSONArray;
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
    private List<Barang> listMasterBarang, listBarang, listBarangTable;
    private SharedPreferenceHandler cfs = new SharedPreferenceHandler();
    private ProgressBar pbLoadBarang;
    private LinearLayout llLoadBarang;
    private Button btnRefreshBarang;
    private boolean firstLoadKategori = true;
    private boolean firstLoadBarang = true;


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
        urlGetAllBarangByKategori = getResources().getString(R.string.url_get_all_barang_by_kategori);

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

        listMasterBarang = new ArrayList<Barang>();
        listMasterBarang = cfs.getListBarangByCategoryFromLocal(getApplicationContext(), kodeKategoriBarang);

        iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"SHOW");

        if(listMasterBarang != null && listMasterBarang.size() > 0){

            listBarang = new ArrayList<Barang>(listMasterBarang);
            listBarangTable = new ArrayList<Barang>(listMasterBarang);

            getListBarangAutocomplete(listBarang);
            getListBarangTable(listBarangTable);
            iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");
        }else{

            // Get All Barang by Kategori
            ApiVolley restService = new ApiVolley(listKategoriView.getContext(), new JSONObject(), "GET", urlGetAllBarangByKategori+ kodeKategoriBarang, "", "", 0,
                    new ApiVolley.VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            JSONObject responseAPI = new JSONObject();

                            try {

                                responseAPI = new JSONObject(result);
                                String status = responseAPI.getJSONObject("metadata").getString("status");
                                listBarang = new ArrayList<Barang>();
                                listBarangTable = new ArrayList<Barang>();
                                listMasterBarang = new ArrayList<>();

                                if(iv.parseNullInteger(status) == 200){
                                    JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                    for(int i = 0; i < arrayJSON.length();i++){
                                        JSONObject jo = arrayJSON.getJSONObject(i);
                                        listMasterBarang.add(new Barang(jo.getString("kdbrg"),jo.getString("namabrg")));
                                    }
                                    ArrayList<Barang> barangMasterData = new ArrayList<>(listMasterBarang);
                                    cfs.saveListBarangToLocalByKategori(getApplicationContext(),barangMasterData, kodeKategoriBarang);
                                }

                                listBarang = new ArrayList<Barang>(listMasterBarang);
                                listBarangTable = new ArrayList<Barang>(listMasterBarang);

                                iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");

                                getListBarangAutocomplete(listBarang);
                                getListBarangTable(listBarangTable);

                            }catch (Exception e){
                                e.printStackTrace();
                                getListBarangAutocomplete(null);
                                getListBarangTable(null);
                                iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");
                            }
                        }

                        @Override
                        public void onError(String result) {
                            iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"ERROR");
                        }
                    });
        }
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

        actvListBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Barang barang = (Barang) adapterView.getItemAtPosition(i);
                kodeBarang = barang.getKodeBarang();
                namaBarang = barang.getNamaBarang();

                List<Barang> itemlistBarang = new ArrayList<Barang>();
                itemlistBarang.add(barang);
                getListBarangTable(itemlistBarang);
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
                    if(editable.toString().length() <= 0) getListBarangTable(listBarangTable);
                }
            });
        }
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            ListBarangTableAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListBarangTableAdapter((Activity) listKategoriView.getContext(),listItems.size(), listItems);

            //set adapter to autocomplete
            lvListBarang.setAdapter(arrayAdapterString);

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
