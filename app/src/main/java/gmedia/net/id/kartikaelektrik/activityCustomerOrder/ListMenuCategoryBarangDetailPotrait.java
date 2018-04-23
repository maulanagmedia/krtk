package gmedia.net.id.kartikaelektrik.activityCustomerOrder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;
import gmedia.net.id.kartikaelektrik.adapter.BarangAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by indra on 02/01/2017.
 */

public class ListMenuCategoryBarangDetailPotrait extends AppCompatActivity {

    private AutoCompleteTextView actvListBarang;
    private String TAG = "Main.Caegory.Barang.Detail.Potrait";
    private ListView lvListBarang;
    private View layout;
    private ItemValidation iv = new ItemValidation();
    private String kdCus = "", namaPelanggan = "", kodeKategoriBarang = "", namaKategoriBarang = "", kodeBarang = "", namaBarang = "", noSalesOrder, tempo = "";

    //Fragment Detail Barang
    private final String urlGetAllBarangByKategori = "http://kartika.gmedia.bz/api/barang/all_barang/kategori/";
    private List<Barang> listMasterBarang, listBarang, listBarangTable;
    private SharedPreferenceHandler cfs = new SharedPreferenceHandler();
    private ProgressBar pgbLoadBarang;
    private LinearLayout llProgressBarContainer;
    private Button btnRefreshBarang;
    private boolean firstLoadBarang = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_category_barang_potrait_detail);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Barang Order");
            initUI();

        }
    }

    private void initUI(){

        layout = (View) findViewById(R.id.fr_detail_list_barang);
        actvListBarang = (AutoCompleteTextView) layout.findViewById(R.id.actv_list_nama_barang);
        lvListBarang = (ListView) layout.findViewById(R.id.lv_fragment_detail_category_barang);
        pgbLoadBarang = (ProgressBar) layout.findViewById(R.id.pgb_nama_barang);
        llProgressBarContainer = (LinearLayout) layout.findViewById(R.id.ll_pgb_container);
        btnRefreshBarang = (Button) layout.findViewById(R.id.btn_refresh);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            noSalesOrder = extras.getString("noSalesOrder");
            kdCus = extras.getString("kdCus");
            namaPelanggan = extras.getString("namaPelanggan");
            tempo = extras.getString("tempo");
            kodeKategoriBarang = extras.getString("kodeKategoriBarang");
            namaKategoriBarang = extras.getString("namaKategoriBarang");
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
        listMasterBarang = cfs.getListBarangByCategoryFromLocal(layout.getContext(),kodeKategoriBarang);

        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"SHOW");
        if(listMasterBarang != null && listMasterBarang.size() > 0){

            listBarang = new ArrayList<Barang>(listMasterBarang);
            listBarangTable = new ArrayList<Barang>(listMasterBarang);

            getListBarangAutocomplete(listBarang);
            getListBarangTable(listBarangTable);
            iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");

        }else{

            // Get All Barang by Kategori
            ApiVolley restService = new ApiVolley(layout.getContext(), new JSONObject(), "GET", urlGetAllBarangByKategori+kodeKategoriBarang, "", "", 0,
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
                                    cfs.saveListBarangToLocalByKategori(layout.getContext(),barangMasterData,kodeKategoriBarang);
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
            BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(layout.getContext(),listItems.size(),listItems);

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
            arrayAdapterString = new ListBarangTableAdapter((Activity) layout.getContext(),listItems.size(), listItems);

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
