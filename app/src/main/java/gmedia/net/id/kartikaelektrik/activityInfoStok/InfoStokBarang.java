package gmedia.net.id.kartikaelektrik.activityInfoStok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.BarangAdapter;
import gmedia.net.id.kartikaelektrik.adapter.InfoStokAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;

public class InfoStokBarang extends AppCompatActivity {

    private String urlInfoStok;
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvListStok;
    private ItemValidation iv = new ItemValidation();
    private ProgressBar pbLoadBarang;
    private LinearLayout llLoadBarang;
    private Button btnRefreshBarang;
    private List<Barang> listMasterBarang;
    private SharedPreferenceHandler sph = new SharedPreferenceHandler();
    private List<Barang> listBarangAutocomplete;
    private List<Barang> listBarangTable;
    private View ftListview;
    private InfoStokAdapter tableAdapter;
    private boolean isLoading = false, itemWasSelected = false;
    private Handler mHandler;
    private String TAG = "InfoStok";
    private String filterIndex = "0";
    private final String filterQuantity = "20";
    private boolean firstAddWatcher = true;
    private static String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_stok_barang);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Informasi Stok Barang");

        initUI();
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

    private void initUI() {

        keyword = "";
        urlInfoStok = getResources().getString(R.string.url_get_filter_barang);

        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        lvListStok = (ListView) findViewById(R.id.lv_list_info_stok);
        pbLoadBarang = (ProgressBar) findViewById(R.id.pb_load_barang);
        llLoadBarang = (LinearLayout) findViewById(R.id.ll_load_barang);
        btnRefreshBarang = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftListview = li.inflate(R.layout.foother_listview_loading, null);

        btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getBarangAutocomplete();
            }
        });

        getBarangAutocomplete();
    }

    public void getBarangAutocomplete() {

        listMasterBarang = new ArrayList<Barang>();

        iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"SHOW");
        lvListStok.setAdapter(null);
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("namabarang", keyword);
            jsonBody.put("startindex", filterIndex);
            jsonBody.put("filterquantity", filterQuantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(InfoStokBarang.this, jsonBody, "POST", urlInfoStok , "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            JSONArray arrayJSON = responseAPI.getJSONArray("response");

                            for(int i = 0; i < arrayJSON.length();i++){
                                JSONObject jo = arrayJSON.getJSONObject(i);
                                listMasterBarang.add(new Barang(
                                        jo.getString("kdbrg"),
                                        jo.getString("namabrg"),
                                        jo.getString("stok") + " "+ iv.parseNullString(jo.getString("satuan")),
                                        jo.getString("hargajual"),
                                        jo.getString("kdkat")));
                            }

                            listBarangAutocomplete = new ArrayList<Barang>(listMasterBarang);
                            listBarangTable = new ArrayList<Barang>(listMasterBarang);

                            ArrayList<Barang> barangMasterData = new ArrayList<>(listMasterBarang);
                            iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");

                            getListBarangAutocomplete(listBarangAutocomplete);
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

    private void getListBarangAutocomplete(List<Barang> listItems){

        actvNamaBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
           /* BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(InfoStokBarang.this,listItems.size(),listItems);

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);*/

            barangAutocompleteEvent();

            /*if(actvNamaBarang.length()>1 && !itemWasSelected){
                actvNamaBarang.showDropDown();
            }*/
        }
    }

    private void barangAutocompleteEvent(){

        /*actvNamaBarang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Barang barang = (Barang) adapterView.getItemAtPosition(i);

                List<Barang> itemlistBarang = new ArrayList<Barang>();
                itemlistBarang.add(barang);
                getListBarangTable(itemlistBarang);
                itemWasSelected = true;
            }
        });*/

        if(firstAddWatcher){

            firstAddWatcher = false;

            actvNamaBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if(s.length() == 0){
                        filterIndex = "0";
                        keyword = "";
                        getBarangAutocomplete();
                    }
                }
            });
        }

        actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    filterIndex = "0";
                    keyword = actvNamaBarang.getText().toString();
                    getBarangAutocomplete();
                    iv.hideSoftKey(InfoStokBarang.this);
                    return true;
                }
                return false;
            }
        });

        /*if(firstAddWatcher){

            firstAddWatcher = false;

            actvNamaBarang.addTextChangedListener(new TextWatcher() {

                boolean isTyping = false;

                Timer timer = new Timer();
                long delay;
                int jml = actvNamaBarang.length();
                int jmlText = actvNamaBarang.length();

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    jmlText = charSequence.length();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(actvNamaBarang.isPerformingCompletion()){
                        itemWasSelected = true;
                        jml = charSequence.length();
                        jmlText = charSequence.length();
                        return;
                    }else{
                        itemWasSelected = false;
                        timer = new Timer();
                        delay = 1300;

                        if(jmlText < charSequence.length()){
                            jml++;
                        }else if(jmlText > charSequence.length()){
                            jml--;
                        }
                    }
                }

                @Override
                public void afterTextChanged(final Editable editable) {

                    final int counter = jml;
                    if(!isTyping) {

                        // start typing
                        isTyping = true;
                    }
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {

                                @Override
                                public void run() {

                                    isTyping = false;

                                    // stopped typing
                                    if(counter == editable.length() && !itemWasSelected){
                                        InfoStokBarang.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                filterIndex = "0";
                                                *//*keyword = editable.toString();
                                                getBarangAutocomplete();*//*
                                            }
                                        });
                                    }
                                }
                            },
                            delay
                    );
                }

            });
        }*/
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListStok.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            //set adapter for autocomplete

            mHandler = new MyHandler();

            tableAdapter = new InfoStokAdapter(InfoStokBarang.this,listItems.size(), listItems, "barang");

            //set adapter to autocomplete
            lvListStok.setAdapter(tableAdapter);

            lvListStok.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int totalCount) {

                    if(absListView.getLastVisiblePosition() == totalCount-1 && lvListStok.getCount() > (iv.parseNullInteger(filterQuantity)-1) && !isLoading ){
                        isLoading = true;
                        Thread thread = new ThreadGetMoreData();
                        thread.start();
                    }

                }
            });

            lvListStok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Barang selectedItem = (Barang) parent.getItemAtPosition(position);

                    Intent intent = new Intent(InfoStokBarang.this, DetailStokBarang.class);
                    intent.putExtra("id", selectedItem.getKodeBarang());
                    intent.putExtra("nama", selectedItem.getNamaBarang());
                    startActivity(intent);
                }
            });
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    lvListStok.addFooterView(ftListview);
                    break;
                case 1:
                    tableAdapter.AddListItemAdapter((ArrayList<Barang>)msg.obj);
                    lvListStok.removeFooterView(ftListview);
                    isLoading = false;
                    break;
                case 2:
                    lvListStok.removeFooterView(ftListview);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread{
        @Override
        public void run() {

            mHandler.sendEmptyMessage(0);

            final ArrayList<Barang> addList = new ArrayList<Barang>();

            JSONObject jsonBody = new JSONObject();

            filterIndex = String.valueOf(iv.parseNullInteger(filterIndex) + iv.parseNullInteger(filterQuantity));

            try {
                jsonBody.put("namabarang", keyword);
                jsonBody.put("startindex", filterIndex);
                jsonBody.put("filterquantity", filterQuantity);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley restService = new ApiVolley(InfoStokBarang.this, jsonBody, "POST", urlInfoStok , "", "", 0,
                    new ApiVolley.VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            JSONObject responseAPI = new JSONObject();

                            try {

                                responseAPI = new JSONObject(result);
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    addList.add(new Barang(jo.getString("kdbrg"),jo.getString("namabrg"),jo.getString("stok") + " " + jo.getString("satuan"),jo.getString("hargajual"),jo.getString("kdkat")));
                                }

                                Message msg = mHandler.obtainMessage(1, addList);
                                mHandler.sendMessage(msg);

                            }catch (Exception e){
                                e.printStackTrace();
                                mHandler.sendEmptyMessage(2);
                            }
                            mHandler.sendEmptyMessage(2);
                        }

                        @Override
                        public void onError(String result) {
                            mHandler.sendEmptyMessage(2);
                        }
                    });
        }
    }
}
