package gmedia.net.id.kartikaelektrik.activityOrderCustom;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.CustomOrder.ListBarangCustomAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;

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
    private List<Barang> masterBarang = new ArrayList<>();
    private String kodeKategoriBarang = "0"; // Default all category
    private RadioGroup rgJenis;
    private String jenisBarang;
    private String status = "", tgl = "", tglTempo = "", flag = "";
    private RadioButton rbK, rbR;
    private int start = 0, count = 10;
    private String keyword = "";
    private View footerList;
    private boolean isLoading = false;
    private ListBarangCustomAdapter arrayAdapterString;

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
        setDetailBarangAutocomplete();
    }

    private void initUI() {

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.foother_listview_loading, null);
        kodeKategoriBarang = "0"; // ALL Kategory
        urlGetAllBarangByKategori = ServerURL.getBarangPerKategori;
        urlGetDetailBarang = getResources().getString(R.string.url_get_barang_by_id);
        actvListBarang = (AutoCompleteTextView) findViewById(R.id.actv_list_nama_barang);
        rgJenis = (RadioGroup) findViewById(R.id.rg_jenis);
        rbK = (RadioButton) findViewById(R.id.rb_1);
        rbR = (RadioButton) findViewById(R.id.rb_2);
        lvListBarang = (ListView) findViewById(R.id.lv_barang);
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

        //set adapter for autocomplete
        arrayAdapterString = new ListBarangCustomAdapter(ListBarangCustom.this, masterBarang);

        //set adapter to autocomplete
        lvListBarang.setAdapter(arrayAdapterString);

        lvListBarang.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvListBarang.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if ((lvListBarang.getLastVisiblePosition() >= (countMerchant - threshold)) && !isLoading) {

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
                jenisBarang = "K";
                if(rgJenis.getCheckedRadioButtonId() == R.id.rb_2) jenisBarang = "R";
                GetBarangDetail(barang.getKodeBarang(), barang.getNamaBarang());
            }
        });

        btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDetailBarangAutocomplete();
            }
        });

    }

    public void setDetailBarangAutocomplete(){

        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"SHOW");
        isLoading = true;
        start = 0;
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(ListBarangCustom.this, jBody, "POST", urlGetAllBarangByKategori, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        isLoading = false;
                        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"GONE");
                        JSONObject responseAPI = new JSONObject();
                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterBarang.clear();

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterBarang.add(new Barang(jo.getString("kdbrg"),
                                            jo.getString("namabrg")));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        getListBarangAutocomplete();
                        arrayAdapterString.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {
                        iv.ProgressbarEvent(llProgressBarContainer,pgbLoadBarang,btnRefreshBarang,"ERROR");
                        isLoading = false;
                    }
                });
    }

    private void getListBarangAutocomplete(){

        actvListBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    String keyword = actvListBarang.getText().toString();
                    setDetailBarangAutocomplete();
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
                    if(editable.toString().length() <= 0) {

                        keyword = "";
                        setDetailBarangAutocomplete();
                    }
                }
            });
        }
    }

    public void getMoreData(){

        isLoading = true;
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", kodeKategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley restService = new ApiVolley(ListBarangCustom.this, jBody, "POST", urlGetAllBarangByKategori, "", "", 0,
                new ApiVolley.VolleyCallback(){

                    @Override
                    public void onSuccess(String result){

                        lvListBarang.removeFooterView(footerList);
                        isLoading = false;

                        try {

                            JSONObject responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterBarang.add(new Barang(jo.getString("kdbrg"),
                                            jo.getString("namabrg")));
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();

                        }

                        arrayAdapterString.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String result) {
                        lvListBarang.removeFooterView(footerList);
                        isLoading = false;
                    }
                });
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
