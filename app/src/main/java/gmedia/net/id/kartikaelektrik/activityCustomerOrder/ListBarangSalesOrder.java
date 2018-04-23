package gmedia.net.id.kartikaelektrik.activityCustomerOrder;

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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.BarangAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;

public class ListBarangSalesOrder extends AppCompatActivity {

    private AutoCompleteTextView actvListBarang;
    private String TAG = "BarangOrder";
    private ListView lvListBarang;
    private ItemValidation iv = new ItemValidation();
    private String kdCus = "", namaPelanggan = "", kodeKategoriBarang = "", namaKategoriBarang = "", kodeBarang = "", namaBarang = "", noSalesOrder, tempo = "";

    //Fragment Detail Barang
    private String urlGetAllBarangByKategori = "";
    private List<Barang> listMasterBarang, listBarang, listBarangTable;
    private SharedPreferenceHandler cfs = new SharedPreferenceHandler();
    private ProgressBar pgbLoadBarang;
    private LinearLayout llProgressBarContainer;
    private Button btnRefreshBarang;
    private boolean firstLoadBarang = true;

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

        urlGetAllBarangByKategori = getResources().getString(R.string.url_get_all_barang_by_kategori);
        actvListBarang = (AutoCompleteTextView) findViewById(R.id.actv_list_nama_barang);
        lvListBarang = (ListView) findViewById(R.id.lv_fragment_detail_category_barang);
        pgbLoadBarang = (ProgressBar) findViewById(R.id.pgb_nama_barang);
        llProgressBarContainer = (LinearLayout) findViewById(R.id.ll_pgb_container);
        btnRefreshBarang = (Button) findViewById(R.id.btn_refresh);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            kodeKategoriBarang = "0"; // ALL Kategory
            noSalesOrder = extras.getString("noSalesOrder");
            kdCus = extras.getString("kdCus");
            namaPelanggan = extras.getString("namaPelanggan");
            tempo = extras.getString("tempo");
            kodeBarang = extras.getString("kodeBarang");
            namaBarang = extras.getString("namaBarang");
        }

        setDetailBarangAutocomplete();
        btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDetailBarangAutocomplete();
            }
        });

    }

    public void setDetailBarangAutocomplete(){

        listMasterBarang = new ArrayList<Barang>();
        listMasterBarang = cfs.getListBarangByCategoryFromLocal(ListBarangSalesOrder.this, kodeKategoriBarang);

        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"SHOW");

        if(listMasterBarang != null && listMasterBarang.size() > 0){

            listBarang = new ArrayList<Barang>(listMasterBarang);
            listBarangTable = new ArrayList<Barang>(listMasterBarang);

            getListBarangAutocomplete(listBarang);
            getListBarangTable(listBarangTable);
            iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");

        }else{

            // Get All Barang by Kategori
            ApiVolley restService = new ApiVolley(ListBarangSalesOrder.this, new JSONObject(), "GET", urlGetAllBarangByKategori+kodeKategoriBarang, "", "", 0,
                    new ApiVolley.VolleyCallback(){

                        @Override
                        public void onSuccess(String result){

                            JSONObject responseAPI = new JSONObject();
                            try {

                                responseAPI = new JSONObject(result);
                                String status = responseAPI.getJSONObject("metadata").getString("status");
                                listBarang = new ArrayList<>();
                                listBarangTable = new ArrayList<>();
                                if(iv.parseNullInteger(status) == 200){
                                    JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                    for(int i = 0; i < arrayJSON.length();i++){
                                        JSONObject jo = arrayJSON.getJSONObject(i);
                                        listMasterBarang.add(new Barang(jo.getString("kdbrg"),jo.getString("namabrg")));
                                    }
                                    ArrayList<Barang> barangMasterData = new ArrayList<>(listMasterBarang);
                                    cfs.saveListBarangToLocalByKategori(ListBarangSalesOrder.this,barangMasterData,kodeKategoriBarang);
                                }
                                listBarang = new ArrayList<Barang>(listMasterBarang);
                                listBarangTable = new ArrayList<Barang>(listMasterBarang);

                                iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");
                                getListBarangAutocomplete(listBarang);
                                getListBarangTable(listBarangTable);

                            }catch (Exception e){
                                e.printStackTrace();
                                iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");
                                getListBarangAutocomplete(null);
                                getListBarangTable(null);
                            }
                        }

                        @Override
                        public void onError(String result) {
                            iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"ERROR");
                        }
                    });
        }
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

        actvListBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<Barang> itemToAdd = new ArrayList<Barang>();
                    String keyword = actvListBarang.getText().toString();

                    for(Barang item : listBarangTable){
                        if(item.getNamaBarang().toUpperCase().contains(keyword.trim().toUpperCase())){
                            itemToAdd.add(item);
                        }
                    }

                    getListBarangTable(itemToAdd);
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
            arrayAdapterString = new ListBarangTableAdapter(ListBarangSalesOrder.this, listItems.size(), listItems);

            //set adapter to autocomplete
            lvListBarang.setAdapter(arrayAdapterString);

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
                    intent.putExtra("kodeKategoriBarang", kodeKategoriBarang);
                    intent.putExtra("namaKategoriBarang", namaKategoriBarang);
                    intent.putExtra("kodeBarang", kodeBarang);
                    intent.putExtra("namaBarang", namaBarang);
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
