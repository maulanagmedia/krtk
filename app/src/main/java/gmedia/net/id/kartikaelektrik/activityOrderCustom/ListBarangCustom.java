package gmedia.net.id.kartikaelektrik.activityOrderCustom;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.activityCustomerOrder.ListBarangSalesOrder;
import gmedia.net.id.kartikaelektrik.activityCustomerOrder.OrderDetail;
import gmedia.net.id.kartikaelektrik.adapter.CustomOrder.ListBarangCustomAdapter;
import gmedia.net.id.kartikaelektrik.adapter.MenuCategoryBarang.ListBarangTableAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.LocationUpdateHandler;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;

public class ListBarangCustom extends AppCompatActivity {

    private AutoCompleteTextView actvListBarang;
    private String TAG = "BarangOrder";
    private ListView lvListBarang;
    private ItemValidation iv = new ItemValidation();
    private String kdCus = "", namaCus = "", noBukti = "";
    private String urlGetAllBarangByKategori = "", urlGetDetailBarang;
    private ProgressBar pgbLoadBarang;
    private LinearLayout llProgressBarContainer;
    private Button btnRefreshBarang;
    private boolean firstLoadBarang = true;
    private List<Barang> masterBarang, masterBarang1, masterBarang2;
    private SharedPreferenceHandler cfs = new SharedPreferenceHandler();
    private String kodeKategoriBarang = "0"; // Default all category
    private RadioGroup rgJenis;
    private String jenisBarang;
    private String status = "", tgl = "", tglTempo = "", flag = "";
    private RadioButton rbK, rbR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang_custom);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pilih Barang Custom");

        initUI();
    }

    private void initUI() {

        kodeKategoriBarang = "0"; // ALL Kategory
        urlGetAllBarangByKategori = getResources().getString(R.string.url_get_all_barang_by_kategori);
        urlGetDetailBarang = getResources().getString(R.string.url_get_barang_by_id);
        actvListBarang = (AutoCompleteTextView) findViewById(R.id.actv_list_nama_barang);
        rgJenis = (RadioGroup) findViewById(R.id.rg_jenis);
        rbK = (RadioButton) findViewById(R.id.rb_1);
        rbR = (RadioButton) findViewById(R.id.rb_2);
        lvListBarang = (ListView) findViewById(R.id.lv_fragment_detail_category_barang);
        pgbLoadBarang = (ProgressBar) findViewById(R.id.pgb_nama_barang);
        llProgressBarContainer = (LinearLayout) findViewById(R.id.ll_pgb_container);
        btnRefreshBarang = (Button) findViewById(R.id.btn_refresh);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            kdCus = extras.getString("kdcus");
            namaCus = extras.getString("nama");
            noBukti = extras.getString("nobukti");
            status = extras.getString("status");
            tgl = extras.getString("tgl");
            tglTempo = extras.getString("tgltempo");
            flag = extras.getString("flag");

            if(flag != null && flag.length()> 0){

                if(flag.equals("K")){
                    rbR.setVisibility(View.GONE);
                    rbK.setChecked(true);
                }else{
                    rbK.setVisibility(View.GONE);
                    rbR.setChecked(true);
                }
            }
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

        masterBarang = new ArrayList<Barang>();
        masterBarang = cfs.getListBarangByCategoryFromLocal(ListBarangCustom.this, kodeKategoriBarang);

        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"SHOW");

        if(masterBarang != null && masterBarang.size() > 0){

            masterBarang1 = new ArrayList<Barang>(masterBarang);
            masterBarang2 = new ArrayList<Barang>(masterBarang);

            getListBarangAutocomplete(masterBarang1);
            getListBarangTable(masterBarang2);
            iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");

        }else{

// Get All Barang by Kategori
            ApiVolley restService = new ApiVolley(ListBarangCustom.this, new JSONObject(), "GET", urlGetAllBarangByKategori + kodeKategoriBarang, "", "", 0,
                    new ApiVolley.VolleyCallback(){

                        @Override
                        public void onSuccess(String result){

                            JSONObject responseAPI = new JSONObject();
                            try {

                                responseAPI = new JSONObject(result);
                                String status = responseAPI.getJSONObject("metadata").getString("status");
                                masterBarang1 = new ArrayList<>();
                                masterBarang2 = new ArrayList<>();
                                if(iv.parseNullInteger(status) == 200){
                                    JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                    for(int i = 0; i < arrayJSON.length();i++){
                                        JSONObject jo = arrayJSON.getJSONObject(i);
                                        masterBarang.add(new Barang(jo.getString("kdbrg"), jo.getString("namabrg")));
                                    }
                                    cfs.saveListBarangToLocalByKategori(ListBarangCustom.this,new ArrayList<Barang>(masterBarang),kodeKategoriBarang);

                                    masterBarang1 = new ArrayList<Barang>(masterBarang);
                                    masterBarang2 = new ArrayList<Barang>(masterBarang);

                                }
                                iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");
                                getListBarangAutocomplete(masterBarang1);
                                getListBarangTable(masterBarang2);

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
                            getListBarangAutocomplete(null);
                            getListBarangTable(null);
                        }
                    });
        }
    }

    private void getListBarangAutocomplete(final List<Barang> listItems){

        actvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            /*BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(ListBarangCustom.this, listItems.size(), listItems);

            //set adapter to autocomplete
            actvListBarang.setAdapter(arrayAdapterString);*/

            actvListBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        List<Barang> itemToAdd = new ArrayList<Barang>();
                        String keyword = actvListBarang.getText().toString();

                        for(Barang item : listItems){
                            if(item.getNamaBarang().toUpperCase().contains(keyword.trim().toUpperCase())){
                                itemToAdd.add(item);
                            }
                        }

                        getListBarangTable(itemToAdd);
                        iv.hideSoftKey(ListBarangCustom.this);
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
                        if(editable.toString().length() <= 0) getListBarangTable(masterBarang);
                    }
                });
            }
        }
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            ListBarangCustomAdapter arrayAdapterString;

            //set adapter for autocomplete
            arrayAdapterString = new ListBarangCustomAdapter(ListBarangCustom.this, R.layout.adapter_single_menu, listItems);

            //set adapter to autocomplete
            lvListBarang.setAdapter(arrayAdapterString);

            lvListBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Barang barang = (Barang) adapterView.getItemAtPosition(i);
                    jenisBarang = "K";
                    if(rgJenis.getCheckedRadioButtonId() == R.id.rb_2) jenisBarang = "R";
                    GetBarangDetail(barang.getKodeBarang(), barang.getNamaBarang());
                }
            });
        }
    }

    private void GetBarangDetail(final String kdBarang, final String namaBarang){

        ApiVolley restService = new ApiVolley(ListBarangCustom.this, new JSONObject(), "GET", urlGetDetailBarang+kdBarang, "", "", 0,

                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {
                            responseAPI = new JSONObject(result);
                            String statusI = responseAPI.getJSONObject("metadata").getString("status");
                            if(iv.parseNullInteger(statusI) == 200){

                                String satuan = responseAPI.getJSONObject("response").getString("sat3");
                                Intent intent = new Intent(getApplicationContext(), OrderCustomDetail.class);
                                if(noBukti != null && noBukti.length() > 0) intent.putExtra("nobukti",noBukti);
                                intent.putExtra("nama", namaCus);
                                intent.putExtra("kdcus", kdCus);
                                intent.putExtra("kdbrg", kdBarang);
                                intent.putExtra("namabrg", namaBarang);
                                intent.putExtra("satuan", satuan);
                                if(status != null && status.length() > 0) intent.putExtra("status",status);
                                if(tgl != null && tgl.length() > 0) intent.putExtra("tgl",tgl);
                                if(tglTempo != null && tglTempo.length() > 0) intent.putExtra("tgltempo", tglTempo);
                                if(flag != null && flag.length() > 0){
                                    intent.putExtra("flag", flag);
                                }else{
                                    intent.putExtra("flag", jenisBarang);
                                }
                                startActivity(intent);
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

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
