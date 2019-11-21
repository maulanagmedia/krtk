package gmedia.net.id.kartikaelektrik.activityEntryPaket;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.EntryPaket.BarangPaketExpandAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailBarangPaket extends AppCompatActivity {

    private String urlGetNamaPaket;
    private AutoCompleteTextView actvPaket;
    private ExpandableListView lvPaket;
    private ProgressBar pbLoad;
    private LinearLayout llLoad;
    private Button btnRefresh;
    private String kdCus = "", namaPelanggan = "";
    private ItemValidation iv = new ItemValidation();
    private boolean firstLoad = true;
    private SortedMap<String, List<CustomListItem>> masterMapList, autocompleteList, tableMapList;
    private String TAG = "DetailPaket";
    private String noBukti = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang_paket);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Pilih Nama Paket");
        urlGetNamaPaket = getResources().getString(R.string.url_get_paket);
        actvPaket = (AutoCompleteTextView) findViewById(R.id.actv_paket);
        lvPaket = (ExpandableListView) findViewById(R.id.elv_paket);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            //kodeKategoriBarang = "0"; // ALL Kategory
            //noSalesOrder = extras.getString("noSalesOrder");
            noBukti = extras.getString("nobukti");
            kdCus = extras.getString("kdcus");
            namaPelanggan = extras.getString("nama");
            //tempo = extras.getString("tempo");
            //kodeBarang = extras.getString("kodeBarang");
            //namaBarang = extras.getString("namaBarang");
            setDetailBarangAutocomplete();
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDetailBarangAutocomplete();
                }
            });
        }
    }

    public void setDetailBarangAutocomplete(){

        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"SHOW");

        // Get All Barang by Kategori
        ApiVolley restService = new ApiVolley(DetailBarangPaket.this, new JSONObject(), "GET", urlGetNamaPaket, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        JSONObject responseAPI = new JSONObject();
                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            List<CustomListItem> itemToAdd = new ArrayList<>();
                            masterMapList = new TreeMap<>();
                            String lastNamaPaket = "";

                            if(iv.parseNullInteger(status) == 200){

                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                if(arrayJSON.length() > 0){

                                    JSONObject jo0 = arrayJSON.getJSONObject(0);
                                    lastNamaPaket = jo0.getString("nama_paket");
                                }

                                for(int i = 0; i < arrayJSON.length();i++){

                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    if(!lastNamaPaket.equals(jo.getString("nama_paket"))) {

                                        masterMapList.put(lastNamaPaket, itemToAdd);
                                        lastNamaPaket = jo.getString("nama_paket");
                                        itemToAdd = new ArrayList<>();
                                    }

                                    itemToAdd.add(new CustomListItem(jo.getString("nobukti"),jo.getString("jenis_paket")));

                                    if(i == arrayJSON.length() - 1 ) {

                                        masterMapList.put(lastNamaPaket, itemToAdd);
                                        lastNamaPaket = jo.getString("nama_paket");
                                        itemToAdd = new ArrayList<>();
                                    }

                                }
                            }
                            autocompleteList = new TreeMap<>(masterMapList);
                            tableMapList = new TreeMap<>(masterMapList);

                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                            getListBarangAutocomplete(autocompleteList);
                            getListBarangTable(tableMapList);

                        }catch (Exception e){
                            e.printStackTrace();
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                            getListBarangAutocomplete(null);
                            getListBarangTable(null);
                        }
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                        getListBarangAutocomplete(null);
                        getListBarangTable(null);
                    }
                });
    }

    private void getListBarangAutocomplete(final SortedMap<String, List<CustomListItem>> listItems){

        actvPaket.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            actvPaket.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        SortedMap<String, List<CustomListItem>> itemToAdd = new TreeMap<String, List<CustomListItem>>();
                        String keyword = actvPaket.getText().toString().toUpperCase();

                        for(String key: masterMapList.keySet()){
                            if(key.toUpperCase().contains(keyword)){
                                itemToAdd.put(key,masterMapList.get(key));
                            }
                        }

                        getListBarangTable(itemToAdd);
                        iv.hideSoftKey(DetailBarangPaket.this);
                        return true;
                    }
                    return false;
                }
            });

            if(firstLoad){
                firstLoad = false;
                actvPaket.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(editable.toString().length() <= 0) getListBarangTable(masterMapList);
                    }
                });
            }
        }
    }

    private void getListBarangTable(SortedMap<String, List<CustomListItem>> listItems){

        lvPaket.setAdapter((BaseExpandableListAdapter)null);

        if (listItems != null && listItems.size() > 0){
            final BarangPaketExpandAdapter arrayAdapterString;

            //set adapter for autocomplete
            List<String> headerList = new ArrayList<>();
            for(String key: listItems.keySet()){
                headerList.add(key);
            }
            arrayAdapterString = new BarangPaketExpandAdapter(DetailBarangPaket.this, headerList, listItems);

            //set adapter to autocomplete
            lvPaket.setAdapter(arrayAdapterString);

            lvPaket.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    CustomListItem item = (CustomListItem) arrayAdapterString.getChild(groupPosition, childPosition);

                    String kdPaket = item.getListItem1();
                    String namaPaket = (String) arrayAdapterString.getGroup(groupPosition);
                    String jenisPaket = item.getListItem2();

                    Intent intent = new Intent(DetailBarangPaket.this, OrderDetailPaket.class);
                    if (noBukti != null && noBukti.length() > 0) intent.putExtra("nobukti", noBukti);
                    intent.putExtra("kdcus", kdCus);
                    intent.putExtra("nama", namaPelanggan);
                    intent.putExtra("kdpaket", kdPaket);
                    intent.putExtra("namapaket", namaPaket);
                    intent.putExtra("jenispaket", jenisPaket);
                    startActivity(intent);
                    finish();

                    return true;
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
