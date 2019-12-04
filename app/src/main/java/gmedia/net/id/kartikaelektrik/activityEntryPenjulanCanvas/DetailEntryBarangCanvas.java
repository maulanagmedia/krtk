package gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas.DetailEntryBarangCanvasAdapter;
import gmedia.net.id.kartikaelektrik.model.Barang;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;
import gmedia.net.id.kartikaelektrik.util.ServerURL;
import gmedia.net.id.kartikaelektrik.util.SharedPreferenceHandler;

public class DetailEntryBarangCanvas extends AppCompatActivity {

    private String urlInfoStok = "";
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvListBarangCanvas;
    private ItemValidation iv = new ItemValidation();
    private ProgressBar pbLoadBarang;
    private LinearLayout llLoadBarang;
    private Button btnRefreshBarang;
    private List<Barang> listMasterBarang;
    private SharedPreferenceHandler sph = new SharedPreferenceHandler();
    private List<Barang> listBarangAutocomplete;
    private List<Barang> listBarangTable;
    private View ftListview;
    private DetailEntryBarangCanvasAdapter tableAdapter;
    private boolean isLoading = false, itemWasSelected = false;
    private Handler mHandler;
    private String TAG = "InfoStok";
    private String filterIndex = "0";
    private final String filterQuantity = "10";
    private boolean firstAddWatcher = true;
    private String kdCus = "", namaCustomer = "";
    private String noKonsinyasi = "";
    private String noBukti = "", selectedBarang = "";
    private static String keyword = "";
    private List<CustomListItem> selectedBarangList;
    private String tempo = "", idTempo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_entry_barang_canvas);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pilih Barang Canvas");
        initUI();
    }

    private void initUI() {

        keyword = "";
        urlInfoStok = ServerURL.getStokCanvas;
        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        lvListBarangCanvas = (ListView) findViewById(R.id.lv_list_barang);
        pbLoadBarang = (ProgressBar) findViewById(R.id.pb_load_barang);
        llLoadBarang = (LinearLayout) findViewById(R.id.ll_load_barang);
        btnRefreshBarang = (Button) findViewById(R.id.btn_refresh);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftListview = li.inflate(R.layout.foother_listview_loading, null);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            try {

                kdCus = bundle.getString("kdcus","");
                namaCustomer = bundle.getString("nama","");
                noBukti = bundle.getString("nobukti","");
                tempo = bundle.getString("tempo", "30");
                idTempo = bundle.getString("idTempo", "");
                selectedBarang = bundle.getString("selectedbarang","");

                String selectedBarangString  = selectedBarang;
                selectedBarangList = new ArrayList<>();
                Type tipeBarangList = new TypeToken<List<CustomListItem>>(){}.getType();
                Gson gson = new Gson();
                selectedBarangList = gson.fromJson(selectedBarangString, tipeBarangList);

            }catch (Exception e){

            }

            btnRefreshBarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getBarangAutocomplete();
                }
            });

            getBarangAutocomplete();
        }
    }

    public void getBarangAutocomplete() {

        listMasterBarang = new ArrayList<Barang>();

        iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"SHOW");
        lvListBarangCanvas.setAdapter(null);
        JSONObject jsonBody = new JSONObject();

        try {

            /*jsonBody.put("namabarang", actvNamaBarang.getText().toString());*/
            jsonBody.put("startindex", filterIndex);
            jsonBody.put("filterquantity", filterQuantity);
            jsonBody.put("keyword", keyword);
            jsonBody.put("tempo", "");
            jsonBody.put("kdcus", kdCus);
            jsonBody.put("id_tempo", idTempo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley restService = new ApiVolley(DetailEntryBarangCanvas.this, jsonBody, "POST", urlInfoStok , "", "", 0,
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
                                        getStok(jo.getString("kdbrg"), jo.getString("stok")),
                                        jo.getString("hargajual"),
                                        jo.getString("satuan"),
                                        "",
                                        jo.getString("nobukti")));
                            }

                            iv.ProgressbarEvent(llLoadBarang, pbLoadBarang,btnRefreshBarang,"GONE");
                            listBarangAutocomplete = new ArrayList<Barang>(listMasterBarang);
                            listBarangTable = new ArrayList<Barang>(listMasterBarang);
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
            /*BarangAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new BarangAdapter(DetailEntryBarangCanvas.this,listItems.size(),listItems);

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

            /*actvNamaBarang.addTextChangedListener(new TextWatcher() {

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
                                        DetailEntryBarangCanvas.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                filterIndex = "0";
                                                getBarangAutocomplete();
                                            }
                                        });
                                    }
                                }
                            },
                            delay
                    );
                }

            });*/
        }

        actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    filterIndex = "0";
                    keyword = actvNamaBarang.getText().toString();
                    getBarangAutocomplete();
                    iv.hideSoftKey(DetailEntryBarangCanvas.this);
                    return true;
                }
                return false;
            }
        });
    }

    private void getListBarangTable(List<Barang> listItems){

        lvListBarangCanvas.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            //set adapter for autocomplete

            mHandler = new DetailEntryBarangCanvas.MyHandler();

            tableAdapter = new DetailEntryBarangCanvasAdapter(DetailEntryBarangCanvas.this,listItems.size(), listItems);

            //set adapter to autocomplete
            lvListBarangCanvas.setAdapter(tableAdapter);

            lvListBarangCanvas.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int totalCount) {

                    if(absListView.getLastVisiblePosition() == totalCount-1 && lvListBarangCanvas.getCount() > (iv.parseNullInteger(filterQuantity)-1) && !isLoading ){
                        isLoading = true;
                        Thread thread = new ThreadGetMoreData();
                        thread.start();
                    }
                }
            });

            lvListBarangCanvas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Barang barang = (Barang) parent.getItemAtPosition(position);

                    Intent intent = new Intent(DetailEntryBarangCanvas.this, DetailOrderEntryCanvas.class);
                    intent.putExtra("kdcus", kdCus);
                    intent.putExtra("nama", namaCustomer);
                    intent.putExtra("kodebarang", barang.getKodeBarang());
                    intent.putExtra("namabarang", barang.getNamaBarang());
                    intent.putExtra("satuan", barang.getSatuan());
                    intent.putExtra("stok", barang.getStok());
                    intent.putExtra("nokonsinyasi", barang.getNoKonsinyasi());
                    intent.putExtra("nobukti", noBukti);
                    intent.putExtra("selectedbarang", selectedBarang);
                    intent.putExtra("tempo", tempo);
                    intent.putExtra("idTempo", idTempo);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    lvListBarangCanvas.addFooterView(ftListview);
                    break;
                case 1:
                    tableAdapter.AddListItemAdapter((ArrayList<Barang>)msg.obj);
                    lvListBarangCanvas.removeFooterView(ftListview);
                    isLoading = false;
                    break;
                case 2:
                    lvListBarangCanvas.removeFooterView(ftListview);
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
                /*jsonBody.put("namabarang", actvNamaBarang.getText().toString());*/
                jsonBody.put("startindex", filterIndex);
                jsonBody.put("filterquantity", filterQuantity);
                jsonBody.put("keyword", keyword);
                jsonBody.put("tempo", "");
                jsonBody.put("kdcus", kdCus);
                jsonBody.put("id_tempo", idTempo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiVolley restService = new ApiVolley(DetailEntryBarangCanvas.this, jsonBody, "POST", urlInfoStok , "", "", 0,
                    new ApiVolley.VolleyCallback(){
                        @Override
                        public void onSuccess(String result){
                            JSONObject responseAPI = new JSONObject();

                            try {

                                responseAPI = new JSONObject(result);
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");

                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    addList.add(new Barang(jo.getString("kdbrg"),
                                            jo.getString("namabrg"),
                                            getStok(jo.getString("kdbrg"), jo.getString("stok")),
                                            jo.getString("hargajual"),
                                            jo.getString("satuan"),
                                            "",
                                            jo.getString("nobukti")));
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

    private String getStok(String kdbrg, String stok){

        String newStok = stok;
        if(selectedBarangList != null && selectedBarangList.size() > 0){

            long currentSisa = 0;
            for(CustomListItem item : selectedBarangList){

                if(kdbrg.equals(item.getListItem1())) currentSisa += iv.parseNullLong(item.getListItem3());
            }

            newStok = String.valueOf(iv.parseNullLong(stok) - currentSisa);
        }

        return newStok;
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
