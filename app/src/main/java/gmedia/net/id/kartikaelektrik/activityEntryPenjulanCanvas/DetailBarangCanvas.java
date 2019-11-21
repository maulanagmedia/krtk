package gmedia.net.id.kartikaelektrik.activityEntryPenjulanCanvas;

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
import gmedia.net.id.kartikaelektrik.adapter.EntryPenjualanCanvas.DetailBarangCanvasAdapter;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;
import gmedia.net.id.kartikaelektrik.util.ApiVolley;
import gmedia.net.id.kartikaelektrik.util.ItemValidation;

public class DetailBarangCanvas extends AppCompatActivity {

    private String urlGetCODetail;
    private TextView tvTotal, tvNama, tvNoBukti;
    private AutoCompleteTextView actvNamaBarang;
    private ListView lvBarang;
    private LinearLayout llLoad;
    private ProgressBar pbLoad;
    private Button btnRefresh;
    private String noBukti = "", namaCus = "";
    private List<CustomListItem> masterList, autocompleteList, tableList;
    private ItemValidation iv = new ItemValidation();
    private Double total = Double.valueOf(0);
    private boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang_canvas);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initUI();
    }

    private void initUI() {

        setTitle("Detail Barang Order Canvas");
        urlGetCODetail = getResources().getString(R.string.url_get_canvas_detail);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvNoBukti = (TextView) findViewById(R.id.tv_nobukti);
        actvNamaBarang = (AutoCompleteTextView) findViewById(R.id.actv_nama_barang);
        lvBarang = (ListView) findViewById(R.id.lv_barang);

        // Progres Bar
        llLoad = (LinearLayout) findViewById(R.id.ll_load);
        pbLoad = (ProgressBar) findViewById(R.id.pb_load);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            noBukti = bundle.getString("nobukti");
            namaCus = bundle.getString("nama");

            tvNama.setText(namaCus);
            tvNoBukti.setText(noBukti);

            getAllData();
            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAllData();
                }
            });
        }
    }

    private void getAllData() {

        JSONObject jsonBody = new JSONObject();
        ApiVolley restService = new ApiVolley(DetailBarangCanvas.this, jsonBody, "GET", urlGetCODetail+noBukti, "", "", 0,
                new ApiVolley.VolleyCallback(){
                    @Override
                    public void onSuccess(String result){
                        JSONObject responseAPI = new JSONObject();

                        try {

                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            masterList = new ArrayList<CustomListItem>();
                            total = Double.valueOf(0);

                            if(iv.parseNullInteger(status) == 200){
                                JSONArray arrayJSON = responseAPI.getJSONArray("response");
                                for(int i = 0; i < arrayJSON.length();i++){
                                    JSONObject jo = arrayJSON.getJSONObject(i);
                                    masterList.add(new CustomListItem(jo.getString("id"),jo.getString("namabrg"), jo.getString("jumlah")+" "+jo.getString("satuan"), iv.ChangeToRupiahFormat(Double.parseDouble(jo.getString("total")))));
                                    total += iv.parseNullDouble(jo.getString("total"));
                                }
                            }

                            tvTotal.setText(iv.ChangeToRupiahFormat(total));
                            autocompleteList = new ArrayList<CustomListItem>(masterList);
                            tableList = new ArrayList<CustomListItem>(masterList);
                            getListBarangAutocomplete(autocompleteList);
                            getListBarangTable(autocompleteList);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");

                        }catch (Exception e){
                            getListBarangAutocomplete(null);
                            getListBarangTable(null);
                            iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"ERROR");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        getListBarangAutocomplete(null);
                        getListBarangTable(null);
                        iv.ProgressbarEvent(llLoad,pbLoad,btnRefresh,"GONE");
                    }
                });
    }


    private void getListBarangAutocomplete(List<CustomListItem> listItems){

        actvNamaBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){
            /*ListBarangDetailSOAutocompleteAdapter arrayAdapterString;

            //set adapter for autocomplete//set adapter for autocomplete
            arrayAdapterString = new ListBarangDetailSOAutocompleteAdapter(DetailSalesOrder.this,listItems.size(),listItems);

            //set adapter to autocomplete
            actvNamaBarang.setAdapter(arrayAdapterString);*/
            setAutocompleteEvent();
        }
    }

    private void setAutocompleteEvent(){

        if(firstLoad){
            firstLoad = false;
            actvNamaBarang.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(actvNamaBarang.getText().length() <= 0){
                        getAllData();
                    }
                }
            });
        }

        actvNamaBarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    List<CustomListItem> items = new ArrayList<CustomListItem>();
                    String keyword = actvNamaBarang.getText().toString().trim().toUpperCase();

                    for(CustomListItem item: masterList){

                        if(item.getListItem2().toUpperCase().contains(keyword)){
                            items.add(item);
                        }
                    }

                    getListBarangTable(items);
                    iv.hideSoftKey(DetailBarangCanvas.this);
                    return true;
                }
                return false;
            }
        });
    }

    private void getListBarangTable( List<CustomListItem> listItems){

        lvBarang.setAdapter(null);

        if (listItems != null && listItems.size() > 0){

            DetailBarangCanvasAdapter arrayAdapterString;

            arrayAdapterString = new DetailBarangCanvasAdapter(DetailBarangCanvas.this, listItems);

            //set adapter to autocomplete
            lvBarang.setAdapter(arrayAdapterString);


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
